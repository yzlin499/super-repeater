package top.yzlin.superrepeater.pythonparse;

import org.python.util.PythonInterpreter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.yzlin.superrepeater.LanguageParse;
import top.yzlin.superrepeater.MethodEvent;
import top.yzlin.superrepeater.SimpleHttpAPI;
import top.yzlin.superrepeater.database.DBManager;
import top.yzlin.superrepeater.log.LoggerManager;

import java.io.File;

@Component
public class PythonParse implements LanguageParse {
    private String groupID;
    private LoggerManager loggerManager;
    private SimpleHttpAPI simpleHttpAPI;
    private DBManager dbManager;

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
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.execfile(file.getAbsolutePath());
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
