package top.yzlin.superrepeater.javaparse;

import com.alibaba.fastjson.JSONObject;
import top.yzlin.superrepeater.BaseMethodEvent;
import top.yzlin.superrepeater.SimpleHttpAPI;
import top.yzlin.superrepeater.log.LogOperate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        init();
    }

    private void init() throws InstantiationException {
        Map<String, Method> methodMap = Stream.of(clazz.getMethods())
                .collect(Collectors.toMap(Method::getName, v -> v, (o1, o2) -> o1));

        Method check = methodMap.get("check");
        if (check == null) {
            throw new InstantiationException("check函数不存在，无法使用");
        }
        setCheckFunction(makeCheck(check));

        Method operate = methodMap.get("operate");
        if (operate == null) {
            throw new InstantiationException("operate函数不存在，无法使用");
        }
        setOperateFunction(makeOperate(operate));
    }

    private <T> void setProperty(String name, Class<T> paramClass, T o) throws InvocationTargetException, IllegalAccessException {
        try {
            clazz.getMethod(name, paramClass).invoke(instance, o);
        } catch (NoSuchMethodException ignored) {
        }
    }

    private Predicate<JSONObject> makeCheck(Method check) throws InstantiationException {
        Class returnType = check.getReturnType();
        Function<JSONObject, Object[]> paramFunc = jsonToParam(getMethodParam(check));
        if (String.class.equals(returnType)) {
            return m -> {
                try {
                    return m.getString("message").equals(check.invoke(instance, paramFunc.apply(m)));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logOperate.log(e);
                    return false;
                }
            };
        } else if (boolean.class.equals(returnType) || Boolean.class.equals(returnType)) {
            return m -> {
                try {
                    return (boolean) check.invoke(instance, paramFunc.apply(m));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logOperate.log(e);
                    return false;
                }
            };
        } else {
            throw new InstantiationException("check函数不规范");
        }
    }

    private Consumer<JSONObject> makeOperate(Method check) throws InstantiationException {
        Class returnType = check.getReturnType();
        Function<JSONObject, Object[]> paramFunc = jsonToParam(getMethodParam(check));
        if (void.class.equals(returnType)) {
            return m -> {
                try {
                    check.invoke(instance, paramFunc.apply(m));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logOperate.log(e);
                }
            };
        } else {
            return m -> {
                try {
                    Object r = check.invoke(instance, paramFunc.apply(m));
                    if (r != null) {
                        simpleHttpAPI.sendGroupMsg(groupID, r.toString());
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logOperate.log(e);
                }
            };
        }
    }


    private Class[] getMethodParam(Method m) {
        return Stream.of(m.getParameters())
                .map(Parameter::getType)
                .toArray(Class[]::new);
    }

    private Function<JSONObject, Object[]> jsonToParam(Class[] p) {
        switch (p.length) {
            case 0:
                return j -> new Object[0];
            case 1:
                if (p[0].equals(String.class)) {
                    return j -> new Object[]{j.getString("message")};
                } else if (p[0].equals(Map.class)) {
                    return j -> new Object[]{j};
                }
                break;
            case 2:
                if (p[0].equals(String.class) && p[1].equals(String.class)) {
                    return j -> new Object[]{j.getString("user_id"), j.getString("message")};
                } else if ((p[0].equals(int.class) || p[0].equals(Integer.class)) && p[1].equals(String.class)) {
                    return j -> new Object[]{j.getInteger("user_id"), j.getString("message")};
                } else if ((p[0].equals(long.class) || p[0].equals(Long.class)) && p[1].equals(String.class)) {
                    return j -> new Object[]{j.getLong("user_id"), j.getString("message")};
                }
                break;
            default:
        }
        Object[] nullArray = new Object[p.length];
        for (int i = 0; i < p.length; i++) {
            nullArray[i] = null;
        }
        return jsonObject -> nullArray;
    }
}
