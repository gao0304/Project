package com.gao.controller;

import com.gao.model.Article;
import com.gao.model.Category;
import com.gao.model.Comment;
import com.gao.model.User;
import com.gao.service.ArticleService;
import com.gao.service.CategoryService;
import com.gao.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

@Controller
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CategoryService categoryService;

    @RequestMapping("/")
    //返回首页
    public String index(Model model){
        List<Article> articles = articleService.queryArticles();
        model.addAttribute("articleList", articles);
        return "index";
    }

    @RequestMapping("/a/{id}")
    //返回文章详情页面
    public String detail(@PathVariable("id") Long id, Model model){
        Article article = articleService.queryArticle(id);
        List<Comment> comments = commentService.queryComments(id);
        article.setCommentList(comments);
        model.addAttribute("article", article);
        return "info";
    }

    @RequestMapping("/writer")
    //发布文章页面
    public String writer(HttpSession session, Model model, Long activeCid){
        //利用Session获取user
        User user = (User) session.getAttribute("user");
        //获取用户的文章列表
        List<Article> articles = articleService.queryArticlesByUserId(user.getId());
        model.addAttribute("articleList", articles);
        //获取用户的分类列表
        List<Category> categories = categoryService.queryCategoriesByUserId(user.getId());
        model.addAttribute("categoryList", categories);
        //还需要添加一个激活分类的id,根据分类的id来决定创建哪个分类的文章
        model.addAttribute("activeCid",activeCid == null ? categories.get(0).getId() : activeCid);
        return "writer";
    }

    /**
     * 跳转到新建文章/修改文章的页面（都在editor.ftlh文件中）
     * @param type 新增为1，修改为2
     * @param id 新增时为categoryId,修改时为articleId
     * @param model editor页面需要type属性，都需要category,新增时需要activeCid,修改时需要article
     * @return
     */
    @RequestMapping("writer/forward/{type}/{id}/editor")
    public String editor(@PathVariable("type") Integer type, @PathVariable("id") Long id, Model model){
        Category category;
        if(type == 1){
            //完成editor页面修改的属性设置
            category = categoryService.queryCategoryById(id);
            model.addAttribute("activeCid",id);
        }else{
            //完成editor页面修改的属性设置
            Article article = articleService.queryArticle(id);
            model.addAttribute("article", article);
            category = categoryService.queryCategoryById(new Long(article.getCategoryId()));
        }
        model.addAttribute("type", type);
        model.addAttribute("category", category);
        return "editor";
    }

    /**
     * 文章新增/修改操作
     * @param type 新增为1，修改为2
     * @param id 新增时为categoryId，修改时为articleId
     * @param article 发布的文章信息
     * @return
     */
    @RequestMapping(value = "/writer/article/{type}/{id}", method = RequestMethod.POST)
    public String publish(@PathVariable("type") Integer type, @PathVariable("id") Integer id, Article article, HttpSession  session){
        article.setUpdatedAt(new Date());
        if(type == 1){
            //新增的时候，插入文章数据
            article.setCategoryId(id);
            User user = (User) session.getAttribute("user");
            article.setUserId(user.getId());
            article.setCoverImage("https://picsum.photos/id/1/400/300");
            article.setCreatedAt(new Date());
            article.setStatus((byte)0);
            article.setViewCount(0L);
            article.setCommentCount(0);
            int num = articleService.insert(article);
            id = article.getId().intValue();
        }else{
            //修改的时候，修改文章数据
            article.setId(new Long(id));
            int num = articleService.updateByCondition(article);
        }
        return "redirect:/writer";
    }

    /**
     * 删除指定的文章
     * @param id
     * @return
     */
    @RequestMapping(value = "/writer/delete/{id}/editor")
    public String deleteEditor( @PathVariable("id") Long id){
        int num = articleService.deleteEditor(id);
        return "redirect:/writer";
    }
}
