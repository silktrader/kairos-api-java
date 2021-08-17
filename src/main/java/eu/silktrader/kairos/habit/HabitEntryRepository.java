package eu.silktrader.kairos.habit;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
// import eu.silktrader.kairos.user.User;
import org.springframework.stereotype.Repository;

import eu.silktrader.kairos.user.User;

@Repository
public interface HabitEntryRepository extends JpaRepository<HabitEntry, HabitEntryId> {
  
  List<HabitEntry> findByHabitUserAndIdDateIn(User user, List<LocalDate> dates);

}