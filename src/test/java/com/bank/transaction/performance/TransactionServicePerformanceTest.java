package com.bank.transaction.performance;

import com.bank.transaction.dto.TransactionDto;
import com.bank.transaction.enums.TransactionType;
import com.bank.transaction.model.Transaction;
import com.bank.transaction.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootTest
public class TransactionServicePerformanceTest {

    @Autowired
    private TransactionService transactionService;

    private List<String> accountNumbers;

    @BeforeEach
    public void setUp() {
        accountNumbers = IntStream.range(0, 1000)
                .mapToObj(i -> "ACC" + i)
                .collect(Collectors.toList());
    }

    @Test
    public void testConcurrentCreateTransactions() throws InterruptedException {
        runConcurrentTest(100, 100, this::createRandomTransaction);
    }

    @Test
    public void testMixedOperations() throws InterruptedException {
        List<Operation> operations = Arrays.asList(
                new Operation(this::createRandomTransaction, 0.4),
                new Operation(this::getRandomTransaction, 0.4),
                new Operation(this::updateRandomTransaction, 0.1),
                new Operation(this::deleteRandomTransaction, 0.1)
        );

        runConcurrentTest(100, 100, operations);
    }

    @Test
    public void testHighContentionAccounts() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(100);
        AtomicInteger counter = new AtomicInteger();

        long startTime = System.currentTimeMillis();

        // 对少量账户产生高竞争
        List<String> hotAccounts = Arrays.asList("HOT1", "HOT2", "HOT3", "HOT4", "HOT5");

        for (int i = 0; i < 100; i++) {
            executor.submit(() -> {
                String account = hotAccounts.get(counter.getAndIncrement() % hotAccounts.size());
                for (int j = 0; j < 1000; j++) {
                    transactionService.createTransaction(createTransactionDto(account));
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.MINUTES);

        long endTime = System.currentTimeMillis();
        printPerformanceMetrics("高竞争账户", 100 * 1000, endTime - startTime);
    }

    @Test
    public void testPaginationPerformance() throws InterruptedException {
        // 先创建大量交易
        createBulkTransactions(10000);

        ExecutorService executor = Executors.newFixedThreadPool(50);
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 50; i++) {
            final int pageNum = i;
            executor.submit(() -> {
                int pageSize = 50;
                transactionService.listTransactions(pageNum % 200, pageSize,null);
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        long endTime = System.currentTimeMillis();
        printPerformanceMetrics("分页查询", 50, endTime - startTime);
    }

    private void createBulkTransactions(int count) {
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            String account = accountNumbers.get(random.nextInt(accountNumbers.size()));
            TransactionDto transaction = createTransactionDto(account);
            transactionService.createTransaction(transaction);
        }
    }

    private Transaction createRandomTransaction() {
        Random random = new Random();
        String account = accountNumbers.get(random.nextInt(accountNumbers.size()));
        return transactionService.createTransaction(createTransactionDto(account));
    }

    private TransactionDto createTransactionDto(String account) {
        TransactionDto transaction = new TransactionDto();
        transaction.setId(transactionService.createTransactionId());
        transaction.setAccountNumber(account);
        transaction.setAmount(BigDecimal.valueOf(Math.random() * 1000));
        transaction.setType(TransactionType.values()[(int) (Math.random() * TransactionType.values().length)]);
        transaction.setDescription("性能测试");
        return transaction;
    }

    private void getRandomTransaction() {
        List<Transaction> transactions = transactionService.listTransactions(0,Integer.MAX_VALUE,null);
        if (!transactions.isEmpty()) {
            Long randomId = transactions.get(new Random().nextInt(transactions.size())).getId();
            transactionService.getTransaction(randomId);
        }
    }

    private void updateRandomTransaction() {
        List<Transaction> transactions = transactionService.listTransactions(0,Integer.MAX_VALUE,null);
        if (!transactions.isEmpty()) {
            Transaction randomTransaction = transactions.get(new Random().nextInt(transactions.size()));
            TransactionDto update = new TransactionDto();
            update.setId(randomTransaction.getId());
            update.setAmount(randomTransaction.getAmount().add(BigDecimal.TEN));
            transactionService.updateTransaction(randomTransaction.getId(), update);
        }
    }

    private void deleteRandomTransaction() {
        List<Transaction> transactions = transactionService.listTransactions(0,Integer.MAX_VALUE,null);
        if (!transactions.isEmpty()) {
            Long randomId = transactions.get(new Random().nextInt(transactions.size())).getId();
            transactionService.deleteTransaction(randomId);
        }
    }

    private void runConcurrentTest(int threadCount, int operationsPerThread, List<Operation> operations) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        Random random = new Random();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    double rand = random.nextDouble();
                    double cumulativeProbability = 0.0;
                    for (Operation operation : operations) {
                        cumulativeProbability += operation.probability;
                        if (rand < cumulativeProbability) {
                            operation.action.run();
                            break;
                        }
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.MINUTES);

        long endTime = System.currentTimeMillis();
        printPerformanceMetrics("混合操作", threadCount * operationsPerThread, endTime - startTime);
    }

    private void runConcurrentTest(int threadCount, int operationsPerThread, Runnable operation) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    operation.run();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.MINUTES);

        long endTime = System.currentTimeMillis();
        printPerformanceMetrics("并发创建", threadCount * operationsPerThread, endTime - startTime);
    }

    private void printPerformanceMetrics(String testType, long totalOperations, long totalTime) {
        System.out.printf("%s - 执行 %d 次操作，耗时 %d ms%n", testType, totalOperations, totalTime);
        System.out.printf("吞吐量: %.2f 操作/秒%n", (double) totalOperations / (totalTime / 1000.0));
        System.out.printf("平均响应时间: %.2f ms/操作%n", (double) totalTime / totalOperations);
        System.out.println("----------------------------------------");
    }

    private static class Operation {
        private final Runnable action;
        private final double probability;

        public Operation(Runnable action, double probability) {
            this.action = action;
            this.probability = probability;
        }
    }
}