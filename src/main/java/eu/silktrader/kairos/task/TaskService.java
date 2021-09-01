package eu.silktrader.kairos.task;

import eu.silktrader.kairos.auth.AuthService;
import eu.silktrader.kairos.auth.ICurrentUserProvider;
import eu.silktrader.kairos.exception.BadRequestException;
import eu.silktrader.kairos.exception.ItemNotFoundException;
import eu.silktrader.kairos.tag.Tag;
import eu.silktrader.kairos.tag.TagService;
import eu.silktrader.kairos.tag.TaskTag;
import java.time.LocalDate;
//import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
//import java.util.function.Function;
import java.util.stream.Collectors;
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

    // create new task with elementary details
    final var task = new Task();
    task.setUser(userProvider.getUser(userName));
    // get previous task or throw relevant exceptions with matching responses
    task.setPrevious(
      getPreviousTask(taskDto.previousId(), taskDto.date(), userName)
    );
    task.setDate(taskDto.date());
    task.setTitle(taskDto.title());
    task.setDetails(taskDto.details());
    task.setComplete(taskDto.complete());
    task.setDuration(taskDto.duration());

    // save entity and update local variable with the entity's ID
    // the ID is required to create new task tag entries
    // tk can avoid tempTask??
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

  // ensure the integrity of each date's linked list of tasks
  private Task getPreviousTask(Long id, LocalDate date, String userName) {
    // prettier-ignore
    // no previous task is specified
    if (id == null) {
      // there already is a null previous task; throw an exception and 400
      if (taskRepository.existsByDateAndPreviousIsNullAndUserName(date, userName))
        throw new BadRequestException();

      // this is the first task of the day being added; a null value's legitimate
      return null;
    }

    // a previous task was specified
    else {
      // the previous task is already referenced by another task; throw and exception and 400
      if (taskRepository.existsByPreviousId(id)) throw new BadRequestException();

      // return the previous task, provided it exists; else throw an exception and 404
      return taskRepository
          .findByIdAndUserName(id, userName)
          .orElseThrow(ItemNotFoundException::new);
    }
  }

  private void addTaskTag(Task task, Tag tag) {
    final var taskTag = new TaskTag(task, tag);
    this.taskTagRepository.save(taskTag);
    task.getTaskTags().add(taskTag);
    // must save repository at the end of the process; possible room for issues if the save is omitted
  }

  public TaskDto getTask(Long id) {
    final var task = taskRepository
      .findById(id)
      .orElseThrow(ItemNotFoundException::new);
    return mapTask(task);
  }

  @Transactional
  public TaskDto updateTask(TaskDto taskDto) {
    final var userName = this.userProvider.getCurrentUserName();

    // ensure the task already exists and is owned by the user
    var task = taskRepository
      .findByIdAndUserName(taskDto.id(), userName)
      .orElseThrow(ItemNotFoundException::new);

    // validate the previous task when changes are detected or throw relevant exceptions
    // allows comparison between null and Long
    if (
      !Objects.equals(
        taskDto.previousId(),
        task.getPrevious() == null ? null : task.getPrevious().getId()
      )
    ) task.setPrevious(
      getPreviousTask(taskDto.previousId(), taskDto.date(), userName)
    );

    task.setTitle(taskDto.title());
    task.setDetails(taskDto.details());
    task.setDate(taskDto.date());
    task.setComplete(taskDto.complete());
    task.setDuration(taskDto.duration());

    // process tags
    Map<String, TaskTag> currentTags = task
      .getTaskTags()
      .stream()
      .collect(Collectors.toMap(t -> t.getTag().getTitle(), t -> t));

    var updatedTaskTags = new HashSet<TaskTag>();
    for (var tagTitle : taskDto.tags()) {
      updatedTaskTags.add(
        Objects.requireNonNullElseGet(
          currentTags.get(tagTitle), // the task tag already exists, use it
          () ->
            tagService
              .getTagByTitle(tagTitle)
              // the tag already exists
              .map(tag -> taskTagRepository.save(new TaskTag(task, tag)))
              // create a new tag
              .orElseGet(
                () ->
                  taskTagRepository.save(
                    new TaskTag(task, tagService.createTag(tagTitle))
                  )
              )
        )
      );
    }

    // overwrite task tags with new ones; old and orphaned task tags will be automatically removed by the cascade
    task.setTaskTags(updatedTaskTags);

    return mapTask(taskRepository.save(task));
  }

  public List<TaskDto> updateTasks(List<TaskDto> taskDtos) {
    final var userName = this.userProvider.getCurrentUserName();

    // gather ids and tags in one loop
    final var tasksMap = new HashMap<Long, TaskDto>();
    final var dtoTagTitles = new HashSet<String>();
    for (var taskDto : taskDtos) {
      tasksMap.put(taskDto.id(), taskDto);
      dtoTagTitles.addAll(taskDto.tags());
    }

    // ensure all the tasks exist and are owned by the relevant user
    var tasks = taskRepository.findByIdInAndUserName(
      List.copyOf(tasksMap.keySet()),
      userName
    );

    // fail when tasks to update are missing
    if (tasks.size() != taskDtos.size()) throw new ItemNotFoundException();

    // draft a dictionary of existing tags to avoid multiple DB lookups
    // Map<String, Tag> existingTags = tagService
    //   .getTagsByUserNameAndTitleIn(userName, List.copyOf(dtoTagTitles))
    //   .stream()
    //   .collect(Collectors.toMap(Tag::getTitle, Function.identity()));

    // overwrite task values
    for (var task : tasks) {
      // find matching dto
      var dto = tasksMap.get(task.getId());

      // locate the previous task or abort with eception
      var previousTask = dto.previousId() == null
        ? null
        : taskRepository
          .findByIdAndUserName(dto.previousId(), userName)
          .orElseThrow(ItemNotFoundException::new);

      // tk todo: handle task tags later!!

      task.setPrevious(previousTask); // tk optimise: too many lookups
      task.setTitle(dto.title());
      task.setDetails(dto.details());
      task.setDate(dto.date());
      task.setComplete(dto.complete());
      task.setDuration(dto.duration());
    }

    // provided no exceptions were thrown, save changes atomically
    return taskRepository.saveAll(tasks).stream().map(this::mapTask).toList();
  }

  @Transactional
  public DeletedTaskDto deleteTask(Long id) {
    // fetch the task ensuring the user owns it, else throw exception and consequent 404
    var task = taskRepository
      .findByIdAndUserName(id, userProvider.getCurrentUserName())
      .orElseThrow(ItemNotFoundException::new);

    // find whether another task references the deleted one, shorten the linked list accordingly,
    // delete the task and return the modifications
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
