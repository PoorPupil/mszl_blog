package com.ccl.blog.controller;

import com.ccl.blog.common.aop.LogAnnotation;
import com.ccl.blog.common.cache.Cache;
import com.ccl.blog.service.ArticleService;
import com.ccl.blog.vo.Result;
import com.ccl.blog.vo.params.ArticleParam;
import com.ccl.blog.vo.params.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;


    /**
     * 首页文章列表
     *
     * @param pageParams
     * @return
     */
    @PostMapping
    public Result listArticle(@RequestBody PageParams pageParams) {
        return articleService.listArticle(pageParams);
    }

    /**
     * 最热文章 接口
     *
     * @return
     */
    @PostMapping("/hot")
    @Cache(expire = 5 * 60 * 1000,name = "hot_article")
    public Result hotArticle() {
        int limit = 5;
        return articleService.hotArticle(limit);
    }

    /**
     * 最新文章 接口
     *
     * @return
     */
    @PostMapping("/new")
    public Result newArticle() {
        int limit = 5;
        return articleService.newArticle(limit);
    }

    /**
     * 文章归档
     *
     * @return
     */
    @PostMapping("/listArchives")
    // 加上此注解，代表要对此接口记录日志
    @LogAnnotation(module = "文章", operator = "获取文章列表")
    public Result listArchives() {
        return articleService.listArchives();
    }

    @PostMapping("/view/{id}")
    public Result findArticleById(@PathVariable("id") Long articleId) {
        return articleService.findArticleById(articleId);

    }

    @PostMapping("/publish")
    public Result publish(@RequestBody ArticleParam articleParam) {
        return articleService.publish(articleParam);
    }
}


