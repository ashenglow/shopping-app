package test.shop.infrastructure.monitoring.model.query;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class SlowQueryInfo {
    private LocalDateTime timeStamp;
    private long executionTime;
    private String threadName;
    private String methodName;
    private String stackTrace;

    public SlowQueryInfo(LocalDateTime timeStamp, long executionTime, String threadName) {
        this.timeStamp = timeStamp;
        this.executionTime = executionTime;
        this.threadName = threadName;
        this.stackTrace = getStackTrace();
    }

    private String getStackTrace() {
        return Arrays.stream(Thread.currentThread().getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));
    }
}
