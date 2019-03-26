package top.yzlin.superrepeater.servlet;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MultipartException.class)
    @ResponseBody
    public String handleError(MultipartException e, RedirectAttributes redirectAttributes) {
//        redirectAttributes.addFlashAttribute("message", e.getCause().getMessage());
        return "文件大小炸了哇";
    }
}