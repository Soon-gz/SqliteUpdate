package com.example.administrator.greendao.db.dao;

import java.util.List;

/**
 * Created by
 * on 2017/2/9.
 */

public interface IBaseDao<T> {

    Long insert(T entity);

    int delete(T entity);

    int update(T newEntity,T where);

    List<T> query(T entity);

    List<T> query(T entity,String orderby,Integer startInt,Integer limit );
}
