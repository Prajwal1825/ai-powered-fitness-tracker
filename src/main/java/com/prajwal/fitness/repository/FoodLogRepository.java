package com.prajwal.fitness.repository;
import com.prajwal.fitness.model.FoodLog; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface FoodLogRepository extends JpaRepository<FoodLog,Long>{ List<FoodLog> findByUserIdOrderByIdDesc(Long userId); }
