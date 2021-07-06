/**
 * Copyright (C), 2015-2021, XXX有限公司
 * FileName: UserController
 * Author:   1056427550
 * Date:     2021-1-21 14:56
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.personal.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.personal.blog.entity.Msg;
import com.personal.blog.entity.User;
import com.personal.blog.mapper.UserMapper;
import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.javabeans.qzone.UserInfoBean;
import com.qq.connect.oauth.Oauth;
import com.qq.connect.api.qzone.UserInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;

/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author 1056427550
 * @create 2021-1-21
 * @since 1.0.0
 */
@Controller
@CrossOrigin
public class LoginController {

    @Autowired
    private UserMapper userMapper;

    @RequestMapping("/selectAll")
    @ResponseBody
    public void selectAll() {
        System.out.println("开始输出");
        List<User> user = userMapper.selectList(null);
        user.forEach(System.out::println);
    }

    @PostMapping("/login")
    @ResponseBody
    public Msg login(@RequestParam String username, @RequestParam String password,
                     HttpServletRequest request, HttpServletResponse response) {

        // 是否自动登录的标志,auto == "on"自动登录，否则不自动登录
        String auto = request.getParameter("auto");

        // 匹配用户名和密码
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        wrapper.eq("password", password);
        User user = userMapper.selectOne(wrapper);
        if (user != null) {
            // 如何匹配成功，将用户信息保存于session
            user.setPassword(null);
            request.getSession().setAttribute("user", user);
            request.getSession().setMaxInactiveInterval(7 * 24 * 60 * 60);
            System.out.println("auto="+auto);
            System.out.println(auto.equals("on"));

            if (auto.equals("on")){
                // 新建一个name为autoLogin的cookie并存放该用户名信息
                Cookie cookie = new Cookie("autoLogin", user.getUsername());
                // 设置作用域为根目录下所有
                cookie.setPath("/");
                // 设置cookie生命周期为一周
                cookie.setMaxAge(7 * 24 * 60 * 60);
                // 将cookie保存到客户端
                response.addCookie(cookie);
            }

            if (user.isStatus()){
                System.out.println("用户存在！");
                return Msg.success().add("user", user);
            }else {
                return Msg.fail().add("error","该用户已注销");
            }

        } else {
            System.out.println("用户不存在！");
            return Msg.fail();
        }
    }

    @PostMapping("/register")
    @ResponseBody
    public Msg register(HttpServletRequest request, HttpServletResponse response)
            throws UnsupportedEncodingException {

        // 设置字符编码方式
        request.setCharacterEncoding("utf-8");
        String username = request.getParameter("username");
        String password1 = request.getParameter("password1");

        // 先查找注册的用户名是否已存在
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User user_exist = userMapper.selectOne(wrapper);

        if (user_exist != null && user_exist.isStatus()) {
            // 如果存在且用户激活中，返回错误信息
            return Msg.fail().add("errorMsg", "该用户名已存在！");
        } else {
            // 存在但已注销则覆盖原用户，不存在则存进数据库
            User user = new User(null, username, password1, false, null,
                    new Date(), new Date(), null, true, username);
            System.out.println("插入的user为：\n" + user);
            if (user_exist != null && !user_exist.isStatus()){
                userMapper.update(user,wrapper);
            }else {
                userMapper.insert(user);
            }

            // 注册完直接登录
            user = userMapper.selectOne(wrapper);
            user.setPassword(null);
            request.getSession().setAttribute("user", user);
            request.getSession().setMaxInactiveInterval(7 * 24 * 60 * 60);

            // 新建一个name为autoLogin的cookie并存放该用户名信息
            Cookie cookie = new Cookie("autoLogin", user.getUsername());
            // 设置作用域为根目录下所有
            cookie.setPath("/");
            // 设置cookie生命周期为一周
            cookie.setMaxAge(7 * 24 * 60 * 60);
            // 将cookie保存到客户端
            response.addCookie(cookie);

            // 返回用户信息
            return Msg.success().add("user", user);
        }
    }

