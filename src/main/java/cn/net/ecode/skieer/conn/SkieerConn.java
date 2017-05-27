package cn.net.ecode.skieer.conn;

import java.io.IOException;

import cn.net.ecode.skieer.config.JSONConfig;
import cn.net.ecode.skieer.config.TaskBaseConfig;
import cn.net.ecode.skieer.entity.ResultData;
import cn.net.ecode.skieer.entity.TaskInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public class SkieerConn extends HttpConn {
    private static String token;

	//获取八爪鱼Token
	public String getToken() throws JSONException, IOException {
		NameValuePair[] args = buildTokenArgs();
        String result = submitForm(JSONConfig.getInstance().getTokenUrl(), args);
		JSONObject json= new JSONObject(result);
		String token = (String) json.get("access_token");
		return token;
	}
	private NameValuePair[]  buildTokenArgs(){
        NameValuePair[] args = {
                new BasicNameValuePair("username", JSONConfig.getInstance().getUsername()),
                new BasicNameValuePair("password", JSONConfig.getInstance().getPassword()),
                new BasicNameValuePair("grant_type", JSONConfig.getInstance().getGrantType())};
        return  args;
    }

	public static String buildDataUrl(TaskInfo taskInfo) {
		return buildDataUrl(taskInfo.getTaskBaseConfig().getTaskId(),taskInfo.getPageIndex(),taskInfo.getPageSize());
	}
	public static String buildDataUrl(String taskId, Integer pageIndex, Integer pageSize) {
		StringBuilder builder = new StringBuilder(JSONConfig.getInstance().getDataApiUrl());
		builder.append("?taskid=" + taskId);
		builder.append("&pageindex=" + pageIndex);
		builder.append("&pagesize=" + pageSize);
		return builder.toString();
	}
	public static  ResultData startup(TaskBaseConfig taskBaseConfig, String url){
		try {
			SkieerConn conn=new SkieerConn();
			String token = conn.getToken();
			String result=conn.get(url, token);
            Gson gson = new GsonBuilder().create();
            ResultData retData = gson.fromJson(result, ResultData.class);
			retData.setTaskBaseConfig(taskBaseConfig);
            return retData;
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return  new ResultData();
	}

	public static void main(String[] args) {
	}
}
