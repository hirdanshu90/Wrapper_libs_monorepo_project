package com.accenture.goss.caching.Exceptions;

public class CacheNotBuiltException extends RuntimeException{
    public CacheNotBuiltException(String errMsg){
        super(errMsg);
    }
}
