package eu.silktrader.kairos.tag;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TaskTagId implements Serializable {

  @Column(name = "tag_id")
  private Long tagId;

  @Column(name = "task_id")
  private Long taskId;

  public TaskTagId() {}

  public TaskTagId(Long tagId, Long taskId) {
    this.tagId = tagId;
    this.taskId = taskId;
  }

  public Long getTagId() {
    return tagId;
  }

  public void setTagId(Long tagId) {
    this.tagId = tagId;
  }

  public Long getTaskId() {
    return taskId;
  }

  public void setTaskId(Long taskId) {
    this.taskId = taskId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(tagId, taskId);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    TaskTagId other = (TaskTagId) obj;
    return Objects.equals(tagId, other.tagId) && Objects.equals(taskId, other.taskId);
  }

}
