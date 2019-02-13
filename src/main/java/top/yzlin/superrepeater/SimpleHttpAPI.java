package top.yzlin.superrepeater;

import com.alibaba.fastjson.JSONObject;
import top.yzlin.cqrobotsdk.HttpAPICode;
import top.yzlin.cqrobotsdk.JSONWebSocketClient;

import java.util.function.Consumer;


public class SimpleHttpAPI {
    private JSONWebSocketClient client;


    public SimpleHttpAPI(int port) {
        this(Integer.toString(port));
    }

    public SimpleHttpAPI(String port) {
        this("ws://0.0.0.0", port);
    }

    public SimpleHttpAPI(String wsPath, String port) {
        client = new JSONWebSocketClient(wsPath + ':' + port + "/api/");
        client.connect();
    }

    public void setOnMessage(Consumer<JSONObject> onMessage) {
        client.setOnMessage(onMessage);
    }

    private JSONObject makeJSON(String action, JSONObject params) {
        return new JSONObject()
                .fluentPut("action", action)
                .fluentPut("params", params);
    }

    public void sendPersonMsg(String qqID, String msg) {
        sendMsg(qqID, msg, HttpAPICode.SEND_PERSON_MSG, "user_id");
    }

    public void sendGroupMsg(String groupID, String msg) {
        sendMsg(groupID, msg, HttpAPICode.SEND_GROUP_MSG, "group_id");
    }

    public void sendDiscussMsg(String discussID, String msg) {
        sendMsg(discussID, msg, HttpAPICode.SEND_DISCUSS_MSG, "discuss_id");
    }

    private void sendMsg(String id, String msg, String msgType, String key) {
        if (id != null && msg != null) {
            client.send(makeJSON(msgType,
                    new JSONObject()
                            .fluentPut(key, id)
                            .fluentPut("message", msg)).toString());
        }
    }

    protected void destruct() {

    }

    public void close() {
        destruct();
        client.close();
        client = null;
    }
}
