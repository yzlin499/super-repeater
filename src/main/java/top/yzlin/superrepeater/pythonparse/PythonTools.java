package top.yzlin.superrepeater.pythonparse;

import top.yzlin.superrepeater.SimpleHttpAPI;
import top.yzlin.superrepeater.database.DBManager;
import top.yzlin.superrepeater.log.LogOperate;

public class PythonTools {
    public String groupID;
    private LogOperate logOperate;
    private SimpleHttpAPI simpleHttpAPI;
    private DBManager dbManager;

    public PythonTools(String groupID, LogOperate logOperate, SimpleHttpAPI simpleHttpAPI, DBManager dbManager) {
        this.groupID = groupID;
        this.logOperate = logOperate;
        this.simpleHttpAPI = simpleHttpAPI;
//        this.dbManager = dbManager;
    }

    public String getGroupID() {
        return groupID;
    }

    public void log(Object text) {
        logOperate.log(text);
    }

    public void sendPersonMsg(String qqID, String msg) {
        simpleHttpAPI.sendPersonMsg(qqID, msg);
    }

    public void sendGroupMsg(String groupID, String msg) {
        simpleHttpAPI.sendGroupMsg(groupID, msg);
    }

    public void sendDiscussMsg(String discussID, String msg) {
        simpleHttpAPI.sendDiscussMsg(discussID, msg);
    }


}
