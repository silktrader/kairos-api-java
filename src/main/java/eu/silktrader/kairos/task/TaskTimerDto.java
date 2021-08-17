package eu.silktrader.kairos.task;

import java.time.LocalDateTime;

public record TaskTimerDto(Long taskId, LocalDateTime timestamp) {
}
