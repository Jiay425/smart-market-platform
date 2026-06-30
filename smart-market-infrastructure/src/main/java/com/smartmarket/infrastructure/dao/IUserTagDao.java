package com.smartmarket.infrastructure.dao;

import com.smartmarket.infrastructure.dao.po.UserTag;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IUserTagDao {

    UserTag queryUserTag(String userId);

}
