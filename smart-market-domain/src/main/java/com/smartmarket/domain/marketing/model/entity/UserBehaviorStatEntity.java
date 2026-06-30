package com.smartmarket.domain.marketing.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description 用户行为统计实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserBehaviorStatEntity {

    /** 用户ID */
    private String userId;
    /** 累计抽奖次数 */
    private Integer totalRaffleCount;
    /** 当日抽奖次数 */
    private Integer dayRaffleCount;
    /** 连续签到天数 */
    private Integer continuousSignDays;
    /** 可用积分金额 */
    private BigDecimal availableCreditAmount;
    /** 最近活跃时间 */
    private Date lastActiveTime;

}
