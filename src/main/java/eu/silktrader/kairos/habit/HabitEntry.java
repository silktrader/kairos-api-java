package eu.silktrader.kairos.habit;

import java.time.LocalDate;

import javax.persistence.*;

@Entity
public class HabitEntry {

  @EmbeddedId
  private HabitEntryId id;

  private String comment;

  @ManyToOne
  @MapsId("habitId")
  @JoinColumn(name = "habit_id")
  private Habit habit;

  public HabitEntry() { }

  public HabitEntry(Long habitId, LocalDate date, String comment, Habit habit) {
    this.id = new HabitEntryId(habitId, date);
    this.comment = comment;
    this.habit = habit;
  }

  public HabitEntryId getId() {
    return id;
  }

  public void setId(HabitEntryId id) {
    this.id = id;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }
}
