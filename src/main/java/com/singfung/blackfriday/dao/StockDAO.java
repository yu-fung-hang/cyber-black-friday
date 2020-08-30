package com.singfung.blackfriday.dao;

import com.singfung.blackfriday.model.Stock;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockDAO
{
	Stock select(int id);

	List<Stock> selectAll();

	int insert(Stock stock);

	int update(Stock stock);
//
//	int delete(String id);
}