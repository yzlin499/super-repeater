package top.yzlin.superrepeater.javaparse;

import com.alibaba.fastjson.JSONObject;
import top.yzlin.superrepeater.BaseMethodEvent;
import top.yzlin.superrepeater.SimpleHttpAPI;
import top.yzlin.superrepeater.log.LogOperate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class JavaMethodEvent extends BaseMethodEvent {
    private String groupID;
    private Object instance;
    private LogOperate logOperate;
    private SimpleHttpAPI simpleHttpAPI;
    private Class clazz;

    public JavaMethodEvent(Class clazz,
                           String groupID,
                           LogOperate logOperate,
                           SimpleHttpAPI simpleHttpAPI) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        this.groupID = groupID;
        this.logOperate = logOperate;
        this.simpleHttpAPI = simpleHttpAPI;
        this.clazz = clazz;
        this.instance = clazz.newInstance();

        setProperty("setGroupID", String.class, groupID);
        setProperty("setLogOperate", Consumer.class, logOperate::log);
        setProperty("setSendGroupMsg", BiConsumer.class, (g, m) -> simpleHttpAPI.sendGroupMsg(g.toString(), m.toString()));
        setProperty("setSendDiscussMsg", BiConsumer.class, (g, m) -> simpleHttpAPI.sendDiscussMsg(g.toString(), m.toString()));
        setProperty("setSendPersonMsg", BiConsumer.class, (g, m) -> simpleHttpAPI.sendPersonMsg(g.toString(), m.toString()));
        init(clazz);
    }

    private void init(Class clazz) throws InstantiationException {
        try {
            setCheckFunction(makeCheck(clazz.getMethod("check"), false));
        } catch (NoSuchMethodException e) {
            try {
                setCheckFunction(makeCheck(clazz.getMethod("check", Map.class), true));
            } catch (NoSuchMethodException e1) {
                throw new InstantiationException("check函数不存在，无法使用");
            }
        }
        try {
            setOperateFunction(makeOperate(clazz.getMethod("operate"), false));
        } catch (NoSuchMethodException e) {
            try {
                setOperateFunction(makeOperate(clazz.getMethod("operate", Map.class), true));
            } catch (NoSuchMethodException e1) {
                throw new InstantiationException("operate函数不存在，无法使用");
            }
        }
    }

    private <T> void setProperty(String name, Class<T> paramClass, T o) throws InvocationTargetException, IllegalAccessException {
        try {
            clazz.getMethod(name, paramClass).invoke(instance, o);
        } catch (NoSuchMethodException ignored) {
        }
    }

    private Predicate<JSONObject> makeCheck(Method check, boolean isArg) throws InstantiationException {
        Class returnType = check.getReturnType();
        if (String.class.equals(returnType)) {
            return m -> {
                try {
                    return m.getString("message").equals(check.invoke(instance, isArg ? m : new Object[0]));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logOperate.log(e);
                    return false;
                }
            };
        } else if (boolean.class.equals(returnType) || Boolean.class.equals(returnType)) {
            return m -> {
                try {
                    return (boolean) check.invoke(instance, isArg ? m : new Object[0]);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logOperate.log(e);
                    return false;
                }
            };
        } else {
            throw new InstantiationException("check函数不规范");
        }
    }

    private Consumer<JSONObject> makeOperate(Method check, boolean isArg) throws InstantiationException {
        Class returnType = check.getReturnType();
        if (void.class.equals(returnType)) {
            return m -> {
                try {
                    check.invoke(instance, isArg ? m : new Object[0]);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logOperate.log(e);
                }
            };
        } else {
            return m -> {
                try {
                    sendGroupMsg(groupID, Objects.toString(check.invoke(instance, isArg ? m : new Object[0]).toString()));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logOperate.log(e);
                }
            };
        }
    }

    private void sendGroupMsg(String groupID, String msg) {
        simpleHttpAPI.sendGroupMsg(groupID, msg);
    }
}
