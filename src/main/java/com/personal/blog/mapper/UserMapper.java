package com.personal.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.personal.blog.entity.User;

/**
 * @author 1056427550
 * @Entity com.personal.blog.entity.User
 */
public interface UserMapper extends BaseMapper<User> {
    /**
     *
     * @mbg.generated 2021-02-02 23:50:30
     */
    int deleteByPrimaryKey(Integer id);


    /**
     *
     * @mbg.generated 2021-02-02 23:50:30
     */
    int insertSelective(User record);

    /**
     *
     * @mbg.generated 2021-02-02 23:50:30
     */
    User selectByPrimaryKey(Integer id);

    /**
     *
     * @mbg.generated 2021-02-02 23:50:30
     */
    int updateByPrimaryKeySelective(User record);

    /**
     *
     * @mbg.generated 2021-02-02 23:50:30
     */
    int updateByPrimaryKey(User record);
}