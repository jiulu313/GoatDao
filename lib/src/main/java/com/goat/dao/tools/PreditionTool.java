package com.goat.dao.tools;

import com.goat.dao.exception.GoatDaoException;

import java.util.List;

public class PreditionTool {

    public static <T> T checkNotNull(T t,String message){
        if(t == null){
            throw new GoatDaoException(message);
        }
        return t;
    }

    public static <T> List<T> checkNotNull(List<T> list,String message){
        if(list == null || list.isEmpty()){
            throw new GoatDaoException(message);
        }

        return list;
    }




}
