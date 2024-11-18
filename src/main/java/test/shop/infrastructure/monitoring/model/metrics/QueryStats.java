package test.shop.infrastructure.monitoring.model.metrics;

import lombok.Data;
import test.shop.infrastructure.monitoring.model.alert.AlertThreshold;
import test.shop.infrastructure.monitoring.model.dashboard.timeseris.TimeSeriesPoint;
import test.shop.infrastructure.monitoring.model.query.QueryExecutionContext;
import test.shop.infrastructure.monitoring.model.query.SlowQueryInfo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

@Data
public class QueryStats {
    private final String methodName;
    private final AtomicLong totalQueries  = new AtomicLong(0);
    private final AtomicLong totalTime = new AtomicLong(0);
    private final AtomicLong slowQueries = new AtomicLong(0);
    private final Queue<SlowQueryInfo> recentSlowQueries = new ConcurrentLinkedQueue<>();
    private final int maxSlowQueries = 100; //keep last 100 slow queries
    private final Queue<TimeSeriesPoint> timeSeriesData = new ConcurrentLinkedQueue<>();
    private final List<OrderExecutionDetail> orderExecutions = new ArrayList<>();
    private final Queue<QueryExecutionContext> queryHistory = new ConcurrentLinkedQueue<>();
    private static final int MAX_TIME_SERIES_POINTS = 100; //keep last 100 points
    private static final int MAX_HISTORY_SIZE = 100;

    public QueryStats(String methodName) {
        this.methodName = methodName;
    }

    @Data
    public static class OrderExecutionDetail {
        private final long executionTime;
        private final LocalDateTime timestamp;
        private final String operation;
        private final Long orderId;
        private final Map<String, Object> context;
    }

    public void addOrderExecutionDetail(long executionTime, Map<String, Object> context) {
        OrderExecutionDetail detail = new OrderExecutionDetail(
                executionTime,
                LocalDateTime.now(),
                context.get("operation").toString(),
                (Long) context.get("orderId"),
                context
        );
        orderExecutions.add(detail);
    }
    public void addQuery(QueryExecutionContext context){
        totalQueries.incrementAndGet();
        totalTime.addAndGet(context.getExecutionTime());
        queryHistory.offer(context);

        while(queryHistory.size() > MAX_HISTORY_SIZE){
            queryHistory.poll();
        }

        timeSeriesData.offer(new TimeSeriesPoint(
                LocalDateTime.now(),
                context.getExecutionTime(),
                this.methodName
        ));

        while (timeSeriesData.size() > MAX_HISTORY_SIZE){
            timeSeriesData.poll();
        }

    }

    public List<TimeSeriesPoint> getTimeSeriesData(){
        return new ArrayList<>(timeSeriesData);
    }

    public List<QueryExecutionContext> getQueryHistory(){
        return new ArrayList<>(queryHistory);
    }

    private void addSlowQuery(long executionTime){
        SlowQueryInfo info = new SlowQueryInfo(
                LocalDateTime.now(),
                executionTime,
                Thread.currentThread().getName()
        );

        recentSlowQueries.offer(info);
        while (recentSlowQueries.size() > maxSlowQueries){
            recentSlowQueries.poll();
        }

    }

    public double getAverageTime(){
        long queries = totalQueries.get();
        return queries > 0 ? (double) totalTime.get() / queries : 0;
    }

    public List<SlowQueryInfo> getRecentSlowQueries() {
        return new ArrayList<>(recentSlowQueries);
    }


}
