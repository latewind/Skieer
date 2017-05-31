package cn.net.ecode.skieer.tools;

import cn.net.ecode.skieer.config.TaskBaseConfig;
import cn.net.ecode.skieer.constant.Constant;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

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
        StringBuilder argsBuilder = new StringBuilder().append("(").append("'").append(id).append("'").append(",");
        for (Map.Entry<String, String> entry : data.entrySet()) {
            String dataKey = entry.getKey();
            String column = baseInfo.getColumn().get(dataKey);
            if (existThis(column)) {
                columnBuilder.append(column).append(",");
                argsBuilder.append("'").append(cutAndEscapeStr(entry.getValue())).append("'").append(",");
            }
        }
        columnBuilder.deleteCharAt(columnBuilder.lastIndexOf(",")).append(")").append("VALUES");
        argsBuilder.deleteCharAt(argsBuilder.lastIndexOf(",")).append(")");
        return columnBuilder.append(argsBuilder.toString()).toString();
    }

    private static String cutAndEscapeStr(String sourceStr){
        String shortString=StringUtils.left(sourceStr, Constant.MAX_COLUMN_LEN.getValue());
        String exceptString=StringEscapeUtils.escapeSql(shortString);
        return exceptString;
    }
    private static boolean existThis(String column){
        return  column==null?false:true;
    }
}