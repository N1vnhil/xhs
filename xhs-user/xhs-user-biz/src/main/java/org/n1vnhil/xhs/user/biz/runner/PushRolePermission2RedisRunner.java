package org.n1vnhil.xhs.user.biz.runner;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.alibaba.nacos.shaded.com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.n1vnhil.framework.common.util.JsonUtils;
import org.n1vnhil.xhs.user.biz.constant.RedisKeyConstants;
import org.n1vnhil.xhs.user.biz.domain.dataobject.PermissionDO;
import org.n1vnhil.xhs.user.biz.domain.dataobject.RoleDO;
import org.n1vnhil.xhs.user.biz.domain.dataobject.RolePermissionDO;
import org.n1vnhil.xhs.user.biz.domain.mapper.PermissionDOMapper;
import org.n1vnhil.xhs.user.biz.domain.mapper.RoleDOMapper;
import org.n1vnhil.xhs.user.biz.domain.mapper.RolePermissionDOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PushRolePermission2RedisRunner implements ApplicationRunner {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String PUSH_PERMISSION_FLAG = "push.permission.flag";

    @Autowired
    private RoleDOMapper roleDOMapper;

    @Autowired
    private RolePermissionDOMapper rolePermissionDOMapper;

    @Autowired
    private PermissionDOMapper permissionDOMapper;

    @Override
    public void run(ApplicationArguments args) {
        log.info("==> 服务启动，开始同步角色权限数据到 Redis 中...");

        try {
            // 是否能够同步数据: 原子操作，只有在键 PUSH_PERMISSION_FLAG 不存在时，才会设置该键的值为 "1"，并设置过期时间为 1 天
            boolean canPushed = redisTemplate.opsForValue().setIfAbsent(PUSH_PERMISSION_FLAG, "1", 1, TimeUnit.DAYS);

            // 如果无法同步权限数据
            if (!canPushed) {
                log.warn("==> 角色权限数据已经同步至 Redis 中，不再同步...");
                return;
            }

            // 查询出所有角色
            List<RoleDO> roleDOS = roleDOMapper.selectEnabledRoles();

            if (CollUtil.isNotEmpty(roleDOS)) {
                // 拿到所有角色的 ID
                List<Long> roleIds = roleDOS.stream().map(RoleDO::getId).toList();

                // 根据角色 ID, 批量查询出所有角色对应的权限
                List<RolePermissionDO> rolePermissionDOS = rolePermissionDOMapper.selectByRoleIds(roleIds);
                // 按角色 ID 分组, 每个角色 ID 对应多个权限 ID
                Map<Long, List<Long>> roleIdPermissionIdsMap = rolePermissionDOS.stream().collect(
                        Collectors.groupingBy(RolePermissionDO::getRoleId,
                                Collectors.mapping(RolePermissionDO::getPermissionId, Collectors.toList()))
                );

                // 查询 APP 端所有被启用的权限
                List<PermissionDO> permissionDOS = permissionDOMapper.selectAppEnabledList();
                // 权限 ID - 权限 DO
                Map<Long, PermissionDO> permissionIdDOMap = permissionDOS.stream().collect(
                        Collectors.toMap(PermissionDO::getId, permissionDO -> permissionDO)
                );

                // 组织 角色-权限 关系
                Map<String, List<String>> roleKeyPermissionsMap = Maps.newHashMap();

                // 循环所有角色
                roleDOS.forEach(roleDO -> {
                    // 当前角色 ID
                    Long roleId = roleDO.getId();
                    // 当前角色 roleKey
                    String roleKey = roleDO.getRoleKey();
                    // 当前角色 ID 对应的权限 ID 集合
                    List<Long> permissionIds = roleIdPermissionIdsMap.get(roleId);
                    if (CollUtil.isNotEmpty(permissionIds)) {
                        List<String> permissionKeys = Lists.newArrayList();
                        permissionIds.forEach(permissionId -> {
                            // 根据权限 ID 获取具体的权限 DO 对象
                            PermissionDO permissionDO = permissionIdDOMap.get(permissionId);
                            permissionKeys.add(permissionDO.getPermissionKey());
                        });
                        roleKeyPermissionsMap.put(roleKey, permissionKeys);
                    }
                });

                // 同步至 Redis 中，方便后续网关查询 Redis, 用于鉴权
                roleKeyPermissionsMap.forEach((roleKey, permissions) -> {
                    String key = RedisKeyConstants.buildRolePermissionKey(roleKey);
                    redisTemplate.opsForValue().set(key, JsonUtils.toJsonString(permissions));
                });
            }

            log.info("==> 服务启动，成功同步角色权限数据到 Redis 中...");
        } catch (Exception e) {
            log.error("==> 同步角色权限数据到 Redis 中失败: ", e);
        }

    }
}
