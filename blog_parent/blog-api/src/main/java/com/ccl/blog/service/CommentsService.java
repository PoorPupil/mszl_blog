package com.ccl.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ccl.blog.dao.pojo.Comment;
import com.ccl.blog.vo.Result;
import com.ccl.blog.vo.params.CommentParam;

public interface CommentsService extends IService<Comment> {
    /**
     * 通过文章id查询对应的评论
     * @param id
     * @return
     */
    Result commentsByArticleId(Long id);

    Result comment(CommentParam commentParam);
}
