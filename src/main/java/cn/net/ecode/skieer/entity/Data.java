package cn.net.ecode.skieer.entity;

import java.util.Map;

/**
 * Created by Li Shang Qing on 2017/5/25.
 */
public class Data {
    private Integer total;
    private Integer currentTotal;
    private Map<String,String> [] dataList;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getCurrentTotal() {
        return currentTotal;
    }

    public void setCurrentTotal(Integer currentTotal) {
        this.currentTotal = currentTotal;
    }

    public Map<String, String>[] getDataList() {
        return dataList;
    }

    public void setDataList(Map<String, String>[] dataList) {
        this.dataList = dataList;
    }
}
