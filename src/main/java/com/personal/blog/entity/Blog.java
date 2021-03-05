package com.personal.blog.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * null
 * @author 1056427550
 * @TableName blog
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Blog implements Serializable {


    @TableId(type = IdType.INPUT)
    private Integer id;

    private String title;

    private String content;

    private String picture;

    private String type;

    private Integer views;

    private String category;

    private boolean recommend;

    private Date createDate;

    private Date updateDate;

    private boolean published;

    private String description;

    private String mdcontent;

    private static final long serialVersionUID = 1L;
}