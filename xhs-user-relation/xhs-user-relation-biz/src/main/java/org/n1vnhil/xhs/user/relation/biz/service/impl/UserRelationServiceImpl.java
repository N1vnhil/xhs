package org.n1vnhil.xhs.user.relation.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import com.mysql.cj.x.protobuf.MysqlxCrud;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.n1vnhil.framework.common.exception.BizException;
import org.n1vnhil.framework.common.response.PageResponse;
import org.n1vnhil.framework.common.response.Response;
import org.n1vnhil.framework.common.util.DateUtils;
import org.n1vnhil.framework.common.util.JsonUtils;
import org.n1vnhil.framework.context.holder.LoginUserContextHolder;
import org.n1vnhil.xhs.user.dto.resp.FindUserByIdRspDTO;
import org.n1vnhil.xhs.user.relation.biz.constant.MQConstants;
import org.n1vnhil.xhs.user.relation.biz.constant.RedisKeyConstants;
import org.n1vnhil.xhs.user.relation.biz.domain.dataobject.FollowingDO;
import org.n1vnhil.xhs.user.relation.biz.domain.mapper.FollowingDOMapper;
import org.n1vnhil.xhs.user.relation.biz.enums.LuaResultEnum;
import org.n1vnhil.xhs.user.relation.biz.enums.ResponseCodeEnum;
import org.n1vnhil.xhs.user.relation.biz.model.dto.FollowUserMqDTO;
import org.n1vnhil.xhs.user.relation.biz.model.dto.UnfollowUserMqDTO;
import org.n1vnhil.xhs.user.relation.biz.model.vo.FindFollowingListReqVO;
import org.n1vnhil.xhs.user.relation.biz.model.vo.FindFollowingUserRspVO;
import org.n1vnhil.xhs.user.relation.biz.model.vo.FollowUserReqVO;
import org.n1vnhil.xhs.user.relation.biz.model.vo.UnfollowUserReqVO;
import org.n1vnhil.xhs.user.relation.biz.rpc.UserRpcService;
import org.n1vnhil.xhs.user.relation.biz.service.UserRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
public class UserRelationServiceImpl implements UserRelationService {

    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private UserRpcService userRpcService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private FollowingDOMapper followingDOMapper;

