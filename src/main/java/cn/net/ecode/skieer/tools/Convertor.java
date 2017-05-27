package cn.net.ecode.skieer.tools;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.UUID;

/**
 * Created by Li Shang Qing on 2017/5/26.
 */
public class Convertor {
    public  void resplaceAllQuoteAndSpace(){

    }
    public static String genId(){
        String uuid = UUID.randomUUID().toString(); //获取UUID并转化为String对象
        uuid = uuid.replace("-", "");
        return uuid;
    }
}
