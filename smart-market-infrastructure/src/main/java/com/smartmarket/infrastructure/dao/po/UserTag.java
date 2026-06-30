package com.smartmarket.infrastructure.dao.po;

import lombok.Data;

import java.util.Date;

@Data
public class UserTag {

    private Long id;
    private String userId;
    private String tagCode;
    private String riskLevel;
    private Date createTime;
    private Date updateTime;

}
