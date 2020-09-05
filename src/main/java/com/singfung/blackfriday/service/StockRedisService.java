package com.singfung.blackfriday.service;

public interface StockRedisService 
{
	public void saveOrdersFromRedisToDB(int stockId);
}