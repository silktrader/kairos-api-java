package eu.silktrader.kairos.habit;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Embeddable
public class HabitEntryId implements Serializable {

    @Column(name = "habit_id")
    private Long habitId;

    private LocalDate date;

    public HabitEntryId() { }

    public HabitEntryId(Long habitId, LocalDate date) {
        this.habitId = habitId;
        this.date = date;
    }

    public Long getHabitId() {
        return habitId;
    }

    public void setHabitId(Long habitId) {
        this.habitId = habitId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, habitId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HabitEntryId other = (HabitEntryId) obj;
        return Objects.equals(date, other.date) && Objects.equals(habitId, other.habitId);
    }

   
}