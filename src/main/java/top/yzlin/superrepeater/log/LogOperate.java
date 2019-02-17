package top.yzlin.superrepeater.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogOperate {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("[MM-dd HH:mm:ss] ");
    private FileWriter log;

    public LogOperate(File file) {
        try {
            log = new FileWriter(file, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(Object text) {
        String t = dateFormat.format(new Date()) + text.toString();
        try {
            log.write(t);
            log.write("\r\n");
            log.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            log.close();
            dateFormat = null;
            log = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
