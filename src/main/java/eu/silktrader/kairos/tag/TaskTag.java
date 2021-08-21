package eu.silktrader.kairos.tag;

import eu.silktrader.kairos.task.Task;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@Entity
public class TaskTag {

  @EmbeddedId
  private TaskTagId id;

  @ManyToOne(optional = false)
  @MapsId("taskId")
  @JoinColumn(nullable = false, updatable = false)
  private Task task;

  @ManyToOne(optional = false)
  @MapsId("tagId")
  @JoinColumn(nullable = false, updatable = false)
  private Tag tag;

  public TaskTag() {}

  public TaskTag(Long tagId, Long taskId) {
    this.id = new TaskTagId(tagId, taskId);
  }

  public Task getTask() {
    return task;
  }

  public Tag getTag() {
    return tag;
  }
}
