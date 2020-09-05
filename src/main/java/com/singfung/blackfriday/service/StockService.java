package com.singfung.blackfriday.service;

import com.singfung.blackfriday.model.Stock;

import java.util.List;

public interface StockService
{	
	Stock findById(int id);

	void decreaseStockNum(int id, int decrement);

	List<Stock> findAll();

	void save(Stock stock);

	void update(Stock stock);

//	void deleteByIds(List<String> ids);
}