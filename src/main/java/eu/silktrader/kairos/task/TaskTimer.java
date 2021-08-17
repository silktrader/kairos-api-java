package eu.silktrader.kairos.task;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

@Entity
public class TaskTimer {

  @Id
  @Column(name = "task_id")
  private Long taskId;

  @OneToOne
  @MapsId
  @JoinColumn(name = "task_id")
  private Task task;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDateTime timestamp;

  public Task getTask() {
    return task;
  }

  public void setTask(Task task) {
    this.task = task;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }
}
