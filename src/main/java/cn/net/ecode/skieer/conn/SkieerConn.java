package cn.net.ecode.skieer.conn;

import java.io.IOException;

import cn.net.ecode.skieer.config.JSONConfig;
import cn.net.ecode.skieer.config.TaskBaseConfig;
import cn.net.ecode.skieer.entity.ResultData;
import cn.net.ecode.skieer.entity.TaskInfo;
import cn.net.ecode.skieer.entity.Token;
import cn.net.ecode.skieer.exceptions.SkieerHttpResponseException;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

/**
 *
 */
public class SkieerConn extends HttpConn {
    private static Token token=loadToken();
	public static Token loadToken(){
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
        NameValuePair[] args = buildTokenArgs();
        String result = null;
        try {
            result = submitForm(JSONConfig.getInstance().getTokenUrl(), args);
        } catch (IOException e) {
            e.printStackTrace();
        }

        token = gson.fromJson(result, Token.class);
        return token;
    }

	//获取八爪鱼Token
	public static Token getToken() throws JSONException, IOException {

		return token;
	}
	private static NameValuePair[]  buildTokenArgs(){
        NameValuePair[] args = {
                new BasicNameValuePair("username", JSONConfig.getInstance().getUsername()),
                new BasicNameValuePair("password", JSONConfig.getInstance().getPassword()),
                new BasicNameValuePair("grant_type", JSONConfig.getInstance().getGrantType())};
        return  args;
    }
	public static String buildExportAllDataUrl(TaskInfo taskInfo) {
		return buildExportAllDataUrl(taskInfo.getTaskBaseConfig().getTaskId(),taskInfo.getPageIndex(),taskInfo.getPageSize());
	}
	public static String buildExportAllDataUrl(String taskId, Integer pageIndex, Integer pageSize) {
		StringBuilder builder = new StringBuilder(JSONConfig.getInstance().getDataApiUrl());
		builder.append("?taskid=" + taskId);
		builder.append("&pageindex=" + pageIndex);
		builder.append("&pagesize=" + pageSize);
		return builder.toString();
	}
	public static String buildAppendDataUrl(String taskId, Integer size) {
		StringBuilder builder = new StringBuilder(JSONConfig.getInstance().getAppendDataApiUrl());
		builder.append("?taskid=" + taskId);
		builder.append("&size=" + size);
		return builder.toString();
	}
	public static  ResultData startup(TaskBaseConfig taskBaseConfig, String url) throws SkieerHttpResponseException {
		try {
			SkieerConn conn=new SkieerConn();
			Token token = conn.getToken();
			String result=conn.getData(url, token);
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

    @Override
    protected void buildRequestHeader(HttpRequestBase httpRequestBase) {
        httpRequestBase.setHeader("Accept", "application/json");
        httpRequestBase.setHeader("Authorization", "bearer " + token);
	    super.buildRequestHeader(httpRequestBase);
    }

    public static void main(String[] args) {
        try {
            new   SkieerConn().getToken();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
