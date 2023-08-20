package com.ccl.blog.service;

import com.ccl.blog.dao.pojo.SysUser;
import com.ccl.blog.vo.Result;
import com.ccl.blog.vo.params.LoginParams;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface LoginService {
    Result login(LoginParams loginParams);

    SysUser checkToken(String token);

    /**
     * 退出登录
     * @param token
     * @return
     */
    Result logout(String token);

    /**
     * 注册方法
     * @param loginParams
     * @return
     */
    Result register(LoginParams loginParams);
}
