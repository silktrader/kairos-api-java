package eu.silktrader.kairos.habit;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

public record HabitEntryDto(Long habitId, @JsonFormat(pattern="yyyy-MM-dd") LocalDate date, String comment) { }