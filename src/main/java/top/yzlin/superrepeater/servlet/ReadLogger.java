package top.yzlin.superrepeater.servlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.yzlin.superrepeater.log.LoggerManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class ReadLogger {
    private LoggerManager loggerManager;

    @Autowired
    public void setLoggerManager(LoggerManager loggerManager) {
        this.loggerManager = loggerManager;
    }

    @RequestMapping("/log/src/{name}")
    public ResponseEntity<Object> getLog(@PathVariable("name") String name) throws FileNotFoundException {
        File f = loggerManager.getLogFile(name);
        if (f == null) {
            return ResponseEntity.ok().body("无法获取文件");
        } else {
            return ResponseEntity.ok().body(new FileSystemResource(f));
        }
    }

    @RequestMapping("/log/{name}")
    public String testLog(@PathVariable("name") String name,
                          @RequestParam(value = "line", required = false, defaultValue = "30") int line,
                          Model model) throws IOException {
        File f = loggerManager.getLogFile(name);
        List<String> list;
        if (f == null) {
            list = Collections.singletonList("没有任何的日志记录哦！");
        } else if (line <= 0 || line > 500) {
            list = Collections.singletonList("参数无效");
        } else {
            String[] strings = new String[line];
            int index = -1;
            FileInputStream fis = new FileInputStream(f);
            InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader bis = new BufferedReader(isr);
            String temp;
            while ((temp = bis.readLine()) != null) {
                index++;
                strings[index % line] = temp;
            }
            int count = (index < line ? index : (line - 1)) + 1;
            if ("".equals(strings[index % line])) {
                count--;
                index--;
            }
            list = new ArrayList<>(count);
            while (count > 0) {
                list.add(strings[index % line]);
                index--;
                count--;
            }
        }
        model.addAttribute("name", name);
        model.addAttribute("logList", list);
        return "log";
    }
}
