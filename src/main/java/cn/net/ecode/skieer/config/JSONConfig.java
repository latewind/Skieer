package cn.net.ecode.skieer.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

/**
 * 全局配置表config.json
 * Created by Li Shang Qing on 2017/5/25.
 */
public class JSONConfig {

    private  String dbUserName;
    private  String dbPassWord;
    private  String dbDriverName;
    private  String dbUrl;
    private  String username;
    private String password;
    private String grantType;
    private String tokenUrl;
    private String dataApiUrl;
    private String appendDataApiUrl;
    private TaskBaseConfig[] taskList;
    private String model;
    private Integer dataFetcherNum;
    private Integer dataParserNum;
    private Integer dataSaverNum;
    private static JSONConfig config;
    static {
        reloadConfig();
    }
    public static  void reloadConfig(){
        Reader reader = null;
        try {
            reader = new InputStreamReader(JSONConfig.class.getResourceAsStream("/config.json"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Gson gson = new GsonBuilder().create();
        config = gson.fromJson(reader, JSONConfig.class);

    }

    public static JSONConfig getInstance(){

        return config;
    }

    public static void main(String [] args){

        System.out.println(JSONConfig.getInstance().getUsername());

    }
    private JSONConfig(){}

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getTokenUrl() {
        return tokenUrl;
    }

    public void setTokenUrl(String tokenUrl) {
        this.tokenUrl = tokenUrl;
    }

    public String getDataApiUrl() {
        return dataApiUrl;
    }

    public void setDataApiUrl(String dataApiUrl) {
        this.dataApiUrl = dataApiUrl;
    }

    public TaskBaseConfig[] getTaskList() {
        return taskList;
    }

    public void setTaskList(TaskBaseConfig[] taskList) {
        this.taskList = taskList;
    }

    public String getDbUserName() {
        return dbUserName;
    }

    public void setDbUserName(String dbUserName) {
        this.dbUserName = dbUserName;
    }

    public String getDbPassWord() {
        return dbPassWord;
    }

    public void setDbPassWord(String dbPassWord) {
        this.dbPassWord = dbPassWord;
    }

    public String getDbDriverName() {
        return dbDriverName;
    }

    public void setDbDriverName(String dbDriverName) {
        this.dbDriverName = dbDriverName;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }


    public Integer getDataFetcherNum() {
        return dataFetcherNum;
    }

    public void setDataFetcherNum(Integer dataFetcherNum) {
        this.dataFetcherNum = dataFetcherNum;
    }

    public Integer getDataParserNum() {
        return dataParserNum;
    }

    public void setDataParserNum(Integer dataParserNum) {
        this.dataParserNum = dataParserNum;
    }

    public Integer getDataSaverNum() {
        return dataSaverNum;
    }

    public void setDataSaverNum(Integer dataSaverNum) {
        this.dataSaverNum = dataSaverNum;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getAppendDataApiUrl() {
        return appendDataApiUrl;
    }

    public void setAppendDataApiUrl(String appendDataApiUrl) {
        this.appendDataApiUrl = appendDataApiUrl;
    }
}
