package cn.net.ecode.skieer.tools;

import cn.net.ecode.skieer.config.TaskBaseConfig;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.Map;

/**
 * Insert SQL生成器
 * Created by Li Shang Qing on 2017/5/26.
 */
public class SqlBuilder{
    public static String  buildInsert(TaskBaseConfig baseInfo, Map<String, String> data){
        StringBuilder columnBuilder=new StringBuilder("INSERT INTO "+baseInfo.getTableName()+"(id,");
        StringBuilder argsBuilder=new StringBuilder("('"+Convertor.genId()+"',");
        for(Map.Entry<String,String> entry:data.entrySet()){
            String dataKey= entry.getKey();
            String column=baseInfo.getColumn().get(dataKey);
            if(column!=null) {
                columnBuilder.append(column + ",");
                argsBuilder.append("'").append( StringEscapeUtils.escapeSql(entry.getValue())).append("'").append(",");
            }
        }
        columnBuilder.deleteCharAt(columnBuilder.lastIndexOf(",")).append(")VALUES");
        argsBuilder.deleteCharAt(argsBuilder.lastIndexOf(",")).append(")");
        return columnBuilder.toString()+argsBuilder.toString();
    }

}