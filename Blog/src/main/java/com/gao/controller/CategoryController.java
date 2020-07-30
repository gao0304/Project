package com.gao.controller;

import com.gao.model.Category;
import com.gao.model.User;
import com.gao.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

@Controller
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @RequestMapping(value = "/editCategory/add", method = RequestMethod.POST)
    //加入新的分类
    public String addCategory(HttpSession session, Category category){
        User user = (User) session.getAttribute("user");
        category.setUserId(user.getId());
        int num = categoryService.insert(category);
        return "redirect:/writer";
    }
}
