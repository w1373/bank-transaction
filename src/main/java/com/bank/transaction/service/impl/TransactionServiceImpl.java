package com.bank.transaction.service.impl;

import com.bank.transaction.dto.TransactionDto;
import com.bank.transaction.enums.ExceptionType;
import com.bank.transaction.enums.TransactionType;
import com.bank.transaction.exception.BusinessException;
import com.bank.transaction.model.Transaction;
import com.bank.transaction.service.TransactionService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 交易服务实现类
 * <p>使用线程安全的集合存储交易数据，支持创建、更新、删除和查询操作</p>
 */
@Service
public class TransactionServiceImpl implements TransactionService {
    //生成id
    private final AtomicLong idGenerator = new AtomicLong(100000);

    //排序数据
    private final SortedSet<Transaction> transactions =
            new ConcurrentSkipListSet<>(Comparator.comparing(Transaction::getTimestamp).reversed());

    //所有的交易数据
    private final Map<Long, Transaction> idIndex = new ConcurrentHashMap<>();

    @Override
    public long createTransactionId() {
        return idGenerator.incrementAndGet();
    }

    /**
     * 创建交易记录
     * @param dto 交易数据传输对象
     * @return 创建成功的交易实体
     * @throws BusinessException 当交易ID重复时抛出
     */
    @Override
    public Transaction createTransaction(TransactionDto dto) {
        Transaction transaction = new Transaction(
                dto.getId(),
                dto.getAccountNumber(),
                dto.getAmount(),
                dto.getType(),
                LocalDateTime.now(),
                dto.getDescription());
        if(idIndex.putIfAbsent(transaction.getId(),transaction) == null) {
            transactions.add(transaction);
            return transaction;
        } else {
            throw new BusinessException(ExceptionType.TRANSACTION_DUPLICATE,transaction.getId());
        }
    }

    /**
     * 更新交易记录
     * @param id 交易ID
     * @return 删除成功返回true
     * @throws BusinessException 当交易不存在时抛出
     */
    @Override
    @CacheEvict(value = "transactions", key = "#id")
    public Transaction updateTransaction(Long id, TransactionDto dto) {
        Transaction transaction = idIndex.get(id);
        if(transaction == null) {
            throw new BusinessException(ExceptionType.TRANSACTION_NOT_EXIST,id);
        }
        if(StringUtils.hasText(dto.getDescription())) {
            transaction.setDescription(dto.getDescription());
        }
        if(Objects.nonNull(dto.getAmount())) {
            transaction.setAmount(dto.getAmount());
        }

        if(Objects.nonNull(dto.getType())) {
            transaction.setType(dto.getType());
        }
        return transaction;
    }

    /**
     * 删除交易
     * @param id
     * @return
     */
    @Override
    @CacheEvict(value = "transactions", key = "#id")
    public Boolean deleteTransaction(Long id) {
        Transaction transaction = idIndex.get(id);
        if(transaction == null) {
            throw new BusinessException(ExceptionType.TRANSACTION_NOT_EXIST,id);
        }
        synchronized (transaction) {
            Transaction target = idIndex.get(id);
            if(target == null || target != transaction) {
                throw new BusinessException(ExceptionType.TRANSACTION_NOT_EXIST,id);
            }
            idIndex.remove(id);
            transactions.remove(transaction);
            return true;
        }
    }

    @Override
    @Cacheable(value = "transactions", key = "#id")
    public Transaction getTransaction(Long id) {
        return idIndex.get(id);
    }

    @Override
    public List<Transaction> listTransactions(int page, int size, TransactionType type) {
        // 根据类型过滤交易记录
        return transactions.stream()
                .filter(t -> type == null || t.getType() == type)
                .skip((long) page * size)
                .limit(size)
                .toList();
    }
}