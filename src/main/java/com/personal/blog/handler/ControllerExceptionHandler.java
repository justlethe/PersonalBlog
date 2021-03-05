/**
 * Copyright (C), 2015-2021, XXX有限公司
 * FileName: ControllerExceptionHandler
 * Author:   1056427550
 * Date:     2021-1-21 20:51
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.personal.blog.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * 〈一句话功能简述〉<br> 
 * 〈用于全局的异常拦截，如404,505错误信息〉
 *
 * @author 1056427550
 * @create 2021-1-21
 * @since 1.0.0
 */
@ControllerAdvice
public class ControllerExceptionHandler {

    /**
     * 创建日记
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 拦截所有exception级别的异常
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView exceptionHander(HttpServletRequest request,Exception e) throws Exception {
        logger.error("请求的URL:　{}, Exception: {}",request.getRequestURL(),e);

        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
            throw e;
        }
        ModelAndView mv = new ModelAndView();
        mv.addObject("url",request.getRequestURL());
        mv.addObject("exception",e);
        mv.setViewName("error/error");
        return mv;
    }

}