package top.yzlin.superrepeater;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.yzlin.cqrobotsdk.HttpAPICode;
import top.yzlin.cqrobotsdk.JSONWebSocketClient;

import java.util.function.Consumer;


@Component
public class SimpleHttpAPI implements InitializingBean {
    private JSONWebSocketClient eventClient;
    private JSONWebSocketClient apiClient;
    private String port;

    @Override
    public void afterPropertiesSet() throws Exception {
        eventClient = new JSONWebSocketClient("ws://0.0.0.0:" + port + "/event/");
        apiClient = new JSONWebSocketClient("ws://0.0.0.0:" + port + "/api/");
        eventClient.connect();
        apiClient.connect();
    }

    @Value("${cqRobot.port}")
    public void setPort(String port) {
        this.port = port;
    }

    public void setOnMessage(Consumer<JSONObject> onMessage) {
        eventClient.setOnMessage(onMessage);
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
            apiClient.send(makeJSON(msgType,
                    new JSONObject()
                            .fluentPut(key, id)
                            .fluentPut("message", msg)).toString());
        }
    }

    protected void destruct() {

    }

    public void lifeAddOneSeconds() {
        if (apiClient.isClosed()) {
            apiClient.reconnect();
        }
        if (eventClient.isClosed()) {
            eventClient.reconnect();
        }
    }

    public void close() {
        destruct();
        apiClient.close();
        eventClient.close();
        apiClient = null;
        eventClient = null;
    }
}
