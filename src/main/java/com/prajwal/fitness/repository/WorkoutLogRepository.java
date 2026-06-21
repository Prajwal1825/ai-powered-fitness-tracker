package com.prajwal.fitness.repository;
import com.prajwal.fitness.model.WorkoutLog; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface WorkoutLogRepository extends JpaRepository<WorkoutLog,Long>{ List<WorkoutLog> findByUserIdOrderByIdDesc(Long userId); }
