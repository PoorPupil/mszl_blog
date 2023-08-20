package com.ccl.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ccl.blog.dao.pojo.Tag;
import com.ccl.blog.vo.Result;
import com.ccl.blog.vo.TagVo;

import java.util.List;

public interface TagService extends IService<Tag> {

//    通过文章 id 查询对应的标签
    List<TagVo> findTagsByArticleId(Long articleId);

    Result hots(int limit);

    Result findAll();

    Result findAllDetail();

    Result findDetailById(Long id);
}
