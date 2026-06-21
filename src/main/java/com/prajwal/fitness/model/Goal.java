package com.prajwal.fitness.model;
import jakarta.persistence.*;
@Entity @Table(name="goals")
public class Goal { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long userId; private Integer caloriesGoal; private Integer stepsGoal; private Integer workoutGoal; private Integer waterGoal;
 public Long getId(){return id;} public void setId(Long id){this.id=id;} public Long getUserId(){return userId;} public void setUserId(Long userId){this.userId=userId;} public Integer getCaloriesGoal(){return caloriesGoal;} public void setCaloriesGoal(Integer caloriesGoal){this.caloriesGoal=caloriesGoal;} public Integer getStepsGoal(){return stepsGoal;} public void setStepsGoal(Integer stepsGoal){this.stepsGoal=stepsGoal;} public Integer getWorkoutGoal(){return workoutGoal;} public void setWorkoutGoal(Integer workoutGoal){this.workoutGoal=workoutGoal;} public Integer getWaterGoal(){return waterGoal;} public void setWaterGoal(Integer waterGoal){this.waterGoal=waterGoal;}
}
