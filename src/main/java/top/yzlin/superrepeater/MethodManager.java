package top.yzlin.superrepeater;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.yzlin.superrepeater.aop.MethodEventAop;
import top.yzlin.superrepeater.log.LoggerManager;
import top.yzlin.tools.Tools;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Component
public class MethodManager implements Consumer<JSONObject>, DisposableBean, InitializingBean {
    private LoggerManager loggerManager;
    private Map<String, MethodEvent> eventMap = new HashMap<>();
    private long groupID;


    @Value("${user.groupID}")
    public void setGroupID(long groupID) {
        this.groupID = groupID;
    }

    @Autowired
    public void setLoggerManager(LoggerManager loggerManager) {
        this.loggerManager = loggerManager;
    }

    @Override
    public void accept(JSONObject jsonObject) {
        if (jsonObject.getLongValue("group_id") == groupID) {
            eventMap.forEach((k, v) -> {
                if (v.check(jsonObject)) {
                    v.operate(jsonObject);
                    //防止被腾讯封号
                    Tools.sleep(500);
                }
            });
        }

    }

    @Autowired
    @Qualifier("methodEventMap")
    public void setEventMap(Map<String, MethodEvent> eventMap) {
        this.eventMap = eventMap;
    }


    public void addAllEvent(Map<String, MethodEvent> methodEvents) {
        methodEvents.forEach(this::addEvent);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, MethodEvent> m = eventMap;
        eventMap = new HashMap<>(m.size());
        addAllEvent(m);
    }

    public MethodEvent addEvent(String name, MethodEvent methodEvent) {
        MethodEventAop m = new MethodEventAop(methodEvent);
        m.setLoggerManager(loggerManager);
        return eventMap.put(name, m);
    }

    public MethodEvent removeEvent(String name) {
        return eventMap.remove(name);
    }

    @Override
    public void destroy() throws Exception {
        eventMap.clear();
        eventMap = null;
    }


}
