package eu.silktrader.kairos.user;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import eu.silktrader.kairos.habit.Habit;
import eu.silktrader.kairos.tag.Tag;
import eu.silktrader.kairos.task.Task;

import java.time.Instant;
import java.util.Set;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message="User name required")
    private String name;
    
    @NotBlank(message="Password required")
    private String password;

    @NotEmpty(message="Valid email required")
    @Email
    private String email;
    
    private Instant created;

    @OneToMany(mappedBy = "user")
    private Set<Task> tasks;

    @OneToMany(mappedBy = "user")
    private Set<Habit> habits;

    @OneToMany(mappedBy = "user")
    private Set<Tag> tags;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public User(Long id, String name, String password, String email, Instant created) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.created = created;
    }

    public User() { }
}
