package com.ccl.blog.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.ccl.blog.dao.pojo.Article;
import com.ccl.blog.vo.Result;
import com.ccl.blog.vo.params.ArticleParam;
import com.ccl.blog.vo.params.PageParams;

public interface ArticleService extends IService<Article> {
    /**
     *  分页查询 文章列表
     * @param pageParams
     * @return
     */
    Result listArticle(PageParams pageParams);

    /**
     * 最热文章查询
     * @param limit 查询文章数
     * @return
     */
    Result hotArticle(int limit);

    /**
     * 最新文章查询
     * @param limit 查询文章数
     * @return
     */
    Result newArticle(int limit);

    /**
     * 文章归档
     * @return
     */
    Result listArchives();

    /**
     * 查看文章详情
     * @param articleId
     * @return
     */
    Result findArticleById(Long articleId);

    Result publish(ArticleParam articleParam);

}
