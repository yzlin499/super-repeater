package top.yzlin.superrepeater.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import top.yzlin.superrepeater.MethodEvent;
import top.yzlin.superrepeater.MethodManager;
import top.yzlin.superrepeater.SimpleHttpAPI;
import top.yzlin.superrepeater.javaparse.JavaClass;
import top.yzlin.superrepeater.javaparse.JavaParse;
import top.yzlin.superrepeater.jsparse.JSFile;
import top.yzlin.superrepeater.jsparse.JSParse;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class MainConfig {

    @Bean
    @Scope("singleton")
    @Autowired
    public SimpleHttpAPI cqRobot(@Value("${cqRobot.port}") int port, MethodManager methodManager) {
        SimpleHttpAPI s = new SimpleHttpAPI(port);
        s.setOnMessage(methodManager);
        return s;
    }

    @Bean
    @Scope("singleton")
    public Map<String, MethodEvent> methodEventMap(@Autowired JSParse jsParse,
                                                   @Autowired JSFile jsFile,
                                                   @Autowired JavaParse javaParse,
                                                   @Autowired JavaClass javaClass) {
        Map<String, MethodEvent> eventMap = new HashMap<>();
        eventMap.putAll(Stream.of(jsFile.getFiles())
                .map(f -> {
                    try {
                        return jsParse.parse(f);
                    } catch (FileNotFoundException | UnsupportedEncodingException e) {
                        e.printStackTrace();
                        return null;
                    }
                }).filter(Objects::nonNull)
                .collect(Collectors.toMap(MethodEvent::getName, v -> v)));
        eventMap.putAll(Stream.of(javaClass.getFiles())
                .map(f -> {
                    try {
                        return javaParse.parse(f);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }).filter(Objects::nonNull)
                .collect(Collectors.toMap(MethodEvent::getName, v -> v)));
        return eventMap;
    }
}
