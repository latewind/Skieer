package cn.net.ecode.skieer.runner;

import cn.net.ecode.skieer.config.JSONConfig;
import cn.net.ecode.skieer.config.TaskBaseConfig;
import cn.net.ecode.skieer.conn.DBConn;
import cn.net.ecode.skieer.conn.SkieerConn;
import cn.net.ecode.skieer.constant.Constant;
import cn.net.ecode.skieer.entity.ResultData;
import cn.net.ecode.skieer.entity.TaskInfo;
import cn.net.ecode.skieer.tools.SqlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Li Shang Qing on 2017/5/25.
 */
public class TaskRunner {
    private BlockingQueue<TaskInfo> taskQueue = new LinkedBlockingQueue<TaskInfo>(Constant.MAX_QUEUE_SIZE.getValue());//采集任务队列
    private BlockingQueue<ResultData> retDataQueue = new LinkedBlockingQueue<ResultData>(Constant.MAX_QUEUE_SIZE.getValue());//返回结果队列
    private BlockingQueue<String> sqlQueue = new LinkedBlockingQueue<String>();//sql语句队列
    private ExecutorService runner = Executors.newCachedThreadPool();
    Logger logger = LoggerFactory.getLogger(TaskRunner.class);
    public void runTask() {
        if(JSONConfig.getInstance().getModel()=="APPEND"){
            startFetcher();
        }else {
            startDispatcherAndFetcher();
        }
   //     startParser();
   //     startSaver();

    }


    private void startFetcher() {
        for (TaskBaseConfig taskBaseConfig : JSONConfig.getInstance().getTaskList()) {
            TaskInfo taskInfo=new TaskInfo(taskBaseConfig, Constant.PAGE_INIT_INDEX.getValue(),Constant.DATA_DETECT_PAGE_SIZE.getValue());
            runner.submit(new SingleDataFetcher(taskInfo));
        }
    }
    private void startDispatcherAndFetcher() {
        for (TaskBaseConfig taskBaseConfig : JSONConfig.getInstance().getTaskList()) {
            TaskInfo taskInfo=new TaskInfo(taskBaseConfig, Constant.PAGE_INIT_INDEX.getValue(),Constant.DATA_DETECT_PAGE_SIZE.getValue());
            runner.submit(new TaskDispatcher(taskInfo));
        }
        for(int i=0;i<JSONConfig.getInstance().getDataFetcherNum();i++){
            runner.submit(new DataFetcher());
        }
    }

    private void startParser(){
        for(int i=0;i<JSONConfig.getInstance().getDataParserNum();i++){
            runner.submit(new DataParser());
        }
    }

    private void startSaver(){
        for(int i=0;i<JSONConfig.getInstance().getDataParserNum();i++){
            runner.submit(new DataParser());
        }
    }


    public static void main(String[] args) {
        new TaskRunner().runTask();
    }
    //任务分发器
    public class TaskDispatcher implements Runnable {
        private final TaskInfo taskInfo;
        public TaskDispatcher(TaskInfo taskInfo) {
            this.taskInfo=taskInfo;
        }
        public void run() {
            String url = SkieerConn.buildDataUrl(taskInfo);
            ResultData retData = SkieerConn.startup(taskInfo.getTaskBaseConfig(), url);
            Integer total=retData.getData().getTotal();
            Integer pageNum=(int)Math.ceil((double)total/(double)Constant.PAGE_SIZE.getValue());
            for(int i=1;i<=pageNum;i++){
                try {
                    taskQueue.put(new TaskInfo(taskInfo.getTaskBaseConfig(),i,Constant.PAGE_SIZE.getValue()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //数据采集器
    class DataFetcher implements Runnable {
        protected Integer pageIndex;
        protected Integer pageSize;
        protected TaskBaseConfig taskBaseConfig;
        protected volatile boolean canCancel=false;
        public DataFetcher(){}
        public DataFetcher(TaskInfo taskInfo){
            this(taskInfo.getTaskBaseConfig(),taskInfo.getPageIndex(),taskInfo.getPageSize());
        }
        public DataFetcher(TaskBaseConfig taskBaseConfig, Integer pageIndex, Integer pageSize) {
            this.pageIndex = pageIndex;
            this.pageSize = pageSize;
            this.taskBaseConfig = taskBaseConfig;
        }
        public void run() {
            while(true){
                TaskInfo taskInfo= null;
                try {
                    taskInfo = taskQueue.take();
                    String url = SkieerConn.buildDataUrl(taskInfo);
                    ResultData retData = SkieerConn.startup(taskInfo.getTaskBaseConfig(), url);
                    System.out.println(retData.getTaskBaseConfig().getTaskName()+":"+taskInfo.getPageIndex());
                    retDataQueue.put(retData);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //数据单程采集器（一个任务，一个采集器）
    class SingleDataFetcher extends DataFetcher{
        public SingleDataFetcher(TaskInfo taskInfo) {
            super(taskInfo);
        }
        public void run() {
            while(!canCancel){
                String url = SkieerConn.buildDataUrl(taskBaseConfig.getTaskId(),pageIndex, pageSize);
                ResultData retData = SkieerConn.startup(taskBaseConfig, url);
                if(haveNotExportedData(retData)){
                    increasePageIndex(1);
                }else{
                    canCancel=true;
                }
                try {
                    retDataQueue.put(retData);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("finished:"+taskBaseConfig.getTaskName());
        }
        private boolean haveNotExportedData(ResultData data){
            Integer total=data.getData().getTotal();
            Integer maxsize= (int)Math.ceil((double)total/(double)pageSize);
            System.out.println(Thread.currentThread()+":"+pageIndex+":"+maxsize);
            return pageIndex<=maxsize? true:false;
        }
        private void increasePageIndex(int increment){
            this.pageIndex=pageIndex+increment;
        }
    }
    //数据解析器
    class DataParser implements Runnable {
        public void run() {
            while (true) {
                ResultData retData = null;
                try {
                    retData = retDataQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (Map<String, String> data : retData.getData().getDataList()) {
                    String sql = SqlBuilder.buildInsert(retData.getTaskBaseConfig(), data);
                    try {
                        sqlQueue.put(sql);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    //数据保存器
    class DataSaver implements Runnable {
        public void run() {
            System.out.println("Data Saver running");
            Connection conn = DBConn.getConnection();
            Statement stmt = null;
            try {
                stmt = conn.createStatement();
                String sql = null;
                while (true) {
                    try {
                        sql = sqlQueue.take();
                        stmt.executeUpdate(sql);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Thread.interrupted();//清除中断状态
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("insert error");
                        logger.warn(e.toString());
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConn.closeAll(conn, stmt, null);
            }
        }
    }
}

