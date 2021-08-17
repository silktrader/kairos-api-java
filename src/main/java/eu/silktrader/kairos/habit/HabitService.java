package eu.silktrader.kairos.habit;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import eu.silktrader.kairos.auth.AuthService;
import eu.silktrader.kairos.exception.ItemNotFoundException;

@Service
public class HabitService {

  private final AuthService authService;
  private final HabitRepository habitRepository;
  private final HabitEntryRepository habitEntryRepository;

  @Autowired
  public HabitService(AuthService authService, HabitRepository habitRepository,
      HabitEntryRepository habitEntryRepository) {
    this.authService = authService;
    this.habitRepository = habitRepository;
    this.habitEntryRepository = habitEntryRepository;
  }

  public List<HabitDto> getHabits() {
    var user = this.authService.getCurrentUser();
    return habitRepository.findByUser(user).stream().map(this::map).toList();
  }

  private Habit getHabit(Long id) {
    return habitRepository.findByIdAndUser(id, this.authService.getCurrentUser()).orElseThrow(ItemNotFoundException::new);
  }

  public HabitDto getHabitDto(Long id) {
    return map(getHabit(id));
  }

  public HabitDto add(HabitDto habitDto) {
    var habit = new Habit();
    habit.setUser(this.authService.getCurrentUser());
    habit.setTitle(habitDto.title());
    habit.setColour(habitDto.colour());
    habit.setDescription(habitDto.description());
    habit = this.habitRepository.save(habit);
    return map(habit);
  }

  public HabitEntryDto add(HabitEntryDto habitEntryDto) {

    // identify the habit and ensure the user owns it
    var habit = getHabit(habitEntryDto.habitId());


    var entry = new HabitEntry(habitEntryDto.habitId(), habitEntryDto.date(), habitEntryDto.comment(), habit);
    entry = this.habitEntryRepository.save(entry);
    return map(entry);
  }

  public void delete(Long id) {
    try {
      habitRepository.deleteById(id);
    } catch (EmptyResultDataAccessException e) {
      throw new ItemNotFoundException();
    }
  }

  public List<HabitEntryDto> getHabitsEntries(List<LocalDate> dates) {
    return this.habitEntryRepository.findByHabitUserAndIdDateIn(this.authService.getCurrentUser(), dates).stream()
        .map(e -> new HabitEntryDto(e.getId().getHabitId(), e.getId().getDate(), e.getComment())).toList();

  }

  private HabitDto map(Habit habit) {
    return new HabitDto(habit.getId(), habit.getTitle(), habit.getDescription(), habit.getColour());
  }

  private HabitEntryDto map(HabitEntry habitEntry) {
    return new HabitEntryDto(habitEntry.getId().getHabitId(), habitEntry.getId().getDate(), habitEntry.getComment());
  }

  // public List<HabitEntryDto> getHabitsEntries(List<LocalDate> dates) {
  //   return this.habitEntryRepository.findByIdDate(dates.stream().findFirst().orElseThrow()).stream()
  //       .map(e -> new HabitEntryDto(e.habitId(), e.date(), e.comment())).toList();

  // }
}
