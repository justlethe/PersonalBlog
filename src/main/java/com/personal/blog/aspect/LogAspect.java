/**
 * Copyright (C), 2015-2021, XXX有限公司
 * FileName: LogAspect
 * Author:   1056427550
 * Date:     2021-1-21 22:00
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.personal.blog.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Arrays;
import java.util.Date;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author 1056427550
 * @create 2021-1-21
 * @since 1.0.0
 */
@Aspect
@Component
public class LogAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 定义切面
     * 把所有前台请求拦截下来
     */
    @Pointcut("execution(* com.personal.blog.web.*.*(..))")
    public void log() {
    }

    /**
     * 请求的方法调用前
     */
    @Before("log()")
    public void doBefore(JoinPoint joinPoint) throws IOException {
        // 获取Httpservlet
        ServletRequestAttributes attributes = (ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        // classMethod = 类名+"."+方法名
        String classMethod =
                joinPoint.getSignature().getDeclaringTypeName() + "." +
                        joinPoint.getSignature().getName();
        RequestLog requestLog = new RequestLog(
                // 请求的url
                request.getRequestURL().toString(),
                // 访问者ip
                request.getRemoteAddr(),
                classMethod,
                // 参数
                joinPoint.getArgs()
        );
        System.out.println(requestLog);
        logger.info("---------- doBefore ----------");
        logger.info("请求体 ----- {}",requestLog);
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("log/diary.log",true),"utf-8"));
            System.out.println(new Date() + requestLog.toString());
            out.write(new Date() + requestLog.toString()+"\n");
            out.close();
        } catch (IOException e) {
            System.out.println("写入失败");
        }
    }

    /**
     * 请求的方法调用后
     */
    @After("log()")
    public void doAfter() {
        logger.info("---------- doAfter ----------");
    }

    /**
     * 请求的方法返回的内容拦截
     */
    @AfterReturning(returning = "result",pointcut = "log()")
    public void doAtferReturning(Object result) {

        logger.info("返回的内容 ----- {}",result );
        try {   
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("log/diary.log",true),"utf-8"));
            out.write(new Date() + result.toString()+"\n");
            out.close();
        } catch (IOException e) {
            System.out.println("写入失败");
        }
    }

    /**
     * 存储操作日记内容
     * 请求 url
     * 访问者 ip
     * 调用方法 classMethod
     * 参数 args
     * 返回内容
     *
     */
    private class RequestLog {
        private String url;
        private String ip;
        private String classMethod;
        private Object[] args;

        public RequestLog(String url, String ip, String classMethod, Object[] args) {
            this.url = url;
            this.ip = ip;
            this.classMethod = classMethod;
            this.args = args;
        }

        @Override
        public String toString() {
            return "{" +
                    "url='" + url + '\'' +
                    ", ip='" + ip + '\'' +
                    ", classMethod='" + classMethod + '\'' +
                    ", args=" + Arrays.toString(args) +
                    '}';
        }
    }

}