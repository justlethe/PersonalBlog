package com.personal.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.personal.blog.entity.Category;

/**
 * @author 1056427550
 * @Entity com.personal.blog.entity.Category
 */
public interface CategoryMapper extends BaseMapper<Category> {
    /**
     *
     * @mbg.generated 2021-01-28 22:02:03
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *
     * @mbg.generated 2021-01-28 22:02:03
     */
    int insert(Category record);

    /**
     *
     * @mbg.generated 2021-01-28 22:02:03
     */
    int insertSelective(Category record);

    /**
     *
     * @mbg.generated 2021-01-28 22:02:03
     */
    Category selectByPrimaryKey(Integer id);

    /**
     *
     * @mbg.generated 2021-01-28 22:02:03
     */
    int updateByPrimaryKeySelective(Category record);

    /**
     *
     * @mbg.generated 2021-01-28 22:02:03
     */
    int updateByPrimaryKey(Category record);
}