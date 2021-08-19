package eu.silktrader.kairos.task;

import eu.silktrader.kairos.auth.AuthService;
import eu.silktrader.kairos.auth.ICurrentUserProvider;
import eu.silktrader.kairos.exception.ItemNotFoundException;
import eu.silktrader.kairos.tag.Tag;
import eu.silktrader.kairos.tag.TagService;
import eu.silktrader.kairos.tag.TaskTag;
import eu.silktrader.kairos.tag.TaskTagId;
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
  private final TaskTagRepository taskTagRepository;
  private final TagService tagService;
  private final ICurrentUserProvider userProvider;

  @Autowired
  public TaskService(
    AuthService authService,
    TaskRepository taskRepository,
    TaskTimerRepository taskTimerRepository,
    TaskTagRepository taskTagRepository,
    TagService tagService,
    ICurrentUserProvider userProvider
  ) {
    this.authService = authService;
    this.taskRepository = taskRepository;
    this.taskTimerRepository = taskTimerRepository;
    this.taskTagRepository = taskTagRepository;
    this.tagService = tagService;
    this.userProvider = userProvider;
  }

  @Transactional
  public TaskDto addTask(TaskDto taskDto) {
    final String userName = userProvider.getCurrentUserName();

    // when present find the previous task or throw; `Optional` would be pedantic
    final var previousTask = taskDto.previousId() == null
      ? null
      : taskRepository
        .findByIdAndUserName(taskDto.previousId(), userName)
        .orElseThrow(ItemNotFoundException::new);

    // create new task with elementary details
    var task = new Task();
    task.setUser(userProvider.getUser(userName));
    task.setPrevious(previousTask);
    task.setDate(taskDto.date());
    task.setTitle(taskDto.title());
    task.setDetails(taskDto.details());
    task.setComplete(taskDto.complete());
    task.setDuration(taskDto.duration());

    // save entity and update local variable with the entity's ID
    // the ID is required to create new task tag entries
    final var tempTask = taskRepository.save(task);

    // process tag titles
    for (var tagTitle : taskDto.tags()) {
      tagService
        .getTagByTitle(tagTitle)
        .ifPresentOrElse(
          tag -> addTaskTag(tempTask, tag),
          () -> addTaskTag(tempTask, tagService.createTag(tagTitle))
        );
    }

    // save tag relationships and add the automatically assigned ID to the DTO
    return mapTask(taskRepository.save(tempTask));
  }

  private void addTaskTag(Task task, Tag tag) {
    var taskTag = new TaskTag(new TaskTagId(tag.getId(), task.getId()));
    this.taskTagRepository.save(taskTag);
    task.getTaskTags().add(taskTag); // must save repository at the end of the process! possible room for issues tk
  }

  public TaskDto getTask(Long id) {
    var task = taskRepository
      .findById(id)
      .orElseThrow(ItemNotFoundException::new);
    return mapTask(task);
  }

  @Transactional
  public DeletedTaskDto deleteTask(Long id) {
    // fetch the task ensuring the user owns it, else throw exception and consequent 404
    var task = taskRepository
      .findByIdAndUserName(id, userProvider.getCurrentUserName())
      .orElseThrow(ItemNotFoundException::new);

    // find whether another task references the deleted one, shorten the linked list accordingly, delete the task and return the modifications
    // otherwise simply delete the task
    return taskRepository
      .findByPrevious(task)
      .map(
        next -> {
          next.setPrevious(task.getPrevious());
          taskRepository.delete(task);
          return new DeletedTaskDto(mapTask(taskRepository.save(next)));
        }
      )
      .orElseGet(
        () -> {
          taskRepository.delete(task);
          return new DeletedTaskDto(null);
        }
      );
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
      // can't use Optional in entities getters, due to serialisation and JPA issues
      task.getPrevious() == null ? null : task.getPrevious().getId(), // Optional.ofNullable(task.getPrevious()).map(Task::getId).orElse(null)
      task.getDate(),
      task.getTitle(),
      task.getDetails(),
      task.getComplete(),
      task.getDuration(),
      mapTaskTags(task)
    );
  }

  private TaskTimerDto mapTimer(TaskTimer taskTimer) {
    return new TaskTimerDto(
      taskTimer.getTask().getId(),
      taskTimer.getTimestamp()
    );
  }

  private List<String> mapTaskTags(Task task) {
    return task.getTaskTags().stream().map(t -> t.getTag().getTitle()).toList();
  }
}
