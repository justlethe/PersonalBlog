package com.personal.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.personal.blog.entity.Comment;

/**
 * @author 1056427550
 * @Entity com.personal.blog.entity.Comment
 */
public interface CommentMapper extends BaseMapper<Comment> {
    /**
     *
     * @mbg.generated 2021-01-28 22:02:03
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *
     * @mbg.generated 2021-01-28 22:02:03
     */
    int insert(Comment record);

    /**
     *
     * @mbg.generated 2021-01-28 22:02:03
     */
    int insertSelective(Comment record);

    /**
     *
     * @mbg.generated 2021-01-28 22:02:03
     */
    Comment selectByPrimaryKey(Integer id);

    /**
     *
     * @mbg.generated 2021-01-28 22:02:03
     */
    int updateByPrimaryKeySelective(Comment record);

    /**
     *
     * @mbg.generated 2021-01-28 22:02:03
     */
    int updateByPrimaryKey(Comment record);
}
