package cn.net.ecode.skieer.exceptions;

/**
 * 调用数据接口Http返回状态异常类
 */
public  class SkieerHttpResponseException extends Exception {
    public SkieerHttpResponseException(String url) {
        super(url);
    }
}