package top.yzlin.superrepeater.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import top.yzlin.superrepeater.MethodEvent;
import top.yzlin.superrepeater.MethodManager;
import top.yzlin.superrepeater.SimpleHttpAPI;
import top.yzlin.superrepeater.jsparse.JSFile;
import top.yzlin.superrepeater.jsparse.JSParse;

import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class MainConfig {
    private MethodManager methodManager;
    private JSFile jsFile;
    private JSParse jsParse;

    @Autowired
    public void setJsParse(JSParse jsParse) {
        this.jsParse = jsParse;
    }

    @Autowired
    public void setMethodManager(MethodManager methodManager) {
        this.methodManager = methodManager;
    }

    @Autowired
    public void setJsFile(JSFile jsFile) {
        this.jsFile = jsFile;
    }

    @Bean
    @Scope("singleton")
    public SimpleHttpAPI cqRobot(@Value("${cqRobot.port}") int port) {
        SimpleHttpAPI s = new SimpleHttpAPI(port);
        s.setOnMessage(methodManager);
        return s;
    }

    @Bean
    public List<MethodEvent> methodEventList() {
        return Stream.of(jsFile.getFiles())
                .map(f -> {
                    try {
                        return jsParse.parse(f);
                    } catch (FileNotFoundException | ScriptException e) {
                        e.printStackTrace();
                        return null;
                    }
                }).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }



}
