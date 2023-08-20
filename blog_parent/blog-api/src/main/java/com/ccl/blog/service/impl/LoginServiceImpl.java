package com.ccl.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.ccl.blog.dao.pojo.SysUser;
import com.ccl.blog.service.LoginService;
import com.ccl.blog.service.SysUserService;
import com.ccl.blog.utils.JWTUtils;
import com.ccl.blog.vo.Result;
import com.ccl.blog.vo.params.LoginParams;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    private static final String slat = "mszlu!@#";
    /**
     * 登录功能的实现
     */
    @Override
    public Result login(LoginParams loginParams) {
        // 1、 检查参数是否合法
        // 2、 根据用户名和密码去user表中查询 是否存在
        // 3、 如果不存在，登录失败
        // 4、 如果存在，使用jwt 生成 token ，返回给前端
        // 5、 token 放入redis中，redis token:user 信息 设置过期时间（登录认证的时候，先认证token字符串是否合法，去redis认证是否存在）
        String account = loginParams.getAccount();
        String password = loginParams.getPassword();
        if(StringUtils.isBlank(account) || StringUtils.isBlank(password)){
            return Result.fail(10001,"参数有误");
        }
        password = DigestUtils.md5Hex(password + slat);
        SysUser sysUser =  sysUserService.findUser(account,password);
        if(sysUser == null) {
            return Result.fail(10002,"不存在该用户");
        }

        String token = JWTUtils.createToken(sysUser.getId());
        redisTemplate.opsForValue().set("TOKEN_"+token, JSON.toJSONString(sysUser),1, TimeUnit.DAYS);
        return Result.success(token);
    }

    @Override
    public SysUser checkToken(String token) {
        if(StringUtils.isBlank(token)){
            return null;
        }
        Map<String, Object> stringObjectMap = JWTUtils.checkToken(token);
        if(stringObjectMap == null){
            return null;
        }
        String userJson = redisTemplate.opsForValue().get("TOKEN_" + token);
        if(userJson == null){
            return null;
        }
        SysUser sysUser = JSON.parseObject(userJson, SysUser.class);
        return sysUser;
    }

    @Override
    public Result logout(String token) {
        // 1、 redis删除对应的 token
        redisTemplate.delete("TOKEN_"+token);
        return Result.success(null);
    }

    @Override
    public Result register(LoginParams loginParams) {
        // 1.参数的合法性校验
        String account = loginParams.getAccount();
        String password = loginParams.getPassword();
        String nickname = loginParams.getNickname();
        if(StringUtils.isBlank(account) && StringUtils.isBlank(password) && StringUtils.isBlank(nickname)) {
            return Result.fail(10003,"注册信息不能为空或者空格");
        }
        // 2.查询账号是否已经被注册
        SysUser sysUser = sysUserService.findUserByAccount(account);
        if(sysUser != null) {
            return Result.fail(10004,"该账号已被注册");
        }
        // 3.填充属性，注册用户
        sysUser = new SysUser();
        sysUser.setNickname(nickname);
        sysUser.setAccount(account);
        sysUser.setPassword(DigestUtils.md5Hex(password + slat));
        sysUser.setCreateDate(System.currentTimeMillis());
        sysUser.setLastLogin(System.currentTimeMillis());
        sysUser.setAvatar("/static/img/logo.b3a48c0.png");
        sysUser.setAdmin(1);
        sysUser.setDeleted(0);
        sysUser.setSalt("");
        sysUser.setStatus("");
        sysUser.setEmail("");
        this.sysUserService.save(sysUser);

        return null;
    }


}
