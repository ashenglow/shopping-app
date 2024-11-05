package test.shop.infrastructure.monitoring.model.metrics;

import lombok.Getter;
import test.shop.infrastructure.monitoring.model.query.SlowQueryInfo;

import java.time.LocalDateTime;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

@Getter
public class QueryMetrics {
    private final AtomicLong totalTime = new AtomicLong(0);
    private final AtomicLong executionCount = new AtomicLong(0);
    private final AtomicLong maxTime = new AtomicLong(0);
    private final Queue<SlowQueryInfo> slowQueries = new ConcurrentLinkedQueue<>();

    public void recordExecution(long executionTime){
        totalTime.addAndGet(executionTime);
        executionCount.incrementAndGet();
        updateMaxTime(executionTime);

        if(executionTime > 1000){ // 1 second threshold
            addSlowQuery(executionTime);
        }
    }

    public double getAverageTime(){
        long count = executionCount.get();
        return count > 0 ? (double) totalTime.get() / count : 0;
    }
    private void updateMaxTime(long executionTime) {
        long currentMax;
        do {
            currentMax = maxTime.get();
        } while (executionTime > currentMax &&
                !maxTime.compareAndSet(currentMax, executionTime));
    }
    private void addSlowQuery(long executionTime) {
        SlowQueryInfo info = new SlowQueryInfo(
                LocalDateTime.now(),
                executionTime,
                Thread.currentThread().getName()
        );
        slowQueries.offer(info);
        while ( slowQueries.size() < 100 ){ //keep last 100 slow queries
            slowQueries.poll();
        }


}


}
