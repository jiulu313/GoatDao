package com.test.simple;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.goat.dao.core.BaseDao;
import com.test.simple.util.VerifyUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener{
    BaseDao<Person> baseDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv_hello).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHello();
            }
        });

        findViewById(R.id.btn_insert1).setOnClickListener(this);
        findViewById(R.id.btn_insert2).setOnClickListener(this);



        if(VerifyUtil.verifyStoragePermissions(this)){
            initDatabase();
        }
    }

    private void onHello() {
        baseDao.insert(new Person("tom","123456"));
    }

    private void initDatabase() {
        String databasefile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mc.db";

        File file = new File(databasefile);


        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        Log.e("zhj","databasefile=" + databasefile);

        baseDao = new BaseDao<>(databasefile);
        baseDao.init(Person.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_insert1:
                onInsert1();
                break;
            case R.id.btn_insert2:
                onInsert2();
                break;
        }
    }

    //常规插入5000条数据
    private void onInsert1() {

        long t1 = System.currentTimeMillis();

        for (int i = 0 ; i < 5000;i++){
            Person person = new Person("wendy" + i ,"abc" + i);
            baseDao.insert(person);
        }

        Log.e("zhj","常规插入5000条 = " + (System.currentTimeMillis() - t1));

    }

    //事务插入5000条
    private void onInsert2() {
        long t1 = System.currentTimeMillis();
        List<Person> personList = new ArrayList<>();
        for (int i = 0 ; i < 5000;i++){
            Person person = new Person("natasha" + i ,"hello" + i);
            personList.add(person);
        }

        baseDao.insert(personList);
        Log.e("zhj","事务插入5000条 = " + (System.currentTimeMillis() - t1));
    }


}
