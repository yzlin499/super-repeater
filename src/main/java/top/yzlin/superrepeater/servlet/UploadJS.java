package top.yzlin.superrepeater.servlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import top.yzlin.superrepeater.MethodEvent;
import top.yzlin.superrepeater.MethodManager;
import top.yzlin.superrepeater.jsparse.JSParse;

import java.io.File;
import java.io.IOException;

@Controller
public class UploadJS {
    private MethodManager methodManager;
    private String privateKey;
    private File jsPath;
    private JSParse jsParse;

    @Value("${user.privateKey}")
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    @Value("${user.jspath}")
    public void setJsPath(String jsPath) {
        try {
            this.jsPath = ResourceUtils.getFile(jsPath);
            if (!this.jsPath.exists()) {
                this.jsPath.mkdirs();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    public void setJsParse(JSParse jsParse) {
        this.jsParse = jsParse;
    }

    @Autowired
    public void setMethodManager(MethodManager methodManager) {
        this.methodManager = methodManager;
    }

    @RequestMapping("uploadjs")
    public String file() {
        return "uploadjs";
    }

    @RequestMapping("fileUpload")
    @ResponseBody
    public String fileUpload(@RequestParam("fileName") MultipartFile file,
                             @RequestParam("privateKey") String privateKey,
                             @RequestParam(value = "onlyName", required = false, defaultValue = "") String onlyName) {
        if (!this.privateKey.equals(privateKey)) {
            return "无效密钥，你这是看不起我棒哥";
        }
        String fileName = file.getOriginalFilename();
        if ("".equals(fileName)) {
            return "你怕不是没有选文件";
        }
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
        if (file.isEmpty() && "JS".equals(suffix)) {
            return "无效文件";
        }
        fileName = "".equals(onlyName) ? fileName : (onlyName + ".js");
        File dest = new File(jsPath, fileName);
        try {
            file.transferTo(dest);
            MethodEvent methodEvent = jsParse.parse(dest);
            if (methodEvent == null) {
                dest.delete();
                methodManager.removeEvent(fileName);
                return "上传成功了\n" +
                        "文件识别名为" + fileName + "\n" +
                        "但是，到最后居然运行失败了，请联系管理\n" +
                        "因为失败的原因，原来有效的文件被删除而且事件也删除了";
            }
            methodManager.addEvent(fileName, methodEvent);
            return "也许成功了\n" +
                    "文件识别名为" + fileName;
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
            return "false";
        }
    }
}
