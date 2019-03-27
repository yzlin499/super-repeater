package top.yzlin.superrepeater.javaparse;

import top.yzlin.superrepeater.BaseMethodEvent;
import top.yzlin.superrepeater.log.LogOperate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Consumer;

public class JavaMethodEvent extends BaseMethodEvent {
    private String groupID;
    private Object instance;

    public JavaMethodEvent() {

    }

    public JavaMethodEvent(Class clazz, String groupID, LogOperate logOperate) throws IllegalAccessException, InstantiationException {
        instance = clazz.newInstance();
        try {
            Method setLogOperateMethod = clazz.getMethod("setLogOperate", Consumer.class);
            setLogOperateMethod.invoke(instance, (Consumer) logOperate::log);
        } catch (NoSuchMethodException ignored) {

        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        try {
            Method check = clazz.getMethod("check");
            check.getReturnType().equals(String.class);
        } catch (NoSuchMethodException e) {
            try {
                Method check = clazz.getMethod("check", Map.class);
            } catch (NoSuchMethodException e1) {
                throw new InstantiationException("check函数不存在，无法使用");
            }
        }

    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }
}
