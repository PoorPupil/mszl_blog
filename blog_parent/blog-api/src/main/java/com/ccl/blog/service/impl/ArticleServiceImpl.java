package com.ccl.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ccl.blog.dao.dos.Archives;
import com.ccl.blog.dao.mapper.ArticleBodyMapper;
import com.ccl.blog.dao.mapper.ArticleMapper;
import com.ccl.blog.dao.mapper.ArticleTagMapper;
import com.ccl.blog.dao.pojo.Article;
import com.ccl.blog.dao.pojo.ArticleBody;
import com.ccl.blog.dao.pojo.ArticleTag;
import com.ccl.blog.dao.pojo.SysUser;
import com.ccl.blog.service.*;
import com.ccl.blog.utils.UserThreadLocal;
import com.ccl.blog.vo.ArticleBodyVo;
import com.ccl.blog.vo.ArticleVo;
import com.ccl.blog.vo.Result;
import com.ccl.blog.vo.TagVo;
import com.ccl.blog.vo.params.ArticleParam;
import com.ccl.blog.vo.params.PageParams;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private TagService tagService;
    @Autowired
    private ThreadService threadService;
    @Autowired
    private ArticleTagMapper articleTagMapper;


    @Override
    public Result listArticle(PageParams pageParams){
        Page<Article> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
        IPage<Article> articleIPage = articleMapper.listArticle(
                page,
                pageParams.getCategoryId(),
                pageParams.getTagId(), pageParams.getYear(),
                pageParams.getMonth());
        List<Article> records = articleIPage.getRecords();
        return Result.success(copyList(records,true,true));
    }

    /*@Override
    public Result listArticle(PageParams pageParams) {
        // 1 、 分页查询 article 数据库表
        Page<Article> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
        // 条件构造器
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();

        if (pageParams.getCategoryId() != null) {
            queryWrapper.eq(Article::getCategoryId, pageParams.getCategoryId());
        }

        List<Long> articleIdList = new ArrayList<>();
        if (pageParams.getTagId() != null) {
            LambdaQueryWrapper<ArticleTag> articlaTagLambdaQueryWrapper = new LambdaQueryWrapper<>();
            articlaTagLambdaQueryWrapper.eq(ArticleTag::getTagId,pageParams.getTagId());
            List<ArticleTag> articleTags = articleTagMapper.selectList(articlaTagLambdaQueryWrapper);
            for (ArticleTag articleTag : articleTags) {
                articleIdList.add(articleTag.getArticleId());
            }
            if(articleIdList.size() > 0){
                queryWrapper.in(Article::getId,articleIdList);
            }
        }

        // 先按照权重进行置顶排序
        queryWrapper.orderByDesc(Article::getWeight);
        // 按照时间降序
        queryWrapper.orderByDesc(Article::getCreateDate);
        *//*
        在 mybatis-plus 的条件构造器的排序中——orderByDesc(),其实参数是可以多个的
        如上面的查询条件构造也可以写成这样：
        queryWrapper.orderByDesc(Article::getWeight，Article::getCreateDate);
        但是我个人觉得分开写比较容易看
        *//*
        // 查询结果
        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);
        // 获取数据信息——即 文章列表的内容，数据库查询的结果
        List<Article> articleList = articlePage.getRecords();
        // 需要将 数据库查询的信息转化为前端页面所需要的信息，这里用一个 copyList（）方法包装实现
        List<ArticleVo> articleVoList = copyList(articleList,true, true);
        return Result.success(articleVoList);
    }
*/
    /**
     * 最热文章查询
     * @param limit 查询文章数
     * @return
     */
    @Override
    public Result hotArticle(int limit) {
        // 根据浏览数进行排序
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.orderByDesc(Article::getViewCounts);
        queryWrapper.select(Article::getId, Article::getTitle);
        queryWrapper.last("limit " + limit);
        List<Article> articles = articleMapper.selectList(queryWrapper);

        return Result.success(copyList(articles,false,false));
    }

    /**
     * 最新文章查询
     * @param limit 查询文章数
     * @return
     */
    @Override
    public Result newArticle(int limit) {
        // 根据时间进行排序
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.orderByDesc(Article::getCreateDate);
        queryWrapper.select(Article::getId, Article::getTitle);
        queryWrapper.last("limit " + limit);
        List<Article> articles = articleMapper.selectList(queryWrapper);

        return Result.success(copyList(articles,false,false));
    }

    @Override
    public Result listArchives() {
        List<Archives> archivesList = articleMapper.listArchives();
        return Result.success(archivesList);
    }

    @Override
    public Result findArticleById(Long articleId) {
        // 1、根据id查询用户信息
        // 2、 根据bodyId和categoryid 去做关联查询
        Article article = this.articleMapper.selectById(articleId);
        ArticleVo articleVo = copy(article, true, true,true,true);
        // 查看完文章了，新增阅读数，有没有问题？
        // 查看完文章之后，本因该直接返回数据了，这时候做了一个更新操作，更新时加写锁，阻塞其他的读操作，性能就会比较低
        // 更新 增加了此次接口 耗时 如果一旦更新出现问题，不能影响 查看文章的操作
        // 线程池 可以把更新操作扔到线程池中去执行，和主线程不相干
        threadService.updateArticleViewCount(articleMapper,article);
        return Result.success(articleVo);
    }

    @Override
    public Result publish(ArticleParam articleParam) {
//        构建Article对象
        SysUser sysUser = UserThreadLocal.get();
        Article article = new Article();
        article.setAuthorId(sysUser.getId());
        article.setWeight(Article.Article_Common);
        article.setViewCounts(0);
        article.setTitle(articleParam.getTitle());
        article.setSummary(articleParam.getSummary());
        article.setCommentCounts(0);
        article.setCreateDate(System.currentTimeMillis());
        article.setCategoryId(articleParam.getCategory().getId());
        // 插入文章，生成文章id
        this.articleMapper.insert(article);
        List<TagVo> tags = articleParam.getTags();
        if (tags != null) {
            Long articleId = article.getId();
            for (TagVo tag : tags) {
                ArticleTag articleTag = new ArticleTag();
                articleTag.setTagId(tag.getId());
                articleTag.setArticleId(articleId);
                articleTagMapper.insert(articleTag);
            }
        }
        // body
        ArticleBody articleBody = new ArticleBody();
        articleBody.setArticleId(article.getId());
        articleBody.setContent(articleParam.getBody().getContentHtml());
        articleBody.setContentHtml(articleParam.getBody().getContentHtml());
        articleBodyMapper.insert(articleBody);
        article.setBodyId(articleBody.getId());
        articleMapper.updateById(article);

        Map<String, Object> map = new HashMap<>();
        map.put("id",article.getId().toString());
        return Result.success(map);
    }


    private List<ArticleVo> copyList(List<Article> articleList, boolean isTag, boolean isAuthor) {
        List<ArticleVo> articleVoList = new ArrayList<>();
        for (Article article: articleList
             ) {
            articleVoList.add(copy(article, isTag, isAuthor,false,false));
        }
        return articleVoList;
    }

    private List<ArticleVo> copyList(List<Article> articleList, boolean isTag, boolean isAuthor,boolean isBody,boolean isCategory) {
        List<ArticleVo> articleVoList = new ArrayList<>();
        for (Article article: articleList
        ) {
            articleVoList.add(copy(article, isTag, isAuthor,isBody,isCategory));
        }
        return articleVoList;
    }


    @Autowired
    private CategoryService categoryService;
    /**
     * 通过 spring 提供的对象复制工具 BeanUtils 将一个 article 对象复制到一个新的 articleVo 对象
     * @param article
     * @return
     */
    private ArticleVo copy(Article article, boolean isTag, boolean isAuthor, boolean isBody, boolean isCategory) {
        ArticleVo articleVo = new ArticleVo();
        BeanUtils.copyProperties(article,articleVo);
        // 由于 article 的时间属性是 Long , 而 articleVo 的时间属性是 String，因此是无法通过
        // BeanUtils 直接复制的，需要通过 手工方式实现复制
        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
        //由于 article 实体类有的只是 tag 的 id 和 作者的 id ，并不符合前端的要求，
        // 无法直接和 articleVo 类型直接对应，所以需要通过 id 进行查询然后对应回去
        // 有标签需要展示
        if (isTag) {
            // 通过 article 查询标签id 对名字
            Long articleId = article.getId();
            List<TagVo> tagsByArticleId = tagService.findTagsByArticleId(articleId);
            articleVo.setTags(tagsByArticleId);
        }
        // 如果作者名需要展示
        if (isAuthor) {
            // 通过 article 的用户id查询对应的名字
            Long authorId = article.getAuthorId();
            String nickname = sysUserService.findUserById(authorId).getNickname();
            articleVo.setAuthor(nickname);
        }
        if (isBody) {
            Long bodyId = article.getBodyId();
            articleVo.setBody(findArticleBodyById(bodyId));
        }
        if(isCategory){
            Long categoryId = article.getCategoryId();
            articleVo.setCategorys(categoryService.findCategoryById(categoryId));
        }
        return articleVo;
    }

    @Autowired
    private ArticleBodyMapper articleBodyMapper;


    private ArticleBodyVo findArticleBodyById(Long bodyId) {

        ArticleBody articleBody = articleBodyMapper.selectById(bodyId);
        ArticleBodyVo articleBodyVo = new ArticleBodyVo();
        articleBodyVo.setContent(articleBody.getContent());

        return articleBodyVo;
    }
}
