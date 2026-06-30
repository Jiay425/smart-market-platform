package com.smartmarket.domain.marketing.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description 用户标签实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserTagEntity {

    /** 用户ID */
    private String userId;
    /** 用户标签；NEW_USER、ACTIVE_USER、SILENT_USER、HIGH_VALUE_USER、RISK_USER */
    private String tagCode;
    /** 风险等级；LOW、MEDIUM、HIGH */
    private String riskLevel;

}
