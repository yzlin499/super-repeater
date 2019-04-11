package top.yzlin.superrepeater.javaparse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.yzlin.superrepeater.LanguageParse;
import top.yzlin.superrepeater.MethodEvent;
import top.yzlin.superrepeater.SimpleHttpAPI;
import top.yzlin.superrepeater.database.DBManager;
import top.yzlin.superrepeater.log.LoggerManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

@Component
public class JavaParse implements LanguageParse {
    private String groupID;
    private LoggerManager loggerManager;
    private ClassLoaderFactory loaderFactory;
    private SimpleHttpAPI simpleHttpAPI;
    private DBManager dbManager;

    @Autowired
    public void setDbManager(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    @Autowired
    public void setSimpleHttpAPI(SimpleHttpAPI simpleHttpAPI) {
        this.simpleHttpAPI = simpleHttpAPI;
    }

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
    public MethodEvent parse(File file) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException, IOException, SQLException {
        String name = file.getName();
        name = name.substring(0, name.lastIndexOf('.'));
        JavaMethodEvent m = new JavaMethodEvent();
        m.setName(file.getName());
        m.setGroupID(groupID);
        m.setSimpleHttpAPI(simpleHttpAPI);
        m.setClazz(loaderFactory.compiler(name));
        m.setLogOperate(loggerManager.getLogOperate(file.getName()));
        m.setDbManager(dbManager);
        m.init();
        return m;
    }

}
