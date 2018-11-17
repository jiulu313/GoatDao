package com.goat.dao.exception;

public class GoatDaoException extends IllegalArgumentException{
    private static final String mExceptionPrefix = "GoatDaoException";


    public GoatDaoException(){
        super();
    }

    public GoatDaoException(String message){
        super("GoatDaoException : " + message);
    }
}
