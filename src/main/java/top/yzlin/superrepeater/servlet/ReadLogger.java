package top.yzlin.superrepeater.servlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import top.yzlin.superrepeater.log.LoggerManager;

import java.io.File;
import java.io.FileNotFoundException;

@Controller
public class ReadLogger {
    private LoggerManager loggerManager;

    @Autowired
    public void setLoggerManager(LoggerManager loggerManager) {
        this.loggerManager = loggerManager;
    }

    @RequestMapping("/log/{name}")
    public ResponseEntity<Object> getLog(@PathVariable("name") String name) throws FileNotFoundException {
        File f = loggerManager.getLogFile(name + ".js");
        if (f == null) {
            return ResponseEntity.ok().body("无法获取文件");
        } else {
            return ResponseEntity.ok().body(new FileSystemResource(f));
        }
    }
}
