package top.yzlin.superrepeater.log;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;


public class LoggerManager implements DisposableBean {
    private File logPath;
    private Map<String, LogOperate> logOperateMap = new HashMap<>();
    private Map<String, File> fileMap = new HashMap<>();


    public void setLogPath(String path) {
        try {
            logPath = ResourceUtils.getFile(path);
            if (!logPath.exists()) {
                logPath.mkdirs();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public File getLogFile(String name) {
        if (fileMap.containsKey(name)) {
            return fileMap.get(name);
        } else {
            File file = new File(logPath, name + ".log");
            if (file.exists()) {
                fileMap.put(name, file);
                return file;
            } else {
                return null;
            }
        }

    }

    public LogOperate getLogOperate(String name) {
        LogOperate temp;
        if (!logOperateMap.containsKey(name)) {
            temp = createLogOperateFormFile(name);
            logOperateMap.put(name, temp);
        } else {
            temp = logOperateMap.get(name);
        }
        return temp;
    }

    private LogOperate createLogOperateFormFile(String name) {
        return new LogOperate(new File(logPath, name + ".log"));
    }

    @Override
    public void destroy() {
        logOperateMap.forEach((k, v) -> v.close());
        logOperateMap.clear();
        logOperateMap = null;
        logPath = null;
    }
}
