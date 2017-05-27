package cn.net.ecode.skieer.config;

import java.util.Map;

/**
 * 任务基本信息（任务名，任务ID，任务对应的数据库表结构与任务段名的映射关系）
 * Created by Li Shang Qing  on 2017/5/25.
 */
public class TaskBaseConfig {
    private String taskName;
    private String taskId;
    private String tableName;
    private Map<String,String> column;

    public TaskBaseConfig(){

    }
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Map<String, String> getColumn() {
        return column;
    }

    public void setColumn(Map<String, String> column) {
        this.column = column;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
