package top.yzlin.superrepeater.javaparse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.yzlin.superrepeater.LanguageParse;
import top.yzlin.superrepeater.MethodEvent;
import top.yzlin.superrepeater.log.LoggerManager;

import java.io.File;

@Component
public class JavaParse implements LanguageParse {
    private String groupID;
    private LoggerManager loggerManager;
    private ClassLoaderFactory loaderFactory;

    @Autowired
    public void setClassLoader(ClassLoaderFactory loaderFactory) {
        this.loaderFactory = loaderFactory;
    }

    @Value("${user.groupID}")
    @Override
    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    @Override
    @Autowired
    public void setLoggerManager(LoggerManager loggerManager) {
        this.loggerManager = loggerManager;
    }

    @Override
    public MethodEvent parse(File file) throws Exception {
        String name = file.getName();
        name = name.substring(0, name.lastIndexOf('.'));
        return new JavaMethodEvent(loaderFactory.compiler(name), groupID, loggerManager.getLogOperate(file.getName()));
    }

}
