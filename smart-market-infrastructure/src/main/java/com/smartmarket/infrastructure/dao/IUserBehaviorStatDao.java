package com.smartmarket.infrastructure.dao;

import com.smartmarket.infrastructure.dao.po.UserBehaviorStat;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IUserBehaviorStatDao {

    UserBehaviorStat queryUserBehaviorStat(String userId);

}
