package com.smartmarket.domain.marketing.service;

import com.smartmarket.domain.marketing.model.entity.MarketingRouteRequestEntity;
import com.smartmarket.domain.marketing.model.entity.MarketingRouteResultEntity;
import com.smartmarket.domain.marketing.model.entity.MarketingStrategyRouterEntity;
import com.smartmarket.domain.marketing.model.entity.UserBehaviorStatEntity;
import com.smartmarket.domain.marketing.model.entity.UserTagEntity;
import com.smartmarket.domain.marketing.model.valobj.RiskLevelVO;
import com.smartmarket.domain.marketing.model.valobj.UserSegmentVO;
import com.smartmarket.domain.marketing.repository.IMarketingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * @description 用户分层驱动的智能奖池策略路由器
 */
@Slf4j
@Service
public class MarketingStrategyRouter implements IMarketingStrategyRouter {

    private static final int SILENT_DAYS = 30;
    private static final int RISK_DAY_RAFFLE_COUNT = 20;
    private static final BigDecimal HIGH_VALUE_CREDIT_AMOUNT = new BigDecimal("1000");

    private final IMarketingRepository marketingRepository;

    public MarketingStrategyRouter(IMarketingRepository marketingRepository) {
        this.marketingRepository = marketingRepository;
    }

    @Override
    public MarketingRouteResultEntity route(MarketingRouteRequestEntity request) {
        MarketingRouteResultEntity fallback = buildFallbackResult(request, "未命中营销路由配置，使用活动默认策略");
        try {
            UserTagEntity userTag = marketingRepository.queryUserTag(request.getUserId());
            UserBehaviorStatEntity behaviorStat = marketingRepository.queryUserBehaviorStat(request.getUserId());
            UserSegmentVO userSegment = decideUserSegment(userTag, behaviorStat);
            RiskLevelVO riskLevel = decideRiskLevel(userTag, behaviorStat);
            MarketingStrategyRouterEntity router = marketingRepository.queryMarketingStrategyRouter(request.getActivityId(), userSegment, riskLevel);
            MarketingRouteResultEntity routeResult = MarketingRouteResultEntity.builder()
                    .userId(request.getUserId())
                    .activityId(request.getActivityId())
                    .defaultStrategyId(request.getDefaultStrategyId())
                    .routedStrategyId(null == router ? request.getDefaultStrategyId() : router.getStrategyId())
                    .userSegment(userSegment)
                    .riskLevel(riskLevel)
                    .routeDesc(null == router ? fallback.getRouteDesc() : router.getRouterDesc())
                    .build();
            marketingRepository.saveMarketingRouteLog(routeResult);
            return routeResult;
        } catch (Exception e) {
            log.warn("营销策略路由失败，回退活动默认策略 userId:{} activityId:{} defaultStrategyId:{}",
                    request.getUserId(), request.getActivityId(), request.getDefaultStrategyId(), e);
            return fallback;
        }
    }

    private MarketingRouteResultEntity buildFallbackResult(MarketingRouteRequestEntity request, String routeDesc) {
        return MarketingRouteResultEntity.builder()
                .userId(request.getUserId())
                .activityId(request.getActivityId())
                .defaultStrategyId(request.getDefaultStrategyId())
                .routedStrategyId(request.getDefaultStrategyId())
                .userSegment(UserSegmentVO.ACTIVE_USER)
                .riskLevel(RiskLevelVO.LOW)
                .routeDesc(routeDesc)
                .build();
    }

    private UserSegmentVO decideUserSegment(UserTagEntity userTag, UserBehaviorStatEntity behaviorStat) {
        UserSegmentVO tagSegment = parseUserSegment(userTag);
        if (null != tagSegment) {
            return tagSegment;
        }
        if (null == behaviorStat || null == behaviorStat.getTotalRaffleCount() || behaviorStat.getTotalRaffleCount() <= 0) {
            return UserSegmentVO.NEW_USER;
        }
        if (isHighValueUser(behaviorStat)) {
            return UserSegmentVO.HIGH_VALUE_USER;
        }
        if (isSilentUser(behaviorStat)) {
            return UserSegmentVO.SILENT_USER;
        }
        return UserSegmentVO.ACTIVE_USER;
    }

    private RiskLevelVO decideRiskLevel(UserTagEntity userTag, UserBehaviorStatEntity behaviorStat) {
        RiskLevelVO tagRiskLevel = parseRiskLevel(userTag);
        if (null != tagRiskLevel) {
            return tagRiskLevel;
        }
        if (null != behaviorStat && null != behaviorStat.getDayRaffleCount() && behaviorStat.getDayRaffleCount() >= RISK_DAY_RAFFLE_COUNT) {
            return RiskLevelVO.HIGH;
        }
        return RiskLevelVO.LOW;
    }

    private UserSegmentVO parseUserSegment(UserTagEntity userTag) {
        if (null == userTag || null == userTag.getTagCode()) {
            return null;
        }
        try {
            return UserSegmentVO.valueOf(userTag.getTagCode());
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private RiskLevelVO parseRiskLevel(UserTagEntity userTag) {
        if (null == userTag || null == userTag.getRiskLevel()) {
            return null;
        }
        try {
            return RiskLevelVO.valueOf(userTag.getRiskLevel());
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private boolean isHighValueUser(UserBehaviorStatEntity behaviorStat) {
        return null != behaviorStat.getAvailableCreditAmount()
                && behaviorStat.getAvailableCreditAmount().compareTo(HIGH_VALUE_CREDIT_AMOUNT) >= 0;
    }

    private boolean isSilentUser(UserBehaviorStatEntity behaviorStat) {
        if (null == behaviorStat.getLastActiveTime()) {
            return false;
        }
        long silentMillis = System.currentTimeMillis() - behaviorStat.getLastActiveTime().getTime();
        return silentMillis >= TimeUnit.DAYS.toMillis(SILENT_DAYS);
    }

}
