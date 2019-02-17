package top.yzlin.superrepeater.jsparse;

import org.springframework.stereotype.Component;
import top.yzlin.tools.SetConnection;
import top.yzlin.tools.Tools;

import java.util.Map;

@Component
public class JSTools {
    private static final SetConnection CONNECTION = conn -> {
        conn.setConnectTimeout(1000 * 30);
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
    };

    public String getData(String url, String param) {
        return Tools.sendGet(url, param, CONNECTION);
    }

    public String getData(String url, String param, Map<String, Object> head) {
        return Tools.sendGet(url, param, e -> {
            e.setConnectTimeout(1000 * 30);
            head.forEach((k, v) -> e.setRequestProperty(k, v.toString()));
        });
    }

    public String postData(String url, String param) {
        return Tools.sendPost(url, param, CONNECTION);
    }

    public String postData(String url, String param, Map<String, Object> head) {
        return Tools.sendPost(url, param, e -> {
            e.setConnectTimeout(1000 * 30);
            head.forEach((k, v) -> e.setRequestProperty(k, v.toString()));
        });
    }

}
