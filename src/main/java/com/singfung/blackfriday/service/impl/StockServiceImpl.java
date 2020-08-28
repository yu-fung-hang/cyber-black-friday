package com.singfung.blackfriday.service.impl;

import com.singfung.blackfriday.model.Stock;
import com.singfung.blackfriday.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.singfung.blackfriday.dao.StockDAO;

@Service
@Slf4j
public class StockServiceImpl implements StockService
{
    @Autowired
    private StockDAO stockDAO;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(Stock stock)
	{
        stockDAO.insert(stock);
    }
}