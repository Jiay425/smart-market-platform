package com.smartmarket.domain.marketing.repository;

import com.smartmarket.domain.marketing.model.entity.MarketingRouteResultEntity;
import com.smartmarket.domain.marketing.model.entity.MarketingStrategyRouterEntity;
import com.smartmarket.domain.marketing.model.entity.UserBehaviorStatEntity;
import com.smartmarket.domain.marketing.model.entity.UserTagEntity;
import com.smartmarket.domain.marketing.model.valobj.RiskLevelVO;
import com.smartmarket.domain.marketing.model.valobj.UserSegmentVO;

/**
 * @description 营销决策仓储接口
 */
public interface IMarketingRepository {

    UserTagEntity queryUserTag(String userId);

    UserBehaviorStatEntity queryUserBehaviorStat(String userId);

    MarketingStrategyRouterEntity queryMarketingStrategyRouter(Long activityId, UserSegmentVO userSegment, RiskLevelVO riskLevel);

    void saveMarketingRouteLog(MarketingRouteResultEntity routeResult);

}
