/**
 * Copyright (C), 2015-2021, XXX有限公司
 * FileName: CategoryController
 * Author:   1056427550
 * Date:     2021-2-19 21:37
 * Description: 博客类别的controller层
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.personal.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.personal.blog.entity.Blog;
import com.personal.blog.entity.Category;
import com.personal.blog.entity.Msg;
import com.personal.blog.mapper.BlogMapper;
import com.personal.blog.mapper.CategoryMapper;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * 〈博客类别的controller层〉
 *
 * @author 1056427550
 * @create 2021-2-19
 * @since 1.0.0
 */

@Controller
@CrossOrigin
public class CategoryController {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private BlogMapper blogMapper;

    @RequestMapping("/admin/getCategories")
    @ResponseBody
    public Msg getCategories(HttpServletRequest request) {

        System.out.println("执行了getCategories");

        List<Category> categories = categoryMapper.selectList(null);

        QueryWrapper<Blog> wrapper = new QueryWrapper<>();
        wrapper.select("id","title","category");
        List<Blog> blogs = blogMapper.selectList(wrapper);
        blogs.forEach(blog -> {
            for (Category category : categories) {
                if (blog.getCategory().contains(category.getCategoryName())) {
                    category.setBlogList(blog);
                }
            }
        });

        return Msg.success().add("total", categories.size()).add("categories", categories).add("blogList",blogs);
    }

    @GetMapping("/admin/getCategory")
    @ResponseBody
    public Msg getCategory(HttpServletRequest request) {

        System.out.println("执行了getCategory");

        List<Category> category = categoryMapper.selectList(null);

        return Msg.success().add("category", category);
    }

    @PostMapping("/admin/addCategory")
    @ResponseBody
    public Msg addCategory(HttpServletRequest request) {

        System.out.println("执行了addCategory");

        String name = request.getParameter("name");
        Category category = new Category(null, name, null);

        if (categoryMapper.selectList(new QueryWrapper<Category>().eq("category_name", name)) != null) {
            return Msg.fail().add("errorMsg", "已存在该类别!");
        }

        try {
            categoryMapper.insert(category);
            return Msg.success().add("successMsg", "添加成功!");
        } catch (Exception e) {
            return Msg.fail().add("errorMsg", "添加失败!");
        }
    }

    @PostMapping("/admin/delCategory")
    @ResponseBody
    public Msg delCategory(HttpServletRequest request) {

        String categoryName = request.getParameter("category");
        List<String> list = null;
        // 若传递过来的是一个数组
        if (categoryName.startsWith("[")) {
            list = JSONArray.fromObject(categoryName);
        }

        if (categoryName != null) {
            QueryWrapper<Category> wrapper = new QueryWrapper<>();

            if (list != null) {
                for (String name : list) {
                    wrapper.clear();
                    wrapper.eq("category_name", name);
                    try {
                        categoryMapper.delete(wrapper);
                        System.out.println("删除成功");
                    } catch (Exception e) {
                        System.out.println("删除失败");
                        return Msg.fail().add("errorMsg", "删除失败!");
                    }
                }
            } else {
                wrapper.eq("category_name", categoryName);
                try {
                    categoryMapper.delete(wrapper);
                    System.out.println("删除成功");
                } catch (Exception e) {
                    System.out.println("删除失败");
                }
            }
        }
        return Msg.success().add("successMsg", "删除成功!");
    }

}