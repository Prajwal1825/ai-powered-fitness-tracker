package com.prajwal.fitness.model;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="workout_logs")
public class WorkoutLog { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long userId; private String workoutName; private Integer durationMinutes; private Integer caloriesBurned; private LocalDateTime createdAt=LocalDateTime.now();
 public Long getId(){return id;} public void setId(Long id){this.id=id;} public Long getUserId(){return userId;} public void setUserId(Long userId){this.userId=userId;} public String getWorkoutName(){return workoutName;} public void setWorkoutName(String workoutName){this.workoutName=workoutName;} public Integer getDurationMinutes(){return durationMinutes;} public void setDurationMinutes(Integer durationMinutes){this.durationMinutes=durationMinutes;} public Integer getCaloriesBurned(){return caloriesBurned;} public void setCaloriesBurned(Integer caloriesBurned){this.caloriesBurned=caloriesBurned;} public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime createdAt){this.createdAt=createdAt;}
}
