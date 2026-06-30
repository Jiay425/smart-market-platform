package com.smartmarket.infrastructure.dao;

import com.smartmarket.infrastructure.dao.po.MarketingStrategyRouterLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IMarketingStrategyRouterLogDao {

    void insert(MarketingStrategyRouterLog marketingStrategyRouterLog);

}
