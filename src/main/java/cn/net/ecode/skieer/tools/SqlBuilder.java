package cn.net.ecode.skieer.tools;

import cn.net.ecode.skieer.config.TaskBaseConfig;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.Map;

/**
 * Insert SQL生成器
 * Created by Li Shang Qing on 2017/5/26.
 */
public class SqlBuilder {
    public static String buildInsert(TaskBaseConfig baseInfo, Map<String, String> data) {
        String tableName = baseInfo.getTableName();
        String id = Convertor.genId();
        StringBuilder columnBuilder = new StringBuilder().append("INSERT INTO ").append(tableName).append("(").append("id").append(",");
        StringBuilder argsBuilder = new StringBuilder().append("(").append(id).append(",");
        for (Map.Entry<String, String> entry : data.entrySet()) {
            String dataKey = entry.getKey();
            String column = baseInfo.getColumn().get(dataKey);
            if (existThis(column)) {
                columnBuilder.append(column).append(",");
                argsBuilder.append("'").append(StringEscapeUtils.escapeSql(entry.getValue())).append("'").append(",");
            }
        }
        columnBuilder.deleteCharAt(columnBuilder.lastIndexOf(",")).append(")").append("VALUES");
        argsBuilder.deleteCharAt(argsBuilder.lastIndexOf(",")).append(")");
        return columnBuilder.append(argsBuilder.toString()).toString();
    }

    private static boolean existThis(String column){
        return  column==null?false:true;
    }
}