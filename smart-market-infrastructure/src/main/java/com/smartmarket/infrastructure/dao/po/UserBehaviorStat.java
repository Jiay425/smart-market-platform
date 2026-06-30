package com.smartmarket.infrastructure.dao.po;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class UserBehaviorStat {

    private Long id;
    private String userId;
    private Integer totalRaffleCount;
    private Integer dayRaffleCount;
    private Integer continuousSignDays;
    private BigDecimal availableCreditAmount;
    private Date lastActiveTime;
    private Date createTime;
    private Date updateTime;

}