    public Response<?> follow(FollowUserReqVO followUserReqVO) {
        Long userId = LoginUserContextHolder.getLoginUserId();
        Long followId = followUserReqVO.getFollowUserId();

        // 校验关注对象是否为自身
        if(Objects.equals(userId, followId)) {
            throw new BizException(ResponseCodeEnum.CANT_FOLLOW_YOUR_SELF);
        }

        // 校验关注对象是否为空
        FindUserByIdRspDTO findUserByIdRspDTO = userRpcService.getUserById(followId);
        if(Objects.isNull(findUserByIdRspDTO)) {
            throw new BizException(ResponseCodeEnum.FOLLOW_USER_NOT_EXISTED);
        }

        // 查询 redis
        String key = RedisKeyConstants.buildUserFollowingKey(userId);
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        /**
         * follow_check_and_add.lua
         * 1. 校验缓存是否存在
         * 2. 校验是否已经关注 followId
         * 3. 校验用户关注人数是否到达上限 (1000)
         * 4. 添加当前用户到 redis
         */
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource("/lua/follow_check_and_add.lua")));
        script.setResultType(Long.class);
        LocalDateTime now = LocalDateTime.now();
        long timestamp = DateUtils.localDateTime2Timestamp(now);
        Long result = redisTemplate.execute(script, Collections.singletonList(key), followId, timestamp);
        checkLuaScriptResult(result);

        // redis 缓存未命中时，查询数据库
        if(Objects.equals(result, LuaResultEnum.ZSET_NOT_EXIST.getCode())) {
            List<FollowingDO> followingDOS = followingDOMapper.selectFollowUserByUserId(userId);
            long expireTime = 60*60*24 + RandomUtil.randomInt(60*60*24);

            DefaultRedisScript<Long> script1 = new DefaultRedisScript<>();
            script1.setResultType(Long.class);
            if(CollUtil.isEmpty(followingDOS)) {
                // 记录为空时，ZADD
                script1.setScriptSource(new ResourceScriptSource(new ClassPathResource("/lua/follow_add_and_expire.lua")));
                redisTemplate.execute(script1, Collections.singletonList(key), followId, timestamp, expireTime);
            } else {
                // 关注列表不为空，同步关注列表到redis，再执行follow_check_and_add脚本
                Object[] args = buildLuaArgs(followingDOS, expireTime);
                script1.setScriptSource(new ResourceScriptSource(new ClassPathResource("/lua/follow_batch_add_and_expire.lua")));
                redisTemplate.execute(script1, Collections.singletonList(key), args);

                // 插入数据
                result = redisTemplate.execute(script, Collections.singletonList(key), followId, timestamp);
                checkLuaScriptResult(result);
            }
        }

        // 发送MQ
        FollowUserMqDTO followUserMqDTO = FollowUserMqDTO.builder()
                .userId(userId)
                .followId(followId)
                .createTime(now)
                .build();
        Message<String> message = MessageBuilder.withPayload(JsonUtils.toJsonString(followUserMqDTO)).build();
        String destination = MQConstants.TOPIC_FOLLOW_OR_UNFOLLOW + ":" + MQConstants.TAG_FOLLOW;
        log.info("==========> MQ：发送关注消息，消息体：{}", followUserMqDTO);
        String hashKey = String.valueOf(userId);
        rocketMQTemplate.asyncSendOrderly(destination, message, hashKey, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("==========> MQ：关注发送成功, Result：{}", sendResult);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("==========> MQ：关注发送异常", throwable);
            }
        });

        return Response.success();
    }

    @Override
    public Response<?> unfollow(UnfollowUserReqVO unfollowUserReqVO) {
        Long userId = LoginUserContextHolder.getLoginUserId();
        Long unfollowUserId = unfollowUserReqVO.getUnfollowUserId();

        // 校验取关对象是否为自己
        if(Objects.equals(userId, unfollowUserId)) {
            throw new BizException(ResponseCodeEnum.CANT_UNFOLLOW_YOUR_SELF);
        }

        // 校验取关用户是否存在
        FindUserByIdRspDTO findUserByIdReqDTO = userRpcService.getUserById(userId);
        if(Objects.isNull(findUserByIdReqDTO)) throw new BizException(ResponseCodeEnum.FOLLOW_USER_NOT_EXISTED);

        // 校验并取关用户
        String followingRedisKey = RedisKeyConstants.buildUserFollowingKey(userId);
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource("/lua/unfollow_check_and_delete.lua")));
        script.setResultType(Long.class);
        Long result = redisTemplate.execute(script, Collections.singletonList(followingRedisKey), unfollowUserId);

        // 校验Lua脚本结果
        // 未关注用户
        if(Objects.equals(result, LuaResultEnum.NOT_FOLLOWED.getCode())) {
            throw new BizException(ResponseCodeEnum.NOT_FOLLOWED);
        }

        //  关注列表缓存不存在
        if(Objects.equals(result, LuaResultEnum.ZSET_NOT_EXIST.getCode())) {
            List<FollowingDO> followingDOS = followingDOMapper.selectFollowUserByUserId(userId);
            long expireTime = 60*60*24 + RandomUtil.randomInt(60*60*24);
            if(CollUtil.isEmpty(followingDOS)) {
                throw new BizException(ResponseCodeEnum.NOT_FOLLOWED);
            } else {
                // 同步缓存到redis
                Object[] args = buildLuaArgs(followingDOS, expireTime);
                DefaultRedisScript<Long> script3 = new DefaultRedisScript<>();
                script3.setResultType(Long.class);
                script3.setScriptSource(new ResourceScriptSource(new ClassPathResource("/lua/follow_batch_add_and_expire.lua")));
                redisTemplate.execute(script3, Collections.singletonList(followingRedisKey), args);

                // 校验并取关用户
                result = redisTemplate.execute(script, Collections.singletonList(followingRedisKey), unfollowUserId);
                if(Objects.equals(result, LuaResultEnum.NOT_FOLLOWED.getCode())) {
                    throw new BizException(ResponseCodeEnum.NOT_FOLLOWED);
                }
            }
        }

        // 发送MQ消息
        UnfollowUserMqDTO unfollowUserMqDTO = UnfollowUserMqDTO.builder()
                .userId(userId)
                .unfollowUserId(unfollowUserId)
                .createTime(LocalDateTime.now())
                .build();

        Message<String> message = MessageBuilder.withPayload(JsonUtils.toJsonString(unfollowUserMqDTO)).build();
        String destination = MQConstants.TOPIC_FOLLOW_OR_UNFOLLOW + ":" + MQConstants.TAG_UNFOLLOW;

        String hashKey = String.valueOf(userId);
        rocketMQTemplate.asyncSendOrderly(destination, message, hashKey, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("==========> MQ: 取关消息发送成功, result: {}", sendResult);
            }

            @Override
            public void onException(Throwable throwable) {
                log.info("==========> MQ: 取关消息发送失败", throwable);
            }
        });

        return Response.success();
    }

    @Override
    public PageResponse<FindFollowingUserRspVO> findFollowingUserList(FindFollowingListReqVO findFollowingListReqVO) {
        Long userId = findFollowingListReqVO.getUserId();
        Integer pageNo = findFollowingListReqVO.getPage();
        String redisKey = RedisKeyConstants.buildUserFollowingKey(userId);
        long total = redisTemplate.opsForZSet().zCard(redisKey);
        long limit = 10;
        List<FindFollowingUserRspVO> findFollowingUserRspVOS = null;
        if(total > 0) {
            long totalPage = PageResponse.getTotalPages(total, limit);

            if(pageNo > totalPage) return PageResponse.success(null, pageNo, total);
            long offset = (pageNo - 1) * limit;
            Set<Object> followingUserIdsSet = redisTemplate.opsForZSet()
                    .reverseRangeByScore(redisKey, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, offset, limit);
            if(CollUtil.isNotEmpty(followingUserIdsSet)) {
                List<Long> userIds = followingUserIdsSet.stream().map(o -> Long.valueOf(o.toString())).toList();
                List<FindUserByIdRspDTO> findUserByIdRspDTOS = userRpcService.getUserByIds(userIds);
                if(CollUtil.isNotEmpty(findUserByIdRspDTOS)) {
                    findFollowingUserRspVOS = findUserByIdRspDTOS.stream().map(
                            findUserByIdRspDTO -> FindFollowingUserRspVO.builder()
                                    .userId(findUserByIdRspDTO.getId())
                                    .avatar(findUserByIdRspDTO.getAvatar())
                                    .introduction(findUserByIdRspDTO.getIntroduction())
                                    .nickname(findUserByIdRspDTO.getNickname())
                                    .build()).toList();
                }
            }
        } else {
            // 缓存未命中，查询数据库
            long count = followingDOMapper.countByUserId(userId);
            long totalPages = PageResponse.getTotalPages(count, limit);
            if(pageNo > totalPages) return PageResponse.success(null, pageNo, count);
            long offset = PageResponse.getOffset(pageNo, limit);
            List<FollowingDO> followingDOS = followingDOMapper.pageSelect(userId, offset, limit);

            // 同步查询结果到redis
            if(CollUtil.isNotEmpty(followingDOS)) {
                List<Long> userIds = followingDOS.stream().map(FollowingDO::getUserId).toList();
                findFollowingUserRspVOS = rpcUserServiceAndDTO2VO(userIds, findFollowingUserRspVOS);
                threadPoolTaskExecutor.execute(() -> syncFollowingList2Redis(userId));
            }
        }

        return PageResponse.success(findFollowingUserRspVOS, pageNo, total);
    }

    /**
     * 检查lua脚本结果
     * @param result
     */
    private static void checkLuaScriptResult(Long result) {
        LuaResultEnum luaResultEnum = LuaResultEnum.valueOf(result);
        if(Objects.isNull(luaResultEnum)) throw new RuntimeException("Lua脚本执行错误");
        switch (luaResultEnum) {

            case FOLLOW_LIMIT -> throw new BizException(ResponseCodeEnum.FOLLOWING_COUNT_LIMIT);

            case ALREADY_FOLLOWED -> throw new BizException(ResponseCodeEnum.ALREADY_FOLLOWED);
        }
    }

    /**
     * 构造lua脚本参数
     * @param followingDOS 关注列表
     * @param expireTime 过期时间
     * @return
     */
    private static Object[] buildLuaArgs(List<FollowingDO> followingDOS, long expireTime) {
        int argsLength = followingDOS.size() * 2 + 1;
        Object[] args = new Object[argsLength];
        int i = 0;
        for(FollowingDO followingDO: followingDOS) {
            args[i] = DateUtils.localDateTime2Timestamp(followingDO.getCreateTime());
            args[i + 1] = followingDO.getFollowingUserId();
            i += 2;
        }
        args[argsLength - 1] = expireTime;
        return args;
    }

    /**
     * rpc服务批量查询用户信息，并转换至VO
     * @param userIds
     * @param findFollowingUserRspVOS
     * @return
     */
    private List<FindFollowingUserRspVO> rpcUserServiceAndDTO2VO(List<Long> userIds, List<FindFollowingUserRspVO> findFollowingUserRspVOS) {
        List<FindUserByIdRspDTO> findUserByIdRspDTOS = userRpcService.getUserByIds(userIds);
        if(CollUtil.isNotEmpty(findUserByIdRspDTOS)) {
            findFollowingUserRspVOS = findUserByIdRspDTOS.stream()
                    .map(dto -> FindFollowingUserRspVO.builder()
                            .nickname(dto.getNickname())
                            .avatar(dto.getAvatar())
                            .introduction(dto.getIntroduction())
                            .userId(dto.getId())
                            .build())
                    .toList();
        }
        return findFollowingUserRspVOS;
    }

    /**
     * 异步同步用户数据至redis
     * @param userId
     */
    private void syncFollowingList2Redis(Long userId) {
        List<FollowingDO> followingDOS = followingDOMapper.selectFollowUserByUserId(userId);
        if(CollUtil.isEmpty(followingDOS)) {
            String key = RedisKeyConstants.buildUserFollowingKey(userId);
            long expireTime = 60*60*24 + RandomUtil.randomInt(60*60*24);
            Object[] luaArgs = buildLuaArgs(followingDOS, expireTime);
            DefaultRedisScript<Long> script = new DefaultRedisScript<>();
            script.setResultType(Long.class);
            script.setScriptSource(new ResourceScriptSource(new ClassPathResource("/lua/follow_batch_add_and_expire.lua")));
            redisTemplate.execute(script, Collections.singletonList(key), luaArgs);
        }
    }
}
