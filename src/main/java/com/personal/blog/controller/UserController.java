/**
 * Copyright (C), 2015-2021, XXX有限公司
 * FileName: UserController
 * Author:   1056427550
 * Date:     2021-2-8 23:03
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.personal.blog.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.personal.blog.entity.Msg;
import com.personal.blog.entity.User;
import com.personal.blog.mapper.UserMapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;


/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author 1056427550
 * @create 2021-2-8
 * @since 1.0.0
 */

@Controller
@CrossOrigin
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/admin/addUsers")
    @ResponseBody
    public Msg addUsers(HttpServletRequest request) {

        // 将user字符串转化为JSON后再转化为User对象
        User user = (User) JSONObject.toBean(JSONObject.fromObject(request.getParameter("user")), User.class);
        System.out.println(user);

        // 判断用户是否已经存在
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", user.getUsername());
        // 设置用户详情
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        user.setStatus(true);
        if (user.getNickname().equals("")) {
            user.setNickname(user.getUsername());
        }
        if (userMapper.selectCount(wrapper) == 0) {
            // 如果不存在该用户即创建该用户
            userMapper.insert(user);

            return Msg.success();
        } else if (!userMapper.selectOne(wrapper).isStatus()){
            userMapper.update(user,wrapper);
            return Msg.success();
        } else {
            return Msg.fail().add("errorMsg", "该用户已存在，创建失败");
        }

    }

    @PostMapping("/admin/updateUser")
    public String updateUser(HttpServletRequest request) {

        System.out.println("运行了update");

        // 取出参数
        Integer page = Integer.valueOf(request.getParameter("page"));
        String username = request.getParameter("username");
        String nickname = request.getParameter("nickname");
        String email = request.getParameter("email");
        boolean permission = request.getParameter("permission").equals("1") ? true : false;

        // 实例化用户
        User user = new User();
        user.setNickname(nickname);
        user.setEmail(email);
        user.setPermission(permission);
        user.setUpdateDate(new Date());
        user.setStatus(true);

        // 条件wrapper
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username",username);

        try {
            userMapper.update(user,wrapper);
        }catch (Exception e){
            System.out.println(e);
        }

        return "forward:/admin/getUsers?page=" + page;
    }

    @PostMapping("/admin/delUser")
    public String delUser(HttpServletRequest request) {

        System.out.println("运行了delUser");

        String username = request.getParameter("username");
        List<String> list = null;
        // 若传递过来的是一个数组
        if (username.startsWith("[")) {
            list = JSONArray.fromObject(username);
        }

        Integer page = Integer.valueOf(request.getParameter("page"));

        if (username != null) {
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            User user = new User();
            user.setStatus(false);

            // 注销多个用户
            if (list != null) {
                for (String name : list) {
                    wrapper.clear();
                    wrapper.eq("username", name);
                    try {
                        userMapper.update(user, wrapper);
                        System.out.println("删除成功");
                    } catch (Exception e) {
                        System.out.println("删除失败");
                    }
                }
            } else {
                // 注销一个用户
                wrapper.eq("username", username);
                try {
                    userMapper.update(user, wrapper);
                    System.out.println("删除成功");
                } catch (Exception e) {
                    System.out.println("删除失败");
                }
            }
        }

        return "forward:/admin/getUsers?page=" + page;
    }

    @RequestMapping("/admin/getUsers")
    @ResponseBody
    public Msg getUsers(@RequestParam(value = "page", defaultValue = "1") Integer page,
                        @RequestParam(value = "showFlag", defaultValue = "false") String showFlag,
                        HttpServletRequest request) {

        // 引入PageHelper插件并调用，传入页码及每页的行数
        PageHelper.startPage(page, 10);

        System.out.println(showFlag);

        List<User> users;

        if (showFlag.equals("true")) {
            // 查询所有员工
            users = userMapper.selectList(new QueryWrapper<User>().orderByDesc("status"));
        } else {
            // 查询未注销的员工
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("status", true);
            users = userMapper.selectList(wrapper);
        }


        // 使用PageInfo包装查询结果，连续显示的页数设置为10
        PageInfo<User> pageInfo = new PageInfo<>(users);

        System.out.println(pageInfo);

        return Msg.success().add("users", pageInfo);
    }

}