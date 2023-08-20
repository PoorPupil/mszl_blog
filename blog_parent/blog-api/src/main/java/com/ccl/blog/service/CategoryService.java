package com.ccl.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ccl.blog.dao.pojo.Category;
import com.ccl.blog.vo.CategoryVo;
import com.ccl.blog.vo.Result;

public interface CategoryService extends IService<Category> {
    CategoryVo findCategoryById(Long categoryId);

    /**
     * 查询所有类别
     * @return
     */
    Result findAll();

    Result findAllDetail();

    Result categoryDetailById(Long id);

}
