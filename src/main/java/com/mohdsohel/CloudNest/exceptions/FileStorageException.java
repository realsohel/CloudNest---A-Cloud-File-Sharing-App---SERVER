package com.mohdsohel.CloudNest.exceptions;

public class FileStorageException extends RuntimeException{
    public FileStorageException(){
    }
    public FileStorageException(String message){
        super(message);
    }

}