package eu.silktrader.kairos.task;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

public record TaskDto(Long id, Long previousId, @JsonFormat(pattern="yyyy-MM-dd") LocalDate date, String title, String details, Boolean complete, Integer duration) { }
