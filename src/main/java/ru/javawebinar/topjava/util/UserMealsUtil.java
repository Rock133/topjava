package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.sql.SQLOutput;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        List<UserMealWithExcess> tmpMealsTo = new ArrayList<>();
        Map<LocalDate, Integer> dateAndCalories = new HashMap<>();
        List<UserMeal> mealNeeded = new ArrayList<>();

        for (UserMeal element : meals){
            LocalDate date = element.getDateTime().toLocalDate();
            if (!dateAndCalories.containsKey(date)) {
                dateAndCalories.put(date, 0);
            }
            if (dateAndCalories.containsKey(date)) {
                dateAndCalories.put(date, dateAndCalories.get(date)+element.getCalories());
            }
            if (TimeUtil.isBetweenHalfOpen(LocalTime.of(element.getDateTime().getHour(), element.getDateTime().getMinute()), startTime, endTime)) {
                mealNeeded.add(element);
            }
        }
        for (UserMeal element : mealNeeded) {
            LocalDate date = element.getDateTime().toLocalDate();
            int calories = dateAndCalories.get(date);
            boolean excess = calories>caloriesPerDay;
            tmpMealsTo.add(new UserMealWithExcess(element.getDateTime(), element.getDescription(), element.getCalories(), excess));
        }

        return tmpMealsTo;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        Map<LocalDate, Integer> dateAndCalories = meals.stream()
                .collect(Collectors.toMap((x) -> x.getDateTime().toLocalDate(), (x) -> x.getCalories(),
                        (oldValue, newValue) -> oldValue + newValue));

        List<UserMealWithExcess> tmpMealsTo = meals.stream()
                .filter (x -> TimeUtil.isBetweenHalfOpen(LocalTime.of(x.getDateTime().getHour(), x.getDateTime().getMinute()), startTime, endTime))
                .map((x) -> new UserMealWithExcess(x.getDateTime(), x.getDescription(), x.getCalories(), dateAndCalories.get(x.getDateTime().toLocalDate())>caloriesPerDay))
                .collect(Collectors.toList());

        return tmpMealsTo;
    }
}