    @RequestMapping("/autoLogin")
    @ResponseBody
    public Msg autoLogin(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("请求了autologin");

        HttpSession session = request.getSession();
        // 获取cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            // 遍历cookie
            for (Cookie cookie : cookies) {
                // 如果客户端某个cookie中的name等于之前设置的”autoLogin“，说明该浏览器有设置自动登录的用户
                if (cookie.getName().equals("autoLogin")) {
                    // 取出里面的值，即自动登录的用户名
                    String username = cookie.getValue();
                    // 根据用户名得到该用户信息并登录(即保存用户信息到session)
                    QueryWrapper<User> wrapper = new QueryWrapper<>();
                    wrapper.eq("username", username);
                    User user = userMapper.selectOne(wrapper);
                    System.out.println(user);

                    user.setPassword(null);
                    session.setAttribute("user", user);
                    session.setMaxInactiveInterval(7 * 24 * 60 * 60);
                    return Msg.success().add("user", user);
                }
            }
            // 如果没有需要自动登录的用户
            return Msg.fail();
        }
        // 如果cookie为空
        return Msg.fail();
    }

    @GetMapping("/logout")
    @ResponseBody
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();

        // 重写autoLogin的信息，将cookie的生命周期设置为0(即销毁)
        Cookie cookie = new Cookie("autoLogin", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        // 重写后返回给客户端
        response.addCookie(cookie);
    }

    /**
     * <点击qq按钮跳转到qq登录页面>
     */
    @RequestMapping ("/qq/oauth")
    public void qqLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("请求qq登录页面");

        response.setContentType("text/html;charset=utf-8");
        try {
            //将页面重定向到qq第三方的登录页面
            response.sendRedirect(new Oauth().getAuthorizeURL(request));
        } catch (QQConnectException e) {
            e.printStackTrace();
        }
    }

    /**
     * qq登录成功后的回调地址
     * <获取登录者的基础信息>
     *
     * @return*/
    @RequestMapping("/loginByQQ")
    public void QQAfterlogin(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        response.setContentType("ext/html;charset=UTF-8");

        try {
            AccessToken accessTokenObj = (new Oauth()).getAccessTokenByRequest(request);

            String accessToken   = null,
                    openID        = null;
            long tokenExpireIn = 0L;




            if (accessTokenObj.getAccessToken().equals("")) {
//                我们的网站被CSRF攻击了或者用户取消了授权
//                做一些数据统计工作
                System.out.print("没有获取到响应参数");
            } else {
                accessToken = accessTokenObj.getAccessToken();
                tokenExpireIn = accessTokenObj.getExpireIn();

                request.getSession().setAttribute("demo_access_token", accessToken);
                request.getSession().setAttribute("demo_token_expirein", String.valueOf(tokenExpireIn));

                // 利用获取到的accessToken 去获取当前用的openid -------- start
                OpenID openIDObj =  new OpenID(accessToken);
                openID = openIDObj.getUserOpenID();

                System.out.println("欢迎你，代号为 " + openID + " 的用户!");
                // 利用获取到的accessToken 去获取当前用户的openid --------- end



                UserInfo qzoneUserInfo = new UserInfo(accessToken, openID);
                UserInfoBean userInfoBean = qzoneUserInfo.getUserInfo();

                String nickname = null,avatar=null;

                if (userInfoBean.getRet() == 0) {
                    nickname = userInfoBean.getNickname();
                    avatar = userInfoBean.getAvatar().getAvatarURL50();
                    model.addAttribute("nickname",nickname);

                } else {
                    System.out.println("很抱歉，我们没能正确获取到您的信息，原因是： " + userInfoBean.getMsg());
                }

                // 检测该qq用户是否已经登录过
                QueryWrapper<User> wrapper = new QueryWrapper<>();
                wrapper.eq("username",openID);
                User user = userMapper.selectOne(wrapper);
                if (user==null){
                    // 未登录过，信息存入数据库
                    user = new User(null, openID, "1", false, avatar,
                            new Date(), new Date(), null, true, nickname);
                    userMapper.insert(user);

                }else{
                    user.setAvator(avatar);
                    user.setNickname(nickname);
                    userMapper.updateByPrimaryKeySelective(user);
                }
                // 注册完直接登录
                user.setPassword(null);
                request.getSession().setAttribute("user", user);
                request.getSession().setMaxInactiveInterval(7 * 24 * 60 * 60);

                // 新建一个name为autoLogin的cookie并存放该用户名信息
                Cookie cookie = new Cookie("autoLogin", user.getUsername());
                // 设置作用域为根目录下所有
                cookie.setPath("/");
                // 设置cookie生命周期为一周
                cookie.setMaxAge(7 * 24 * 60 * 60);
                // 将cookie保存到客户端
                response.addCookie(cookie);

                // 返回用户信息
                response.sendRedirect("main");

            }
        } catch (QQConnectException e) {
        }
    }
}