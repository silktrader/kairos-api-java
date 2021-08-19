package eu.silktrader.kairos.task;

import eu.silktrader.kairos.tag.TaskTag;
import eu.silktrader.kairos.user.User;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
public class Task {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "previous_id", referencedColumnName = "id")
  private Task previous;

  @NotEmpty
  @NotNull
  private String title;

  private String details;

  private LocalDate date; // null value signals unscheduled state

  @NotNull
  @Column(columnDefinition = "boolean default false")
  private Boolean complete;

  private Integer duration;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @OneToMany(mappedBy = "task")
  private Set<TaskTag> taskTags = new HashSet<>();

  @OneToOne(mappedBy = "task", cascade = CascadeType.ALL) // cascade facilitates removals
  @PrimaryKeyJoinColumn
  private TaskTimer taskTimer;

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getDuration() {
    return duration;
  }

  public void setDuration(Integer duration) {
    this.duration = duration;
  }

  public String getDetails() {
    return details;
  }

  public void setDetails(String details) {
    this.details = details;
  }

  public void setTaskTags(Set<TaskTag> taskTags) {
    this.taskTags = taskTags;
  }

  public Task getPrevious() {
    return previous;
  }

  public void setPrevious(Task previous) {
    this.previous = previous;
  }

  public TaskTimer getTaskTimer() {
    return taskTimer;
  }

  public void setTaskTimer(TaskTimer taskTimer) {
    this.taskTimer = taskTimer;
  }

  public Set<TaskTag> getTaskTags() {
    return taskTags;
  }

  // public Long getPreviousId() {
  //   return previousId;
  // }

  // public void setPreviousId(Long previousId) {
  //   this.previousId = previousId;
  // }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Boolean getComplete() {
    return complete;
  }

  public void setComplete(Boolean complete) {
    this.complete = complete;
  }
}
