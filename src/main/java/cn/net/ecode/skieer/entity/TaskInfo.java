package cn.net.ecode.skieer.entity;

import cn.net.ecode.skieer.config.TaskBaseConfig;

/**
 * 任务结构实体
 * Created by Li Shang Qing on 2017/5/27.
 */
public class TaskInfo {
    private final TaskBaseConfig taskBaseConfig;
    private final Integer pageIndex;
    private final Integer pageSize;
    public TaskInfo(TaskBaseConfig taskBaseConfig,Integer pageIndex,Integer pageSize){
        this.taskBaseConfig=taskBaseConfig;
        this.pageIndex=pageIndex;
        this.pageSize=pageSize;
    }
    public TaskBaseConfig getTaskBaseConfig() {
        return taskBaseConfig;
    }
    public Integer getPageIndex() {
        return pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

}
