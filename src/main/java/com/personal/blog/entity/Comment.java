package com.personal.blog.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * null
 * @TableName comment
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment implements Serializable {
    /**
     * 
     *
     * @mbg.generated 2021-02-26 21:44:03
     */
    @TableId(type = IdType.INPUT)
    private Integer id;

    private String blogTitle;

    private String userName;

    private String content;

    private Date createDate;

    private String replyUser;

    private Boolean status;

    private Integer relatedId;

    @TableField(exist = false)
    private List<Comment> commentList;

    @TableField(exist = false)
    private String imgUrl;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table comment
     *
     * @mbg.generated 2021-02-26 21:44:03
     */
    private static final long serialVersionUID = 1L;
}