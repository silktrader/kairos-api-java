package eu.silktrader.kairos.task;

import eu.silktrader.kairos.user.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskTimerRepository extends JpaRepository<TaskTimer, Long> {
  List<TaskTimer> findByTaskUser(User user);
}
