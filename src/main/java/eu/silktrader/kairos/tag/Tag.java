package eu.silktrader.kairos.tag;

import eu.silktrader.kairos.user.User;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.*;

@Entity
public class Tag {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Size(min = 3, max = 25)
  private String title;

  private String description;

  @NotNull
  @Min(0)
  @Max(360)
  private Short colour;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @OneToMany(mappedBy = "tag")
  private Set<TaskTag> taskTags = new HashSet<>();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Short getColour() {
    return colour;
  }

  public void setColour(Short colour) {
    this.colour = colour;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Set<TaskTag> getTaskTags() {
    return taskTags;
  }

  public void setTaskTags(Set<TaskTag> taskTags) {
    this.taskTags = taskTags;
  }
}
