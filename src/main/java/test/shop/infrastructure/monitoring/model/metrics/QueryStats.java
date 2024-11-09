package test.shop.infrastructure.monitoring.model.metrics;

import lombok.Data;
import test.shop.infrastructure.monitoring.model.alert.AlertThreshold;
import test.shop.infrastructure.monitoring.model.dashboard.timeseris.TimeSeriesPoint;
import test.shop.infrastructure.monitoring.model.query.SlowQueryInfo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private static final int MAX_TIME_SERIES_POINTS = 100; //keep last 100 points

    public QueryStats(String methodName) {
        this.methodName = methodName;
    }

    public void addQuery(long executionTime, AlertThreshold threshold){
        totalQueries.incrementAndGet();
        totalTime.addAndGet(executionTime);
        timeSeriesData.offer(new TimeSeriesPoint(LocalDateTime.now(), (double) executionTime, this.methodName));
        while(timeSeriesData.size() > MAX_TIME_SERIES_POINTS){
            timeSeriesData.poll();
        }
        if(executionTime > threshold.getSlowQueryThreshold()){
            slowQueries.incrementAndGet();
            addSlowQuery(executionTime);
        }


    }

    public List<TimeSeriesPoint> getTimeSeriesData(){
        return new ArrayList<>(timeSeriesData);
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
