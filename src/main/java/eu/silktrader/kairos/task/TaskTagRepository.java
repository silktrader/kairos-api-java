package eu.silktrader.kairos.task;

import eu.silktrader.kairos.tag.TaskTag;
import eu.silktrader.kairos.tag.TaskTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskTagRepository extends JpaRepository<TaskTag, TaskTagId> {}
