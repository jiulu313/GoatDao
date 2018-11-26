package com.test.simple;

import com.goat.dao.annotation.DbField;
import com.goat.dao.annotation.DbTable;

@DbTable(tableName = "tb_person")
public class Person {

    @DbField("name")
    public String name;

    @DbField("password")
    public String password;

    public Person(String name,String password){
        this.name = name;
        this.password = password;
    }

}
