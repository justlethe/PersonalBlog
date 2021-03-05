/**
 * Copyright (C), 2015-2021, XXX有限公司
 * FileName: CommentController
 * Author:   1056427550
 * Date:     2021-2-26 1:22
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.personal.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.personal.blog.entity.Comment;
import com.personal.blog.entity.Msg;
import com.personal.blog.mapper.BlogMapper;
import com.personal.blog.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author 1056427550
 * @create 2021-2-26
 * @since 1.0.0
 */
@CrossOrigin
@Controller
public class CommentController {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private BlogMapper blogMapper;

    /**
     * 获取博客留言
     */
    @PostMapping("/admin/getBlogComments")
    @ResponseBody
    public Msg getBlogComments(HttpServletRequest request){

        // 获取博客id
        String blogTitle = request.getParameter("blogTitle");
        System.out.println("执行了getBlogComments,博客标题为"+blogTitle);

        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        if (blogTitle==null){
            wrapper.isNull("blog_title");
        }else {
            wrapper.eq("blog_title",blogTitle);
        }
        wrapper.isNull("reply_user").eq("status",1);
        List<Comment> comments = commentMapper.selectList(wrapper);
        System.out.println(comments);

        for (Comment comment : comments) {
            wrapper.clear();
            wrapper.eq("related_id",comment.getId());
            List<Comment> comments1=commentMapper.selectList(wrapper);
            comment.setCommentList(comments1);
        }

        return Msg.success().add("comments",comments);
    }

    /**
     * 留言管理获取所有留言
     */
    @GetMapping("/admin/getAllComments")
    @ResponseBody
    public Msg getAllComments(HttpServletRequest request){

        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_date");
        List<Comment> comments = commentMapper.selectList(wrapper);
        Integer total = commentMapper.selectCount(wrapper);

        return Msg.success().add("total",total).add("comments",comments);
    }

    /**
     * 保存博客留言
     */
    @PostMapping("/admin/saveBlogComments")
    @ResponseBody
    public Msg saveBlogComments(HttpServletRequest request){

        System.out.println("调用了saveBlogComments");

        String blogTitle=null,userName=null,content=null;
        String replyUser = null;
        Integer relatedId = null;
        try{
            blogTitle = request.getParameter("blogTitle").equals("")?null:request.getParameter("blogTitle");
            userName = request.getParameter("userName");
            content = request.getParameter("content");
            replyUser = request.getParameter("replyUser").equals("")?null:request.getParameter("replyUser");
            relatedId = Integer.parseInt(request.getParameter("relatedId"));
        }catch (Exception e){
            System.out.println(e);
        }

        Comment comment = new Comment(null,blogTitle,userName,content,new Date(),replyUser,false,relatedId,null);
        System.out.println(comment);

        try{
            commentMapper.insert(comment);
        }catch (Exception e){
            return Msg.fail().add("errorMsg","留言提交失败");
        }

        return Msg.success();
    }

    @PostMapping("/admin/changeCommentStatus")
    @ResponseBody
    public Msg changeCommentsStatus(HttpServletRequest request){

        String status = request.getParameter("status");
        int id = Integer.parseInt(request.getParameter("id"));
        System.out.println(status+"   "+id);

        Comment comment = new Comment();
        comment.setId(id);
        comment.setStatus(status.equals("true")?true:false);
        try {
            System.out.println(comment);
            commentMapper.updateByPrimaryKeySelective(comment);
        }catch (Exception e){
            return Msg.fail();
        }

        return Msg.success();
    }

    @PostMapping("/admin/delComment")
    @ResponseBody
    public Msg delComment(HttpServletRequest request){

        int id = Integer.parseInt(request.getParameter("id"));

        try{
            commentMapper.deleteByPrimaryKey(id);
            return Msg.success();
        }catch (Exception e){
            return Msg.fail().add("errorMsg","删除失败");
        }
    }
}