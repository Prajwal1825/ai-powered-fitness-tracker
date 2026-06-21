package com.prajwal.fitness.repository;
import com.prajwal.fitness.model.Goal; import org.springframework.data.jpa.repository.JpaRepository; import java.util.Optional;
public interface GoalRepository extends JpaRepository<Goal,Long>{ Optional<Goal> findTopByUserIdOrderByIdDesc(Long userId); }
