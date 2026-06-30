package com.smartmarket.domain.marketing.service;

import com.smartmarket.domain.marketing.model.entity.MarketingRouteRequestEntity;
import com.smartmarket.domain.marketing.model.entity.MarketingRouteResultEntity;

/**
 * @description 营销策略路由服务
 */
public interface IMarketingStrategyRouter {

    MarketingRouteResultEntity route(MarketingRouteRequestEntity request);

}
