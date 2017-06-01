package cn.net.ecode.skieer.tools;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.Calendar;
import java.util.Date;
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

    public static Date getEveryDayTimerDate(int hour, int min , int second ){
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MARCH);
        int day=calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH);
        calendar.set(year,month,day,hour,min,second);

        return  calendar.getTime();
    }
}
