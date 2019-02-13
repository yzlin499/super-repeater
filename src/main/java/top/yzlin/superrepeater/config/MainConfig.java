package top.yzlin.superrepeater.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import top.yzlin.superrepeater.MethodManager;
import top.yzlin.superrepeater.SimpleHttpAPI;

@Configuration
public class MainConfig {

    private final MethodManager methodManager;

    @Autowired
    public MainConfig(MethodManager methodManager) {
        this.methodManager = methodManager;
    }

    @Bean
    @Scope("singleton")
    public SimpleHttpAPI cqRobot(@Value("${cqRobot.port}") int port) {
        SimpleHttpAPI s = new SimpleHttpAPI(port);
        s.setOnMessage(methodManager);
        return s;
    }


}
