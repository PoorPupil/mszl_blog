package com.ccl.blog.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 这个类是 返回结果类
 */
@Data
@AllArgsConstructor
public class Result {
    // 请求是否成功
    private boolean success;
    // 编码
    private Integer code;
    // 错误信息
    private String msg;
    // 数据信息
    private Object data;
    // 成功时调用的方法
    public static Result success(Object data){
        return new Result(true, 200, "success", data);
    }
    // 失败调用的方法
    public static Result fail(int code, String msg){
        return new Result(false, code, msg, null);
    }

}
