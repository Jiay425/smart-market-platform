package com.smartmarket.domain.marketing.model.entity;

import com.smartmarket.domain.marketing.model.valobj.RiskLevelVO;
import com.smartmarket.domain.marketing.model.valobj.UserSegmentVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description 营销策略路由结果
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketingRouteResultEntity {

    /** 用户ID */
    private String userId;
    /** 活动ID */
    private Long activityId;
    /** 活动默认策略ID */
    private Long defaultStrategyId;
    /** 实际执行策略ID */
    private Long routedStrategyId;
    /** 用户分层 */
    private UserSegmentVO userSegment;
    /** 风险等级 */
    private RiskLevelVO riskLevel;
    /** 路由原因 */
    private String routeDesc;

}
