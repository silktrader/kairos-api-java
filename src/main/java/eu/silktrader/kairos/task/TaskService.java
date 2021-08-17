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
  private final TaskTimerRepository taskTimerRepository;

  @Autowired
  public TaskService(
    AuthService authService,
    TaskRepository taskRepository,
    TaskTimerRepository taskTimerRepository
  ) {
    this.authService = authService;
    this.taskRepository = taskRepository;
    this.taskTimerRepository = taskTimerRepository;
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
    return mapTask(task);
  }

  public TaskDto getTask(Long id) {
    var task = taskRepository
      .findById(id)
      .orElseThrow(ItemNotFoundException::new);
    return mapTask(task);
  }

  public void deleteTask(Long id) {
    taskRepository.deleteById(id);
  }

  // transactional readonly = true look into tk
  public List<TaskDto> getTasks(List<LocalDate> dates) {
    return taskRepository
      .findByDateInAndUser(dates, this.authService.getCurrentUser())
      .stream()
      .map(this::mapTask)
      .toList();
  }

  public List<TaskDto> getUnscheduledTasks() {
    return taskRepository
      .findByUserAndDateIsNull(this.authService.getCurrentUser())
      .stream()
      .map(this::mapTask)
      .toList();
  }

  public List<TaskTimerDto> getTaskTimers() {
    return taskTimerRepository
      .findByTaskUser(this.authService.getCurrentUser())
      .stream()
      .map(this::mapTimer)
      .toList();
  }

  private TaskDto mapTask(Task task) {
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

  private TaskTimerDto mapTimer(TaskTimer taskTimer) {
    return new TaskTimerDto(
      taskTimer.getTask().getId(),
      taskTimer.getTimestamp()
    );
  }
}
