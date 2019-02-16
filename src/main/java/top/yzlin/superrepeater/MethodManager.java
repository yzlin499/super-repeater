package top.yzlin.superrepeater;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.yzlin.tools.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
public class MethodManager implements Consumer<JSONObject>, DisposableBean {
    private List<MethodEvent> eventList = new ArrayList<>();
    private long groupID;

    @Value("${user.groupID}")
    public void setGroupID(long groupID) {
        this.groupID = groupID;
    }

    @Override
    public void accept(JSONObject jsonObject) {
        if (jsonObject.getLongValue("group_id") == groupID) {
            eventList.forEach(e -> {
                if (e.check(jsonObject)) {
                    e.operate(jsonObject);
                    Tools.sleep(500);
                }
            });
        }

    }

    @Autowired
    @Qualifier("methodEventList")
    public boolean addAllEvent(List<MethodEvent> methodEvents) {
        return eventList.addAll(methodEvents);
    }

    public boolean addEvent(MethodEvent methodEvent) {
        return eventList.add(methodEvent);
    }

    public boolean removeEvent(MethodEvent methodEvent) {
        return eventList.remove(methodEvent);
    }

    @Override
    public void destroy() throws Exception {
        eventList.clear();
        eventList = null;
    }
}
