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
    private LocalClassLoader classLoader;

    @Autowired
    public void setClassLoader(LocalClassLoader classLoader) {
        this.classLoader = classLoader;
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
//        if(name.substring(0,name.lastIndexOf('0')))

        return null;
    }

}
