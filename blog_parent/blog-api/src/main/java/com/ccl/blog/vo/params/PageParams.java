package com.ccl.blog.vo.params;

import lombok.Data;

/**
 * 这个类是设置翻页的信息类
 */
@Data
public class PageParams {
    // 默认当前页为第一页
    private Integer page = 1;
    // 默认每页条数为10条
    private Integer pageSize = 10;

    private Long categoryId;

    private Long tagId;

    private String year;

    private String month;

    public String getMonth() {
        if(this.month != null && this.month.length() == 1){
            return "0" + this.month;
        }
        return this.month;
    }

}
