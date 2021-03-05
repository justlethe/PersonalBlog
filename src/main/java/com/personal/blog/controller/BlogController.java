/**
 * Copyright (C), 2015-2021, XXX有限公司
 * FileName: BlogController
 * Author:   1056427550
 * Date:     2021-2-20 17:21
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.personal.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.personal.blog.entity.Blog;
import com.personal.blog.entity.Msg;
import com.personal.blog.mapper.BlogMapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author 1056427550
 * @create 2021-2-20
 * @since 1.0.0
 */

@CrossOrigin
@Controller
public class BlogController {

    @Autowired
    private BlogMapper blogMapper;

    /**
     * 按分类取得所有博客列表后分页
     **/
    @RequestMapping("/admin/getBlogs")
    @ResponseBody
    public Msg getBlogs(@RequestParam(value = "page", defaultValue = "1") Integer page,
                        @RequestParam(value = "category",defaultValue = "")String category,
                        @RequestParam(value = "search",defaultValue = "")String search,HttpServletRequest request) {

        Integer limit = Integer.parseInt(request.getParameter("limit"));
        PageHelper.startPage(page, limit);

        List<Blog> blogs = new ArrayList<>();
        QueryWrapper<Blog> wrapper = new QueryWrapper<>();

        if (!search.equals("")){
            System.out.println(search);
            wrapper.and(w -> w.like("category",search).or().like("title",search).or().like("content",search));
        }

        if (!category.equals("")){
            wrapper.and(w->w.like("category",category));
            blogs = blogMapper.selectList(wrapper);
        }else {
            blogs = blogMapper.selectList(wrapper);
        }

        PageInfo<Blog> pageInfo = new PageInfo<>(blogs);

        Integer total = blogMapper.selectCount(null);

        return Msg.success().add("blogs", pageInfo).add("total",total);
    }

    /**
     * 根据id修改博客类别
     **/
    @PostMapping("/admin/updateBlogsCategory")
    @ResponseBody
    public Msg updateBlogs(HttpServletRequest request) {

        //要操作的类别changeCategory,要增加该分类的博客id数组addCategoryList,要删除该分类的博客id数组remCategoryList
        String changeCategory = request.getParameter("changeCategory");
        JSONArray addCategoryList = JSONArray.fromObject(request.getParameter("addCategoryList"));
        JSONArray remCategoryList = JSONArray.fromObject(request.getParameter("remCategoryList"));

        Blog blog = new Blog();
        blog.setUpdateDate(new Date());
        QueryWrapper<Blog> wrapper = new QueryWrapper<>();

        // 遍历addCategoryList作为博客id为博客添加类别
        for (Object id : addCategoryList) {
            wrapper.clear();
            wrapper.eq("id", id);
            blog = blogMapper.selectOne(wrapper);
            //若博客分类不为空,添加","+该类别；否则直接添加该类别
            if (!blog.getCategory().equals("")) {
                blog.setCategory(blog.getCategory() + "," + changeCategory);
            } else {
                blog.setCategory(changeCategory);
            }
            try {
                blogMapper.update(blog, wrapper);
            } catch (Exception e) {
                return Msg.fail();
            }
        }
        // 遍历remCategoryList作为博客id为博客删除类别
        for (Object id : remCategoryList) {
            wrapper.clear();
            wrapper.eq("id", id);
            blog = blogMapper.selectOne(wrapper);
            String category = blog.getCategory();
            //若博客分类包含",",删除","+该类别；否则直接删除该类别
            if(blog.getCategory().contains(",")){
                category = category.replace("," +changeCategory, "");
            }else {
                category = category.replace(changeCategory, "");
            }
            blog.setCategory(category);

            try {
                blogMapper.update(blog, wrapper);
            } catch (Exception e) {
                return Msg.fail();
            }
        }

        return Msg.success();
    }

    /**
     * 保存或发布或编辑博客
     **/
    @PostMapping("/admin/saveBlog")
    @ResponseBody
    public Msg saveBlog(HttpServletRequest request){

        /**
         * blogDate:博客数据； publish:是否发布；updateFlag:更新博客
         **/
        JSONObject blogData = JSONObject.fromObject(request.getParameter("blogData"));
        Integer publish = Integer.parseInt((request.getParameter("published")));
        String updateFlag = request.getParameter("updateFlag");
        System.out.println("\n\n\n"+updateFlag+"\n\n\n");

        // 检查标题是否重复
        QueryWrapper<Blog> wrapper = new QueryWrapper<>();
        wrapper.eq("title",blogData.getString("title"));

        Blog blog = new Blog();
        blog.setTitle(blogData.getString("title"));
        blog.setDescription(blogData.getString("description"));
        blog.setContent(blogData.getString("md-content-html-code"));
        blog.setMdcontent(blogData.getString("content"));
        blog.setType(blogData.getString("type"));
        blog.setCategory(blogData.getString("category"));
        blog.setUpdateDate(new Date());
        if (publish == 1){
            blog.setPublished(true);
        }else {
            blog.setPublished(false);
        }
        if (blogData.getString("recommend").equals("1")){
            blog.setRecommend(true);
        }else {
            blog.setRecommend(false);
        }

        try {
            if (updateFlag.equals("true")){
                blogMapper.update(blog,wrapper);
            }else {
                blog.setCreateDate(new Date());
                blog.setViews(0);
                blogMapper.insert(blog);
            }
            return Msg.success();
        }catch (Exception e){
            System.out.println(e);
            return Msg.fail().add("errorMsg","发生错误，操作失败");
        }
    }

    /**
     * 根据博客标题获取单篇博客数据
     **/
    @PostMapping("/admin/getBlog")
    @ResponseBody
    public Msg getBlog(@RequestParam(value = "title",defaultValue = "测试用1")String title
            ,@RequestParam(value = "addView",defaultValue = "false")String addView
            ,HttpServletRequest request){

        System.out.println("运行了getBlog,title="+title);

        QueryWrapper<Blog> wrapper = new QueryWrapper<>();
        wrapper.eq("title",title);

        Blog blog = blogMapper.selectOne(wrapper);
        System.out.println(addView);
        if (addView.equals("true")){
            blog.setViews(blog.getViews()+1);
            blogMapper.update(blog,wrapper);
        }

        return Msg.success().add("blog",blog);
    }

    /**
     * 获取点击数最高的5篇博客
     **/
    @GetMapping("/admin/getHotBlogs")
    @ResponseBody
    public Msg getHotBlogs(HttpServletRequest request){

        System.out.println("请求了getHotBlogs");

        QueryWrapper<Blog> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("views").last("limit 5").select("title");

        List<Blog> blogs = blogMapper.selectList(wrapper);

        return Msg.success().add("blogs",blogs);
    }


}