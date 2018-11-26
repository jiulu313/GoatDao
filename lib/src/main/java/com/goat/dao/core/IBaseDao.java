package com.goat.dao.core;


import java.util.List;

public interface IBaseDao<T> {
    long insert(T entity);

    long insert(List<T> entityList);

    List<T> query(T where);

}
