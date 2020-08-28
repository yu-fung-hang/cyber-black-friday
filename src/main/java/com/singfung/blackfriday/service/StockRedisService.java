package com.singfung.blackfriday.service;

public interface StockRedisService 
{
	public void saveOrdersIntoRedis(int stockId);
}