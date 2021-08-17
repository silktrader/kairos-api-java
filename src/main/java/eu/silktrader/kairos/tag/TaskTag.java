package eu.silktrader.kairos.tag;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import eu.silktrader.kairos.task.Task;

@Entity
public class TaskTag {

  @EmbeddedId
  private TaskTagId id;
  
  // @ManyToOne
  // @MapsId("habitId")
  // @JoinColumn(name = "habit_id")
  // private Habit habit;

  @ManyToOne(optional = false)
  @MapsId("taskId")
  @JoinColumn(nullable = false, updatable = false)
  private Task task;

  @ManyToOne(optional = false)
  @MapsId("tagId")
  @JoinColumn(nullable = false, updatable = false)
  private Tag tag;
  
}
