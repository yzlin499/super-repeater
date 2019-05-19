package top.yzlin.superrepeater.jsparse;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.objects.NativeRegExp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.yzlin.superrepeater.LanguageParse;
import top.yzlin.superrepeater.MethodEvent;
import top.yzlin.superrepeater.SimpleHttpAPI;
import top.yzlin.superrepeater.log.LogOperate;
import top.yzlin.superrepeater.log.LoggerManager;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component("jsParse")
public class JSParse implements LanguageParse {
    private ScriptEngineManager manager = new ScriptEngineManager();
    private SimpleHttpAPI simpleHttpAPI;
    private String groupID;
    private LoggerManager loggerManager;
    private JSTools jsTools;

    @Override
    @Autowired
    public void setLoggerManager(LoggerManager loggerManager) {
        this.loggerManager = loggerManager;
    }

    @Autowired
    public void setSimpleHttpAPI(SimpleHttpAPI simpleHttpAPI) {
        this.simpleHttpAPI = simpleHttpAPI;
    }

    @Autowired
    public void setJsTools(JSTools jsTools) {
        this.jsTools = jsTools;
    }

    @Override
    @Value("${user.groupID}")
    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    @Override
    public MethodEvent parse(File file) throws FileNotFoundException, UnsupportedEncodingException {
        //实例化一个操作方法
        JSMethodEvent jsMethodEvent = new JSMethodEvent();
        jsMethodEvent.setName(file.getName());
        ScriptEngine engine = manager.getEngineByName("javascript");

        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
        try {
            engine.eval(isr);
        } catch (Exception e) {
            LogOperate logOperate = loggerManager.getLogOperate(file.getName());
            StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter));
            logOperate.log(stringWriter);
            return null;
        }
        //获取最外层的赞美棒哥节点
        ScriptObjectMirror s = (ScriptObjectMirror) engine.get("praiseBang");
        if (s == null) {
            return null;
        }

        Object priority = s.get("priority");
        if (priority != null) {
            if (priority instanceof Number) {
                jsMethodEvent.setPriority(((Number) priority).intValue());
            } else if (priority instanceof String) {
                try {
                    jsMethodEvent.setPriority(Integer.parseInt(String.valueOf(priority)));
                } catch (NumberFormatException ignored) {
                }
            }
        }

        //注入机器人的操作函数
        injectionProperties(s, file.getName());

        //获取检查节点
        Object check = s.get("check");

        //为方法设置检查方法
        //如果值为数字或者字符串则进行值对比，如果为数组则判断数组的值，如果为函数着将函数的返回结果作为判断
        if (check instanceof Number || check instanceof String) {
            jsMethodEvent.setCheckFunction(j -> Objects.equals(check, j.getString("message")));
        } else if (check instanceof ScriptObjectMirror) {
            ScriptObjectMirror mirror = (ScriptObjectMirror) check;
            if (mirror.isArray()) {
                Set<String> keySet = mirror.values().stream().map(Object::toString).collect(Collectors.toSet());
                jsMethodEvent.setCheckFunction(j -> keySet.contains(j.getString("message")));
            } else if (mirror.isFunction()) {
                jsMethodEvent.setCheckFunction(j -> {
                    Object result = mirror.call(null, j);
                    return Boolean.TRUE.equals(result) || result instanceof Number && (!Integer.valueOf(0).equals(result));
                });
            } else {
                //判断是否可变为正则表达式
                try {
                    NativeRegExp nativeRegExp = mirror.to(NativeRegExp.class);
                    jsMethodEvent.setCheckFunction(j -> nativeRegExp.test(j.getString("message")));
                } catch (ClassCastException e) {
                    return null;
                }
            }
        } else {
            return null;
        }

        //操作
        Object operate = s.get("operate");
        //如果操作的值为数字或者字符串则返回该值
        if (operate instanceof Number || operate instanceof String) {
            jsMethodEvent.setOperateFunction(j -> simpleHttpAPI.sendGroupMsg(groupID, operate.toString()));
        } else if (operate instanceof ScriptObjectMirror) {
            ScriptObjectMirror mirror = (ScriptObjectMirror) operate;
            if (mirror.isFunction()) {
                jsMethodEvent.setOperateFunction(j -> {
                    Object result = mirror.call(null, j);
                    if (result instanceof Number || result instanceof String) {
                        simpleHttpAPI.sendGroupMsg(groupID, result.toString());
                    }
                });
            } else {
                return null;
            }
        } else {
            return null;
        }
        return jsMethodEvent;
    }

    private void injectionProperties(ScriptObjectMirror s, String fileName) {
        LogOperate logOperate = loggerManager.getLogOperate(fileName);
        s.put("robot", simpleHttpAPI);
        s.put("groupID", groupID);
        s.put("log", (Consumer) logOperate::log);
        s.put("tools", jsTools);
    }
}
