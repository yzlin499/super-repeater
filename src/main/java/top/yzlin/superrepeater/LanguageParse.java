package top.yzlin.superrepeater;

import top.yzlin.superrepeater.log.LoggerManager;

import java.io.File;

public interface LanguageParse {

    void setGroupID(String groupID);

    void setLoggerManager(LoggerManager loggerManager);

    MethodEvent parse(File file) throws Exception;
}
