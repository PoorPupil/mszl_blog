package com.ccl.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ccl.blog.dao.pojo.Tag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    /**
     *  根据文章 id 查询标签列表
     * @param articleId
     * @return
     */
    List<Tag> findTagsByArticleId(Long articleId);

    /**
     * 最热标签查询 的 id
     * @param limit
     * @return
     */
    List<Long> findHostTagIds(int limit);

    /**
     * 根据 id 查询标签对象
     * @param hostTagIds
     * @return
     */
    List<Tag> findTagsByIds(List<Long> hostTagIds);
}
