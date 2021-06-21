package com.singfung.blackfriday.service;

public interface RedisOrderService
{
	boolean generateAnOrderInRedis(int stockId, int userId);
}