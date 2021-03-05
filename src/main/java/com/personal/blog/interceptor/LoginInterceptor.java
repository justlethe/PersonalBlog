/**
 * Copyright (C), 2015-2021, XXX有限公司
 * FileName: LoginInterceptor
 * Author:   1056427550
 * Date:     2021-1-31 3:46
 * Description: 未登录前不可访问某某功能的拦截器
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.personal.blog.interceptor;

import net.sf.json.JSONObject;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 〈一句话功能简述〉<br> 
 * 〈未登录前不可访问某某功能或页面的拦截器〉
 *
 * @author 1056427550
 * @create 2021-1-31
 * @since 1.0.0
 */
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        JSONObject permission = JSONObject.fromObject(request.getSession().getAttribute("user"));

        if (request.getSession().getAttribute("user") == null || permission.getString("permission").equals("false")){
            response.sendRedirect("/");
            return false;
        }else {
            System.out.println(request.getSession().getAttribute("user"));

            return true;
        }
    }
}