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
import top.yzlin.superrepeater.javaparse.JavaParse;
import top.yzlin.superrepeater.jsparse.JSParse;
import top.yzlin.superrepeater.pythonparse.PythonParse;

import java.io.File;
import java.io.IOException;

@Controller
public class UploadFile {
    private MethodManager methodManager;
    private String privateKey;
    private File jsPath;
    private File javaPath;
    private File pythonpath;
    private JSParse jsParse;
    private JavaParse javaParse;
    private PythonParse pythonParse;

    @Value("${user.privateKey}")
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    @Value("${user.javapath}")
    public void setJavaPath(String javaPath) {
        try {
            this.javaPath = ResourceUtils.getFile(javaPath);
            if (!this.javaPath.exists()) {
                this.javaPath.mkdirs();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @Value("${user.pythonpath}")
    public void setPythonpath(String pythonpath) {
        try {
            this.pythonpath = ResourceUtils.getFile(pythonpath);
            if (!this.pythonpath.exists()) {
                this.pythonpath.mkdirs();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    public void setJavaParse(JavaParse javaParse) {
        this.javaParse = javaParse;
    }

    @Autowired
    public void setJsParse(JSParse jsParse) {
        this.jsParse = jsParse;
    }

    @Autowired
    public void setPythonParse(PythonParse pythonParse) {
        this.pythonParse = pythonParse;
    }

    @Autowired
    public void setMethodManager(MethodManager methodManager) {
        this.methodManager = methodManager;
    }

    @RequestMapping("uploadjs")
    public String jsFile() {
        return "uploadjs";
    }

    @RequestMapping("uploadjava")
    public String javaFile() {
        return "uploadjava";
    }

    @RequestMapping("uploadpython")
    public String pythonFile() {
        return "uploadpython";
    }

    @RequestMapping("jsFileUpload")
    @ResponseBody
    public String fileUploadJS(@RequestParam("fileName") MultipartFile file,
                               @RequestParam("privateKey") String privateKey,
                               @RequestParam(value = "onlyName", required = false, defaultValue = "") String onlyName) {
        String fileName = file.getOriginalFilename();
        String t = fileCheck(file, privateKey, "JS");
        if (t != null) {
            return t;
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

    @RequestMapping("pythonFileUpload")
    @ResponseBody
    public String fileUploadPython(@RequestParam("fileName") MultipartFile file,
                                   @RequestParam("privateKey") String privateKey,
                                   @RequestParam(value = "onlyName", required = false, defaultValue = "") String onlyName) {
        String fileName = file.getOriginalFilename();
        String t = fileCheck(file, privateKey, "PY");
        if (t != null) {
            return t;
        }
        fileName = "".equals(onlyName) ? fileName : (onlyName + ".py");
        File dest = new File(pythonpath, fileName);
        try {
            file.transferTo(dest);
            MethodEvent methodEvent = pythonParse.parse(dest);
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
        } catch (Exception e) {
            e.printStackTrace();
            return "false";
        }
    }


    @RequestMapping("javaFileUpload")
    @ResponseBody
    public String fileUploadJava(@RequestParam("fileName") MultipartFile file,
                                 @RequestParam("privateKey") String privateKey) {
        String fileName = file.getOriginalFilename();
        String t = fileCheck(file, privateKey, "JAVA");
        if (t != null) {
            return t;
        }
        File dest = new File(javaPath, fileName);
        try {
            file.transferTo(dest);
            MethodEvent methodEvent = null;
            try {
                methodEvent = javaParse.parse(dest);
                if (methodEvent == null) {
                    throw new InstantiationException("奇妙的错误");
                }
                methodManager.addEvent(fileName, methodEvent);
                return "也许成功了\n" +
                        "文件识别名为" + fileName;
            } catch (InstantiationException | IOException e) {
                dest.delete();
                methodManager.removeEvent(fileName);
                return "上传成功了\n" +
                        "文件识别名为" + fileName + "\n" +
                        "但是，到最后居然运行失败了，请联系管理\n" +
                        "因为失败的原因，原来有效的文件被删除而且事件也删除了\n" +
                        e.getMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "false";
        }
    }


    private String fileCheck(MultipartFile file, String privateKey, String fileSuffix) {
        if (!this.privateKey.equals(privateKey)) {
            return "无效密钥，你这是看不起我棒哥";
        }
        String fileName = file.getOriginalFilename();
        if (fileName == null || "".equals(fileName)) {
            return "你怕不是没有选文件";
        }
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
        if (file.isEmpty() || !fileSuffix.equals(suffix)) {
            return "无效文件";
        }
        return null;
    }





}
