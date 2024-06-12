package com.project.springbootinit.manager;

import com.project.springbootinit.common.ErrorCode;
import com.project.springbootinit.exception.BusinessException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *  专门提供 RedisLimit 限流基础服务的 (提供了通用的能力)
 */

@Service
public class RedisLimitManager {

    @Resource
    private RedissonClient redissonClient;

    public void doRateLimit(String key){
        //  创建一个名称为user_limiter的限流器 每秒最多5次
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);

        //  这里定义的是可操作的令牌数
        boolean canOp = rateLimiter.tryAcquire(1);
        if (!canOp)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }
}
