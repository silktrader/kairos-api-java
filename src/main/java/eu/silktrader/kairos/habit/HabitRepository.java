package eu.silktrader.kairos.habit;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import eu.silktrader.kairos.user.User;

public interface HabitRepository extends JpaRepository<Habit, Long> {

  List<Habit> findByUser(User user);

  Optional<Habit> findByIdAndUser(Long id, User user);

}