package top.yzlin.superrepeater;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;
import top.yzlin.tools.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
public class MethodManager implements Consumer<JSONObject>, DisposableBean {
    private List<MethodEvent> eventList = new ArrayList<>();

    @Override
    public void accept(JSONObject jsonObject) {
        eventList.forEach(e -> {
            if (e.check(jsonObject)) {
                e.operate(jsonObject);
                Tools.sleep(500);
            }
        });
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
