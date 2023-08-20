package com.ccl.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ccl.blog.dao.pojo.SysUser;
import com.ccl.blog.vo.Result;
import com.ccl.blog.vo.UserVo;

public interface SysUserService extends IService<SysUser> {

    UserVo findUserVoById(Long id);

    SysUser findUserById(Long id);

    SysUser findUser(String account, String password);

    Result findUserByToken(String token);

    SysUser findUserByAccount(String account);
}
