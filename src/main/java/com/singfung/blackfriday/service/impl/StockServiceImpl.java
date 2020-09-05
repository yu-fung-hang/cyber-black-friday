package com.singfung.blackfriday.service.impl;

import com.singfung.blackfriday.exception.BusinessException;
import com.singfung.blackfriday.model.Stock;
import com.singfung.blackfriday.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.singfung.blackfriday.dao.StockDAO;

import java.util.List;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Stock stock)
    {
        stockDAO.update(stock);
    }

    @Override
    public Stock findById(int id)
    {
        Stock result = stockDAO.select(id);

        if(result == null)
        { throw new BusinessException("Stock not found."); }

        return result;
    }

    @Override
    public List<Stock> findAll()
    {
        return stockDAO.selectAll();
    }

    @Override
    public void decreaseStockNum(int id, int decrement)
    {
        Stock stock = findById(id);

        stock.setStockNum(stock.getStockNum() - decrement);

        stockDAO.decreaseStockNum(stock);
    }
}