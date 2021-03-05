/**
 * Copyright (C), 2015-2021, XXX有限公司
 * FileName: NotFoundException
 * Author:   1056427550
 * Date:     2021-1-21 21:54
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.personal.blog;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author 1056427550
 * @create 2021-1-21
 * @since 1.0.0
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundExcepiton extends RuntimeException {
    public NotFoundExcepiton() {
    }
    public NotFoundExcepiton(String message) {
        super(message);
    }
    public NotFoundExcepiton(String message, Throwable cause) {
        super(message, cause);
    }
}