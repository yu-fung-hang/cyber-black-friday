package com.singfung.blackfriday.dao;

import com.singfung.blackfriday.model.Stock;
import org.springframework.stereotype.Repository;

@Repository
public interface StockDAO
{
//	Stock select(Long id);

	int insert(Stock stock);

//	int update(Stock stock);
//
//	int delete(String id);
}