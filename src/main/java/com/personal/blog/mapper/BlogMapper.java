package com.personal.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.personal.blog.entity.Blog;

/**
 * @author 1056427550
 * @Entity com.personal.blog.entity.Blog
 */
public interface BlogMapper extends BaseMapper<Blog> {
    /**
     *
     * @mbg.generated 2021-01-28 22:02:03
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *
     * @mbg.generated 2021-01-28 22:02:03
     */
    int insert(Blog record);

    /**
     *
     * @mbg.generated 2021-01-28 22:02:03
     */
    int insertSelective(Blog record);

    /**
     *
     * @mbg.generated 2021-01-28 22:02:03
     */
    Blog selectByPrimaryKey(Integer id);

    /**
     *
     * @mbg.generated 2021-01-28 22:02:03
     */
    int updateByPrimaryKeySelective(Blog record);

    /**
     *
     * @mbg.generated 2021-01-28 22:02:03
     */
    int updateByPrimaryKey(Blog record);
}
