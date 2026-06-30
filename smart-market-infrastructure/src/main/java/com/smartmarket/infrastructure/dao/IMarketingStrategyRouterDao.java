package com.smartmarket.infrastructure.dao;

import com.smartmarket.infrastructure.dao.po.MarketingStrategyRouter;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IMarketingStrategyRouterDao {

    MarketingStrategyRouter queryMarketingStrategyRouter(MarketingStrategyRouter marketingStrategyRouter);

}
