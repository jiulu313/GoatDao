package com.goat.dao.core;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.goat.dao.annotation.DbField;
import com.goat.dao.annotation.DbTable;
import com.goat.dao.exception.GoatDaoException;
import com.goat.dao.tools.PreditionTool;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @param <T> T 为实体的类型，对应于一张表
 */
public class BaseDao<T> implements IBaseDao<T>{
    private SQLiteDatabase mSqliteDatabase; //操作数据库实例
    private String mDatabaseFilePath;       //数据库文件路径
    private boolean mIsInited;              //是否初始化过

    private Class<T> mEntityClazz;          //实体类的字节码
    private String mTableName;              //表名
    private String mFieldPrefix;            //属性的前缀

    private Map<String,Field> mFieldCache;  //把数据库中的字段名和实体类中的相应的Field缓存起来

    public BaseDao(SQLiteDatabase sqliteDatabase, String databaseFilePath) {
        this.mSqliteDatabase = sqliteDatabase;
        this.mDatabaseFilePath = databaseFilePath;
    }

    public void init(Class<T> clazz){
        if(mIsInited){
            return;
        }

        if(clazz == null){
            throw new GoatDaoException("clazz is null!!!");
        }

        mEntityClazz = clazz;

        if(!autoCreateTable()){
            return;
        }

        initCache();

        mIsInited = true;
    }

    private boolean autoCreateTable() {
        //1 打开或者创建数据库操作对象
        mSqliteDatabase = SQLiteDatabase.openOrCreateDatabase(mDatabaseFilePath,null);

        //2 条件判断
        if(mSqliteDatabase == null){
            throw new GoatDaoException("create database failed !!!");
        }

        if(!mSqliteDatabase.isOpen()){
            throw new GoatDaoException("database open failed !!!");
        }

        //3 获取表名以及字段名
        mTableName = mEntityClazz.getAnnotation(DbTable.class).tableName();
        if(mTableName == null || "".equals(mTableName)){
            throw new GoatDaoException("table name is null !!!");
        }

        //4 创建数据库  create table if not exists usser2(tb_name TEXT,tb_password BIGINT)
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ");
        sb.append(mTableName + "(");

        Field[] declaredFields = mEntityClazz.getDeclaredFields();
        if(declaredFields == null || declaredFields.length == 0){
            throw new GoatDaoException("entity has no attribute !!!");
        }

        for (Field field : declaredFields){
            DbField dbField = field.getAnnotation(DbField.class);
            String fieldName = mFieldPrefix + dbField.value();
            Class<?> type = field.getType();

            if(type == String.class){
                sb.append(fieldName + " TEXT,");
            }else if(type == int.class || type == Integer.class){
                sb.append(fieldName + " INTEGER,");
            }else if(type == double.class || type == Double.class){
                sb.append(fieldName + " DOUBLE,");
            }else if(type == long.class || type == Long.class){
                sb.append(fieldName + " BIGINT,");
            }else if(type == byte[].class){
                sb.append(fieldName + " BLOB,");
            }else {
                //不支持的类型
                continue;
            }
        }

        //5 删除最后一个"," 并且替追加一个")"
        if(sb.charAt(sb.length() - 1) == ','){
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(")");

        //6 执行拼接好的sql语句创建数据库
        try {
            mSqliteDatabase.execSQL(sb.toString());
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void initCache() {
        //把数据库中的字段名和实体类中的相应的Field缓存起来
        //数据库中的字段的名字，通过查一次空表进行查找

        mFieldCache = new HashMap<>();

        String sql = "SELECT * FROM " + mTableName + " limit 1,0";
        Cursor cursor = mSqliteDatabase.rawQuery(sql,null);
        String[] columnNames = cursor.getColumnNames();
        Field[] fields = mEntityClazz.getDeclaredFields();

        for (String columnName : columnNames){
            for (Field field : fields){
                DbField dbField = field.getAnnotation(DbField.class);
                if(dbField != null && columnName.equals(dbField.value())){
                    mFieldCache.put(columnName,field);
                    break;
                }
            }
        }
        cursor.close();
    }


    @Override
    public long insert(T entity) {
        PreditionTool.checkNotNull(entity,"entity is null !!!");

        ContentValues contentValues = getContentValues(entity);
        return mSqliteDatabase.insert(mTableName,null,contentValues);
    }

    @Override
    public long insert(List<T> entityList) {
        PreditionTool.checkNotNull(entityList,"entityList is null or empty !!!");

        int count = 0;
        for (T t : entityList){
            count += insert(t);
        }

        return count;
    }

    @Override
    public List<T> query(T where) {

        return null;
    }

    private ContentValues getContentValues(T entity) {
        ContentValues contentValues = new ContentValues();

        Iterator<Map.Entry<String, Field>> iterator = mFieldCache.entrySet().iterator();
        while (iterator.hasNext()){
            String columnName = iterator.next().getKey();
            Field field = iterator.next().getValue();
            field.setAccessible(true);

            Class type = field.getType();
            try {
                Object object = field.get(entity);
                if(type == String.class){
                    String value = (String) object;
                    contentValues.put(columnName,value);
                }else if(type == Integer.class){
                    Integer value = (Integer) object;
                    contentValues.put(columnName,value);
                }else if(type == Long.class){
                    Long value = (Long) object;
                    contentValues.put(columnName,value);
                }else if(type == byte[].class){
                    byte[] value = (byte[]) object;
                    contentValues.put(columnName,value);
                }else {
                    continue;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return contentValues;
    }
}
