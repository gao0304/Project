package com.gao.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.io.PrintWriter;
import java.io.StringWriter;

//此代码是为了解决错误，当代码中有错误的时候会直接在网页上返回错误的信息
@ControllerAdvice
public class ControllerInterceptor {

    @ExceptionHandler(Exception.class)
    public ModelAndView handle(Exception e){
        StringWriter sw =new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        ModelAndView mv = new ModelAndView();
        mv.addObject("message", e.getMessage());
        mv.addObject("stackTrace", sw.toString());
        mv.setViewName("error");
        return mv;
    }
}
