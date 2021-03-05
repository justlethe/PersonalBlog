/**
 * Copyright (C), 2015-2021, XXX有限公司
 * FileName: ServletInitializer
 * Author:   1056427550
 * Date:     2021-3-3 15:02
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.personal.blog;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author 1056427550
 * @create 2021-3-3
 * @since 1.0.0
 */
public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {

        return builder.sources(BlogApplication.class);
    }

}