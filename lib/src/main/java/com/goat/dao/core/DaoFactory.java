package com.goat.dao.core;

import com.goat.dao.tools.PreditionTool;

public class DaoFactory {
    private static volatile DaoFactory sInstance = null;

    private String mDatabaseFilePath;
    private boolean mIsInited = false;

    private DaoFactory(){}

    public static DaoFactory getInstance(){
        if(sInstance == null){
            synchronized (DaoFactory.class){
                if(sInstance == null){
                    sInstance = new DaoFactory();
                }
            }
        }

        return sInstance;
    }


    public static void init(String databaseFilePath){
        if(getInstance().mIsInited){
            return;
        }

        getInstance().mDatabaseFilePath = databaseFilePath;



    }

    public static <T> IBaseDao<T> getDao(Class<T> clazz){
        PreditionTool.checkNotNull(clazz,"clazz is null");

        return null;
    }




}
