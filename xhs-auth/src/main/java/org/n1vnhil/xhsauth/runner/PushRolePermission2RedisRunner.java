package org.n1vnhil.xhsauth.runner;

import cn.hutool.core.collection.CollUtil;
import com.google.protobuf.MapEntry;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.checkerframework.checker.units.qual.A;
import org.n1vnhil.framework.common.util.JsonUtils;
import org.n1vnhil.xhsauth.constant.RedisKeyConstants;
import org.n1vnhil.xhsauth.domain.dataobject.PermissionDO;
import org.n1vnhil.xhsauth.domain.dataobject.RoleDO;
import org.n1vnhil.xhsauth.domain.dataobject.RolePermissionDO;
import org.n1vnhil.xhsauth.domain.mapper.PermissionDOMapper;
import org.n1vnhil.xhsauth.domain.mapper.RoleDOMapper;
import org.n1vnhil.xhsauth.domain.mapper.RolePermissionDOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PushRolePermission2RedisRunner implements ApplicationRunner {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private PermissionDOMapper permissionDOMapper;

    @Autowired
    private RoleDOMapper roleDOMapper;

    @Autowired
    private RolePermissionDOMapper rolePermissionDOMapper;

    private static final String PUSH_PERMISSION_FLAG = "push.permission.flag";

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("=========== 缓存角色权限 ===========");
        try {
            boolean canPush = redisTemplate.opsForValue().setIfAbsent(PUSH_PERMISSION_FLAG, "1", 1, TimeUnit.DAYS);
            if(!canPush) {
                log.info("=========== 角色权限已缓存 ===========");
                return;
            }

            // 获取所有角色
            List<RoleDO> roles = roleDOMapper.selectEnalbedRoles();

            if(CollUtil.isNotEmpty(roles)) {
                List<Long> roleIds = roles.stream().map(RoleDO::getId).toList();
                List<RolePermissionDO> rolePermissionDOS = rolePermissionDOMapper.selectByRoleIds(roleIds);

                Map<Long, List<Long>> roleIdPermissionIdsMap = rolePermissionDOS.stream().collect(
                        Collectors.groupingBy(
                                RolePermissionDO::getRoleId,
                                Collectors.mapping(RolePermissionDO::getPermissionId, Collectors.toList())
                        )
                );

                // 获取所有权限
                List<PermissionDO> permissions = permissionDOMapper.selectAppEnabledList();
                Map<Long, PermissionDO> permissionIdPermissionDOMap = permissions.stream().collect(
                        Collectors.toMap(
                                PermissionDO::getId, permissionDO -> permissionDO
                        )
                );

                // 建立角色和权限关系
                Map<Long, List<PermissionDO>> roleIdPermissionDOMap = new HashMap<>();
                roles.forEach(roleDO -> {
                    Long roleId = roleDO.getId();
                    List<PermissionDO> permissionDOS = new ArrayList<>();
                    roleIdPermissionIdsMap.get(roleId).forEach(permissionId -> {
                        permissionDOS.add(permissionIdPermissionDOMap.get(permissionId));
                    });
                    roleIdPermissionDOMap.put(roleId, permissionDOS);
                });

                roleIdPermissionDOMap.forEach((roleId, permissionDOS) -> {
                    String redisKey = RedisKeyConstants.buildRolePermissionKey(roleId);
                    redisTemplate.opsForValue().set(redisKey, JsonUtils.toJsonString(permissionDOS));
                });
            } else {
                log.info("===========> 角色集合为空");
            }

            log.info("=========== 角色权限缓存完成，服务启动 ===========");
        } catch (Exception e) {
            log.error("===========> 角色权限缓存失败: ", e);
        }
    }
}
