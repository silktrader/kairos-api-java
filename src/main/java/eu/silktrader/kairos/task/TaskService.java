package eu.silktrader.kairos.task;

import eu.silktrader.kairos.auth.AuthService;
import eu.silktrader.kairos.exception.ItemNotFoundException;
import java.time.LocalDate;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

  private final TaskRepository taskRepository;
  private final AuthService authService;

  @Autowired
  public TaskService(AuthService authService, TaskRepository taskRepository) {
    this.authService = authService;
    this.taskRepository = taskRepository;
  }

  // test without above tk
  @Transactional
  public TaskDto addTask(TaskDto taskDto) {
    var task = new Task();
    task.setUser(this.authService.getCurrentUser());
    task.setPreviousId(taskDto.previousId());
    task.setDate(taskDto.date());
    task.setTitle(taskDto.title());
    task.setDetails(taskDto.details());
    task.setComplete(taskDto.complete());
    task.setDuration(taskDto.duration());
    task = taskRepository.save(task);

    // add the automatically assigned ID to the DTO
    return map(task);
  }

  public TaskDto getTask(Long id) {
    var task = taskRepository
      .findById(id)
      .orElseThrow(ItemNotFoundException::new);
    return map(task);
  }

  public void deleteTask(Long id) {
    taskRepository.deleteById(id);
  }

  // transactional readonly = true look into tk
  public List<TaskDto> getTasks(List<LocalDate> dates) {
    return taskRepository
      .findByDateInAndUser(dates, this.authService.getCurrentUser())
      .stream()
      .map(this::map)
      .toList();
  }

  public List<TaskDto> getUnscheduledTasks() {
    return taskRepository
      .findByUserAndDateIsNull(this.authService.getCurrentUser())
      .stream()
      .map(this::map)
      .toList();
  }

  private TaskDto map(Task task) {
    return new TaskDto(
      task.getId(),
      task.getPreviousId(),
      task.getDate(),
      task.getTitle(),
      task.getDetails(),
      task.getComplete(),
      task.getDuration()
    );
  }
}
