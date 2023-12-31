package com.ccl.blog.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ccl.blog.admin.pojo.Permission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    List<Permission> findPermissionsByAdminId(Long adminId);
}