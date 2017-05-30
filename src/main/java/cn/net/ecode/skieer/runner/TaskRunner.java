package cn.net.ecode.skieer.runner;

import cn.net.ecode.skieer.config.JSONConfig;
import cn.net.ecode.skieer.config.TaskBaseConfig;
import cn.net.ecode.skieer.conn.DBConn;
import cn.net.ecode.skieer.conn.SkieerConn;
import cn.net.ecode.skieer.constant.Constant;
import cn.net.ecode.skieer.entity.ResultData;
import cn.net.ecode.skieer.entity.TaskInfo;
import cn.net.ecode.skieer.gui.MsgObservable;
import cn.net.ecode.skieer.gui.MsgObserver;
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
public class TaskRunner implements MsgObservable{
    private BlockingQueue<TaskInfo> taskQueue = new LinkedBlockingQueue<TaskInfo>(Constant.MAX_QUEUE_SIZE.getValue());//采集任务队列
    private BlockingQueue<ResultData> retDataQueue = new LinkedBlockingQueue<ResultData>(Constant.MAX_QUEUE_SIZE.getValue());//返回结果队列
    private BlockingQueue<String> sqlQueue = new LinkedBlockingQueue<String>();//sql语句队列
    private ExecutorService fetcherRunner=Executors.newFixedThreadPool(Constant.MAX_FETCHER_NUM.getValue());
    private ExecutorService runner = Executors.newCachedThreadPool();
    Logger logger = LoggerFactory.getLogger(TaskRunner.class);
   private MsgObserver msgObserver;

    public void registeObserver(MsgObserver msgObserver) {
            this.msgObserver=msgObserver;
    }
    public void notice(String msg){
        if(msgObserver!=null){
            msgObserver.update(msg);
        }
    }
    public void runTask() {
        notice("start Task...");
        if (("SINGLE").equals(JSONConfig.getInstance().getModel())) {
            startSingleFetcher();
            System.out.println("SINGLE");
        } else {
            startDualFetcher();
            System.out.println("DUAL");
        }
        //     startParser();
        //     startSaver();
    }

    private void startSingleFetcher() {
        for (TaskBaseConfig taskBaseConfig : JSONConfig.getInstance().getTaskList()) {
            if (!taskBaseConfig.getOpen())
                continue;
            TaskInfo taskInfo = new TaskInfo(taskBaseConfig, Constant.PAGE_INIT_INDEX.getValue(), Constant.PAGE_MAX_SIZE.getValue());
            fetcherRunner.submit(new FlexibleDataFetcher(taskInfo, 1));
        }
    }

    private void startDualFetcher() {
        for (TaskBaseConfig taskBaseConfig : JSONConfig.getInstance().getTaskList()) {
            if (!taskBaseConfig.getOpen())
                continue;
            TaskInfo oddtaskInfo = new TaskInfo(taskBaseConfig, Constant.PAGE_INIT_INDEX_ODD.getValue(), Constant.PAGE_MAX_SIZE.getValue());
            fetcherRunner.submit(new FlexibleDataFetcher(oddtaskInfo, 2));
            TaskInfo eventtaskInfo = new TaskInfo(taskBaseConfig, Constant.PAGE_INIT_INDEX_EVEN.getValue(), Constant.PAGE_MAX_SIZE.getValue());
            fetcherRunner.submit(new FlexibleDataFetcher(eventtaskInfo, 2));
        }
    }

    /**
     * 八爪鱼一个任务只支持2个线程同时从网上取数据,所以分割任务多线程方案取消
     */
    @Deprecated
    private void startDispatcherAndFetcher() {
        for (TaskBaseConfig taskBaseConfig : JSONConfig.getInstance().getTaskList()) {
            TaskInfo taskInfo = new TaskInfo(taskBaseConfig, Constant.PAGE_INIT_INDEX.getValue(), Constant.DATA_DETECT_PAGE_SIZE.getValue());
            runner.submit(new TaskDispatcher(taskInfo));
        }
        for (int i = 0; i < Constant.MAX_FETCHER_NUM.getValue(); i++) {
            runner.submit(new DataFetcher());
        }
    }

