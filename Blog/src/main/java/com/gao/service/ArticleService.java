package com.gao.service;

import com.gao.mapper.ArticleMapper;
import com.gao.model.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleService {

    //mapper需要加上@Mapper，MyBatis才能将Mapper注册到Spring容器中
    @Autowired
    private ArticleMapper articleMapper;


    public List<Article> queryArticles() {
        return articleMapper.selectAll();
    }

    public Article queryArticle(Long id) {
        return articleMapper.selectByPrimaryKey(id);
    }

    public List<Article> queryArticlesByUserId(Long id) {
        return articleMapper.queryArticlesByUserId(id);
    }

    public int insert(Article article) {
        return articleMapper.insert(article);
    }

    public int updateByCondition(Article article) {
        return articleMapper.updateByCondition(article);
    }

    public int deleteEditor(Long id) {
        return articleMapper.deleteByPrimaryKey(id);
    }
}
