/**
 * Copyright (C), 2015-2021, XXX有限公司
 * FileName: WebConfig
 * Author:   1056427550
 * Date:     2021-1-31 3:52
 * Description: 登录前具体拦截什么请求的配置
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.personal.blog.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 〈一句话功能简述〉<br> 
 * 〈登录前具体拦截什么请求的配置〉
 *
 * @author 1056427550
 * @create 2021-1-31
 * @since 1.0.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/admin")
                .addPathPatterns("/input");
    }
}