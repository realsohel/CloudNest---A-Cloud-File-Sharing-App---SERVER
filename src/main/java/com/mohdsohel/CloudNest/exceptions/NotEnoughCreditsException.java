package com.mohdsohel.CloudNest.exceptions;

public class NotEnoughCreditsException extends RuntimeException{
    public NotEnoughCreditsException(){
    }
    public NotEnoughCreditsException(String message){
        super(message);
    }

}
