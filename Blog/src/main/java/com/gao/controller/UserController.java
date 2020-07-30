package com.gao.controller;

import com.gao.model.User;
import com.gao.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/login")
    public String login(String username, String password, HttpServletRequest request){
        //用户名和密码的校验，目前只做空的判断，跳转到login页面，检验通过后会跳转到首页
        if(username == null || password ==null){
            return "login";
        }
        User user = userService.login(username,password);
        if(user == null){
            return "login";
        }else{
            //设置session
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            return "/";
        }
    }
}
