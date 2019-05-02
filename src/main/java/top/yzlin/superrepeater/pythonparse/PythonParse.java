package top.yzlin.superrepeater.pythonparse;

import org.python.core.Py;
import org.python.util.PythonInterpreter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.yzlin.superrepeater.LanguageParse;
import top.yzlin.superrepeater.MethodEvent;
import top.yzlin.superrepeater.SimpleHttpAPI;
import top.yzlin.superrepeater.database.DBManager;
import top.yzlin.superrepeater.log.LoggerManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Component
public class PythonParse implements LanguageParse, InitializingBean {
    private String groupID;
    private LoggerManager loggerManager;
    private SimpleHttpAPI simpleHttpAPI;
    private DBManager dbManager;


    @Override
    public void afterPropertiesSet() throws Exception {
        Properties props = new Properties();
        props.put("python.home", "path to the Lib folder");
        props.put("python.console.encoding", "utf-8");
        props.put("python.security.respectJavaAccessibility", "false");
        props.put("python.import.site", "false");
        Properties preprops = System.getProperties();
        PythonInterpreter.initialize(preprops, props, new String[0]);
    }

    @Autowired
    public void setSimpleHttpAPI(SimpleHttpAPI simpleHttpAPI) {
        this.simpleHttpAPI = simpleHttpAPI;
    }

    @Autowired
    public void setDbManager(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    @Value("${user.groupID}")
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
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder(fis.available());
        int tmp;
        while ((tmp = isr.read()) != -1) {
            stringBuilder.append((char) tmp);
        }
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.exec(Py.newStringUTF8(stringBuilder.toString()));
        PythonMethodEvent methodEvent = new PythonMethodEvent();
        methodEvent.setInterpreter(interpreter);
        methodEvent.setDbManager(dbManager);
        methodEvent.setGroupID(groupID);
        methodEvent.setLogOperate(loggerManager.getLogOperate(file.getName()));
        methodEvent.setSimpleHttpAPI(simpleHttpAPI);
        methodEvent.setName(file.getName());
        methodEvent.init();
        return methodEvent;
    }
}
