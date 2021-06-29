package com.singfung.blackfriday.service;

import com.singfung.blackfriday.model.Stock;

import java.util.List;

public interface StockService
{	
	Stock findById(int id);

	Stock selectForUpdate(int id);

	Stock findByStockname(String stockname);

	int decreaseStockNum(int id, int decrement);

	int decreaseStockNumByOne(Stock stock);

	List<Stock> findAll();

	void save(Stock stock);

	void deleteAll();

	void syncRedis();
}