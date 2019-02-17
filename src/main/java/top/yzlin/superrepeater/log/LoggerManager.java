package top.yzlin.superrepeater.log;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

@Component
public class LoggerManager {
    private File logPath;
    private Map<String, LogOperate> logOperateMap = new HashMap<>();

    @Value("${user.logpath}")
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
        return new LogOperate(new File(logPath, name));
    }

}
