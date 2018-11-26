package com.goat.dao.core;

import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.IOException;

public class DatabaseControl {
    private SQLiteDatabase mSqliteDatabase; //操作数据库实例
    private String mDatabaseFilePath;       //数据库文件路径
    private boolean mIsInited;              //是否初始化过


    public DatabaseControl(String databaseFilePath){
        mDatabaseFilePath = databaseFilePath;

        File file = new File(mDatabaseFilePath);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        mSqliteDatabase = SQLiteDatabase.openOrCreateDatabase(databaseFilePath,null);

    }




}
