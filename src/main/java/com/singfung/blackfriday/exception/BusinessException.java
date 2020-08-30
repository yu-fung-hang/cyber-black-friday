package com.singfung.blackfriday.exception;

public class BusinessException extends RuntimeException 
{
    private Object data;

    public BusinessException(String message) 
	{
        super(message);
    }

    public BusinessException(String message, Object data) 
	{
        super(message);
        this.data = data;
    }

    public Object getData() 
	{
        return data;
    }

    public void setData(Object data) 
	{
        this.data = data;
    }
}
