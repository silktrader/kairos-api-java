package eu.silktrader.kairos.task;

import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("tasks")
public class TaskController {

  private final TaskService taskService;

  public TaskController(TaskService taskService) {
    this.taskService = taskService;
  }

  @GetMapping
  public List<TaskDto> getTasks(
    @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE
    ) List<LocalDate> dates
  ) {
    return taskService.getTasks(dates);
  }

  @GetMapping("/unscheduled")
  public List<TaskDto> getUnscheduledTasks() {
    return taskService.getUnscheduledTasks();
  }

  @GetMapping("/timers")
  public List<TaskTimerDto> getTaskTimers() {
    return taskService.getTaskTimers();
  }

  @GetMapping("/{id}")
  public TaskDto getTask(@PathVariable("id") Long id) {
    return taskService.getTask(id);
  }

  @PostMapping
  public ResponseEntity<TaskDto> addTask(@RequestBody TaskDto taskDto) {
    // two tasks with the same name are allowed on purpose
    return new ResponseEntity<>(
      taskService.addTask(taskDto),
      HttpStatus.CREATED
    );
  }
}
