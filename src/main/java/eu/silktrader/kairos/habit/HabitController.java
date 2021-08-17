package eu.silktrader.kairos.habit;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("habits")
public class HabitController {

  private final HabitService habitService;

  @Autowired
  public HabitController(HabitService habitService) {
    this.habitService = habitService;
  }

  @GetMapping
  public Iterable<HabitDto> getHabits() {
    // defaults to OK status, code 200
    return this.habitService.getHabits();
  }

  @GetMapping("/{id}")
  public HabitDto getHabit(@PathVariable Long id) {
    return this.habitService.getHabitDto(id);
  }

  @PostMapping
  public ResponseEntity<HabitDto> addHabit(@RequestBody HabitDto habitDto) {
    return new ResponseEntity<>(this.habitService.add(habitDto), HttpStatus.CREATED);
  }

  // returns 200 status by default, which seems acceptable
  @DeleteMapping("/{id}")
  public void deleteHabit(@PathVariable("id") Long id) {
    this.habitService.delete(id);
  }

  @GetMapping("/entries")
  public List<HabitEntryDto> getHabitsEntries(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) List<LocalDate> dates) {
    return this.habitService.getHabitsEntries(dates);
  }

  @PostMapping("/entries")
  public ResponseEntity<HabitEntryDto> addEntry(@RequestBody HabitEntryDto habitEntryDto) {
    return new ResponseEntity<>(this.habitService.add(habitEntryDto), HttpStatus.CREATED);
  }
  
}
