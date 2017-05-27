package cn.net.ecode.skieer.entity;

import cn.net.ecode.skieer.config.TaskBaseConfig;

/**
 * Created by Li Shang Qing on 2017/5/25.
 */
public class ResultData {
    private Data data;
    private String error;
    private TaskBaseConfig taskBaseConfig;


    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public TaskBaseConfig getTaskBaseConfig() {
        return taskBaseConfig;
    }

    public void setTaskBaseConfig(TaskBaseConfig taskBaseConfig) {
        this.taskBaseConfig = taskBaseConfig;
    }
}
