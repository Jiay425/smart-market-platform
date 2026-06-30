package com.smartmarket.infrastructure.adapter.repository;

import com.smartmarket.domain.marketing.model.entity.MarketingRouteResultEntity;
import com.smartmarket.domain.marketing.model.entity.MarketingStrategyRouterEntity;
import com.smartmarket.domain.marketing.model.entity.UserBehaviorStatEntity;
import com.smartmarket.domain.marketing.model.entity.UserTagEntity;
import com.smartmarket.domain.marketing.model.valobj.RiskLevelVO;
import com.smartmarket.domain.marketing.model.valobj.UserSegmentVO;
import com.smartmarket.domain.marketing.repository.IMarketingRepository;
import com.smartmarket.infrastructure.dao.IMarketingStrategyRouterDao;
import com.smartmarket.infrastructure.dao.IMarketingStrategyRouterLogDao;
import com.smartmarket.infrastructure.dao.IUserBehaviorStatDao;
import com.smartmarket.infrastructure.dao.IUserTagDao;
import com.smartmarket.infrastructure.dao.po.MarketingStrategyRouter;
import com.smartmarket.infrastructure.dao.po.MarketingStrategyRouterLog;
import com.smartmarket.infrastructure.dao.po.UserBehaviorStat;
import com.smartmarket.infrastructure.dao.po.UserTag;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * @description 营销策略路由仓储服务
 */
@Repository
public class MarketingRepository implements IMarketingRepository {

    @Resource
    private IUserTagDao userTagDao;
    @Resource
    private IUserBehaviorStatDao userBehaviorStatDao;
    @Resource
    private IMarketingStrategyRouterDao marketingStrategyRouterDao;
    @Resource
    private IMarketingStrategyRouterLogDao marketingStrategyRouterLogDao;

    @Override
    public UserTagEntity queryUserTag(String userId) {
        UserTag userTag = userTagDao.queryUserTag(userId);
        if (null == userTag) {
            return null;
        }
        return UserTagEntity.builder()
                .userId(userTag.getUserId())
                .tagCode(userTag.getTagCode())
                .riskLevel(userTag.getRiskLevel())
                .build();
    }

    @Override
    public UserBehaviorStatEntity queryUserBehaviorStat(String userId) {
        UserBehaviorStat userBehaviorStat = userBehaviorStatDao.queryUserBehaviorStat(userId);
        if (null == userBehaviorStat) {
            return null;
        }
        return UserBehaviorStatEntity.builder()
                .userId(userBehaviorStat.getUserId())
                .totalRaffleCount(userBehaviorStat.getTotalRaffleCount())
                .dayRaffleCount(userBehaviorStat.getDayRaffleCount())
                .continuousSignDays(userBehaviorStat.getContinuousSignDays())
                .availableCreditAmount(userBehaviorStat.getAvailableCreditAmount())
                .lastActiveTime(userBehaviorStat.getLastActiveTime())
                .build();
    }

    @Override
    public MarketingStrategyRouterEntity queryMarketingStrategyRouter(Long activityId, UserSegmentVO userSegment, RiskLevelVO riskLevel) {
        MarketingStrategyRouter router = marketingStrategyRouterDao.queryMarketingStrategyRouter(buildRouterQuery(activityId, userSegment, riskLevel));
        if (null == router && RiskLevelVO.HIGH.equals(riskLevel)) {
            router = marketingStrategyRouterDao.queryMarketingStrategyRouter(buildRouterQuery(activityId, UserSegmentVO.RISK_USER, riskLevel));
        }
        if (null == router) {
            return null;
        }
        return MarketingStrategyRouterEntity.builder()
                .activityId(router.getActivityId())
                .userSegment(router.getUserSegment())
                .riskLevel(router.getRiskLevel())
                .strategyId(router.getStrategyId())
                .priority(router.getPriority())
                .enabled(router.getEnabled())
                .routerDesc(router.getRouterDesc())
                .build();
    }

    @Override
    public void saveMarketingRouteLog(MarketingRouteResultEntity routeResult) {
        MarketingStrategyRouterLog routeLog = new MarketingStrategyRouterLog();
        routeLog.setUserId(routeResult.getUserId());
        routeLog.setActivityId(routeResult.getActivityId());
        routeLog.setDefaultStrategyId(routeResult.getDefaultStrategyId());
        routeLog.setRoutedStrategyId(routeResult.getRoutedStrategyId());
        routeLog.setUserSegment(routeResult.getUserSegment().name());
        routeLog.setRiskLevel(routeResult.getRiskLevel().name());
        routeLog.setRouteDesc(routeResult.getRouteDesc());
        marketingStrategyRouterLogDao.insert(routeLog);
    }

    private MarketingStrategyRouter buildRouterQuery(Long activityId, UserSegmentVO userSegment, RiskLevelVO riskLevel) {
        MarketingStrategyRouter router = new MarketingStrategyRouter();
        router.setActivityId(activityId);
        router.setUserSegment(userSegment.name());
        router.setRiskLevel(riskLevel.name());
        return router;
    }

}
