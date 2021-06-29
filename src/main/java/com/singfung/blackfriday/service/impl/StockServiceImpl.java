package com.singfung.blackfriday.service.impl;

import com.singfung.blackfriday.common.QueryWrapper.QueryWrapperBuilder;
import com.singfung.blackfriday.exception.BusinessException;
import com.singfung.blackfriday.model.Stock;
import com.singfung.blackfriday.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.singfung.blackfriday.dao.StockDAO;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Slf4j
public class StockServiceImpl implements StockService
{
    @Autowired
    private StockDAO stockDAO;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Stock findByStockname(String stockname)
    {
        Stock stock = null;
        if (!StringUtils.isEmpty(stockname))
        {
            QueryWrapperBuilder builder = new QueryWrapperBuilder();
            builder.eq("name", stockname);
            List<Stock> stocks = stockDAO.selectWithQuery(builder.build());
            if (!CollectionUtils.isEmpty(stocks)) {
                stock = stocks.get(0);
            }
        }
        return stock;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(Stock stock)
	{
        if (stock.getStockNum() != stock.getTotalNum())
        { throw new BusinessException("totalNum should be equal to stockNum."); }

        uniqueStockname(stock.getName());
        stockDAO.insert(stock);
        syncRedis();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncRedis()
    {
        List<Stock> stock_list = findAll();

        for(Stock stock : stock_list)
        {
            int stockId = stock.getId();
            String key = "stock_" + stockId;
            String field = "stockNum";
            String value = "" + stock.getStockNum();
            redisTemplate.opsForHash().put(key, field, value);
        }
    }

    public void uniqueStockname(String stockname)
    {
        Stock s = findByStockname(stockname);
        if (s != null) {
            throw new BusinessException("Conflict : This product is already in db, operation failed.");
        }
    }

    @Override
    public Stock findById(int id)
    {
        Stock result = stockDAO.select(id);
        if(result == null)
        { throw new BusinessException("Stock not found!"); }

        return result;
    }

    @Override
    public Stock selectForUpdate(int id)
    {
        Stock result = stockDAO.selectForUpdate(id);
        if(result == null)
        { throw new BusinessException("Stock not found!"); }

        return result;
    }

    @Override
    public List<Stock> findAll()
    {
        return stockDAO.selectAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int decreaseStockNum(int id, int decrement)
    {
        Stock stock = findById(id);
        stock.setStockNum(stock.getStockNum() - decrement);
        return stockDAO.decreaseStockNum(stock);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int decreaseStockNumByOne(Stock stock)
    {
        return stockDAO.decreaseStockNumByOne(stock);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll()
    {
        List<Stock> stockList = findAll();
        for(Stock s : stockList)
        {
            redisTemplate.delete("stock_" + s.getId());
        }

        stockDAO.deleteAll();
    }
}