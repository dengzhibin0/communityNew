package com.nowcoder.community.dao;

import org.springframework.stereotype.Repository;

/**
 * @author 邓志斌
 * @version 1.0
 * @date 2022/3/18 20:35
 */
@Repository("alphaHibernate")
public class AlphaDaoHibernateImpl implements AlphaDao{

    @Override
    public String select() {
        return "Hibernate";
    }
}