    private void startParser() {
        for (int i = 0; i < JSONConfig.getInstance().getDataParserNum(); i++) {
            runner.submit(new DataParser());
        }
    }

    private void startSaver() {
        for (int i = 0; i < JSONConfig.getInstance().getDataParserNum(); i++) {
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
            this.taskInfo = taskInfo;
        }

        public void run() {
            String url = SkieerConn.buildDataUrl(taskInfo);
            ResultData retData = SkieerConn.startup(taskInfo.getTaskBaseConfig(), url);
            Integer total = retData.getData().getTotal();
            Integer pageNum = (int) Math.ceil((double) total / (double) Constant.PAGE_MAX_SIZE.getValue());
            for (int i = 1; i <= pageNum; i++) {
                try {
                    taskQueue.put(new TaskInfo(taskInfo.getTaskBaseConfig(), i, Constant.PAGE_MAX_SIZE.getValue()));
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
        protected volatile boolean canCancel = false;

        public DataFetcher() {
        }

        public DataFetcher(TaskInfo taskInfo) {
            this(taskInfo.getTaskBaseConfig(), taskInfo.getPageIndex(), taskInfo.getPageSize());
        }

        public DataFetcher(TaskBaseConfig taskBaseConfig, Integer pageIndex, Integer pageSize) {
            this.pageIndex = pageIndex;
            this.pageSize = pageSize;
            this.taskBaseConfig = taskBaseConfig;
        }

        public void run() {
            while (true) {
                TaskInfo taskInfo = null;
                try {
                    taskInfo = taskQueue.take();
                    String url = SkieerConn.buildDataUrl(taskInfo);
                    ResultData retData = SkieerConn.startup(taskInfo.getTaskBaseConfig(), url);
                    System.out.println(Thread.currentThread().getName() + retData.getTaskBaseConfig().getTaskName() + ":" + taskInfo.getPageIndex());
                    retDataQueue.put(retData);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 可变的数据采集器,
     * 通过更改pageIndex每次递增值配合建立的线程数,实现一个任务多个线程共同执行
     * 如pageIndexIncrement为2 ,开2个线程,一个线程初始PageIndex为1,另一个线程pageIndex为2,
     * 那么这2个线程就一个采集奇数页,一个采集偶数页,相互不影响
     * 经过测试,八爪鱼一个任务最多支持2个线程 ,所以pageIndexIncrement 最大取值为2
     */
    class FlexibleDataFetcher extends DataFetcher {
        private Integer pageIndexIncrement;

        public FlexibleDataFetcher(TaskInfo taskInfo, Integer pageIndexIncrement) {
            super(taskInfo);
            this.pageIndexIncrement = pageIndexIncrement;
        }

        public void run() {
            while (!canCancel) {
                String url = SkieerConn.buildDataUrl(taskBaseConfig.getTaskId(), pageIndex, pageSize);
                ResultData retData = SkieerConn.startup(taskBaseConfig, url);
                if (haveNotExportedData(retData)) {
                    increasePageIndex((pageIndexIncrement));
                } else {
                    canCancel = true;
                }
                try {
                    retDataQueue.put(retData);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("finished:" + taskBaseConfig.getTaskName());
            notice("finished:" + taskBaseConfig.getTaskName());
        }

        private boolean haveNotExportedData(ResultData data) {
            Integer total = data.getData().getTotal();
            Integer maxsize = (int) Math.ceil((double) total / (double) pageSize);
            System.out.println(Thread.currentThread() + ":" + pageIndex + ":" + maxsize);
            notice(Thread.currentThread() + ":" + pageIndex + ":" + maxsize);
            return pageIndex <= maxsize ? true : false;
        }

        private void increasePageIndex(int increment) {
            this.pageIndex = pageIndex + increment;
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

