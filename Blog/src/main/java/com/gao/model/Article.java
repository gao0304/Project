package com.gao.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class Article {
    private Long id;

    private Long userId;

    private String coverImage;

    private Integer categoryId;

    private Byte status;

    private String title;

    private String content;

    private Long viewCount;

    private Date createdAt;

    private Date updatedAt;

    //页面需要的属性，需要我们自行添加
    private User author; //文章的作者

    private Integer commentCount; //文章的评论数

    private List<Comment> commentList; //文章的评论列表
}