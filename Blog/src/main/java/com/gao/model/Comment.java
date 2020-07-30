package com.gao.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Comment {
    private Long id;

    private Long userId;

    private Long articleId;

    private String content;

    private Date createdAt;

    private User user;

}