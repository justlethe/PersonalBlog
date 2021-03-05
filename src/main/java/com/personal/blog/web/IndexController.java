/**
 * Copyright (C), 2015-2021, XXX有限公司
 * FileName: IndexController
 * Author:   1056427550
 * Date:     2021-1-21 17:10
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.personal.blog.web;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.personal.blog.entity.User;
import com.personal.blog.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;


/**
 * 〈一句话功能简述〉<br> 
 * 〈用于各个页面的普通跳转〉
 *
 * @author 1056427550
 * @create 2021-1-21
 * @since 1.0.0
 */
@Controller
public class IndexController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/")
    public String index(HttpSession session){
        //int i = 9/0;
        //String blog = null;
        //if (blog == null){
        //    throw new NotFoundExcepiton("博客不存在");
        //}
        //System.out.println("index执行——————————————————");
        System.out.println(session.getId());
        return "index";
    }

    @RequestMapping("/main")
    public String main(){
        return "main";
    }

    @RequestMapping("/blog")
    public String blog(){
        return "blog";
    }

    @RequestMapping("/comments")
    public String comments(){
        return "comments";
    }

    @RequestMapping("/about")
    public String about(){
        return "about";
    }

    @RequestMapping("/admin")
    public String admin(@RequestParam(value = "page", defaultValue = "1") Integer page, Model model){

        System.out.println("请求了/admin");

        // 引入PageHelper插件并调用，传入页码及每页的行数
        PageHelper.startPage(page,5);

        // 查询所有员工
        List<User> users = userMapper.selectList(null);
        // 使用PageInfo包装查询结果，连续显示的页数设置为5
        PageInfo<User> pageInfo = new PageInfo<User>(users);

        System.out.println(pageInfo);
        System.out.println(pageInfo.getList().get(0));

        model.addAttribute("users",pageInfo);

        return "admin";
    }

    @RequestMapping("/timeline")
    public String timeline(){
        return "timeline";
    }

    @RequestMapping("/input")
    public String input(){
        return "input";
    }

}