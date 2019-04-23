package top.yzlin.superrepeater;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.yzlin.superrepeater.aop.MethodEventAop;
import top.yzlin.superrepeater.javaparse.JavaClass;
import top.yzlin.superrepeater.javaparse.JavaParse;
import top.yzlin.superrepeater.jsparse.JSFile;
import top.yzlin.superrepeater.jsparse.JSParse;
import top.yzlin.superrepeater.log.LoggerManager;
import top.yzlin.tools.Tools;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class MethodManager implements Consumer<JSONObject>, DisposableBean {
    private LoggerManager loggerManager;
    private Map<String, MethodEvent> eventMap = new HashMap<>();
    private long groupID;
    private JSParse jsParse;
    private JSFile jsFile;
    private JavaParse javaParse;
    private JavaClass javaClass;

    @Autowired
    public void setSimpleHttpAPI(SimpleHttpAPI simpleHttpAPI) {
        simpleHttpAPI.setOnMessage(this);
    }

    @Autowired
    public void setJsParse(JSParse jsParse) {
        this.jsParse = jsParse;
    }

    @Autowired
    public void setJsFile(JSFile jsFile) {
        this.jsFile = jsFile;
    }

    @Autowired
    public void setJavaParse(JavaParse javaParse) {
        this.javaParse = javaParse;
    }

    @Autowired
    public void setJavaClass(JavaClass javaClass) {
        this.javaClass = javaClass;
    }

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


    public void addAllEvent(Map<String, MethodEvent> methodEvents) {
        methodEvents.forEach(this::addEvent);
    }

    @PostConstruct
    public void afterPropertiesSet() {
        Map<String, MethodEvent> eventMap = new HashMap<>();
        eventMap.putAll(Stream.of(jsFile.getFiles())
                .map(f -> {
                    try {
                        return jsParse.parse(f);
                    } catch (FileNotFoundException | UnsupportedEncodingException e) {
                        e.printStackTrace();
                        return null;
                    }
                }).filter(Objects::nonNull)
                .collect(Collectors.toMap(MethodEvent::getName, v -> v)));
        eventMap.putAll(Stream.of(javaClass.getFiles())
                .map(f -> {
                    try {
                        return javaParse.parse(f);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }).filter(Objects::nonNull)
                .collect(Collectors.toMap(MethodEvent::getName, v -> v)));
        this.eventMap = new HashMap<>(eventMap.size());
        System.out.println(eventMap);
        addAllEvent(eventMap);
    }

    public void addEvent(String name, MethodEvent methodEvent) {
        MethodEventAop m = new MethodEventAop(methodEvent);
        m.setLoggerManager(loggerManager);
        MethodEvent event = eventMap.put(name, m);
        if (event != null) {
            event.destroy();
        }
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
