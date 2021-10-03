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

        List<UserMealWithExcess> mealsToByOneCycle = filteredByOneCycle(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsToByOneCycle.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        Map<LocalDate, Integer> dateAndCalories = new HashMap<>();

        for (UserMeal element : meals) {
            dateAndCalories.merge(element.getDateTime().toLocalDate(), element.getCalories(), Integer::sum);
        }

        List<UserMealWithExcess> resultMealsTo = new ArrayList<>();

        for (UserMeal element : meals) {
            if (TimeUtil.isBetweenHalfOpen(LocalTime.of(element.getDateTime().getHour(), element.getDateTime().getMinute()), startTime, endTime)) {
                resultMealsTo.add(new UserMealWithExcess(
                        element.getDateTime(),
                        element.getDescription(),
                        element.getCalories(),
                        dateAndCalories.get(element.getDateTime().toLocalDate()) > caloriesPerDay));
            }
        }

        return resultMealsTo;
    }

    public static List<UserMealWithExcess> filteredByOneCycle(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        Map<LocalDate, Integer> dateAndCalories = new HashMap<>();
        ArrayList<UserMealWithExcess> resultMealsTo = new ArrayList<>();

        class RecursiveHandler {

            int count = 0;

            void recursiveProcessing() {

                while (count < meals.size()) {
                    int i = count;                                          //Index of processing element
                    dateAndCalories.merge(meals.get(i).getDateTime().toLocalDate(), meals.get(i).getCalories(), Integer::sum);

                    if (TimeUtil.isBetweenHalfOpen(LocalTime.of(meals.get(i).getDateTime().getHour(),
                            meals.get(i).getDateTime().getMinute()), startTime, endTime)) {
                        count++;
                        if (count != meals.size()) {
                            recursiveProcessing();
                        }
                        resultMealsTo.add((new UserMealWithExcess(
                                meals.get(i).getDateTime(),
                                meals.get(i).getDescription(),
                                meals.get(i).getCalories(),
                                dateAndCalories.get(meals.get(i).getDateTime().toLocalDate()) > caloriesPerDay)));
                    }
                    count++;
                }
            }
        }

        RecursiveHandler recursiveHandler = new RecursiveHandler();
        recursiveHandler.recursiveProcessing();

        return resultMealsTo;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        Map<LocalDate, Integer> dateAndCalories = meals.stream()
                .collect(Collectors.toMap(um -> um.getDateTime().toLocalDate(),
                        UserMeal::getCalories,
                        Integer::sum));

        return meals.stream()
                .filter(um -> TimeUtil.isBetweenHalfOpen(LocalTime.of(
                        um.getDateTime().getHour(),
                        um.getDateTime().getMinute()),
                        startTime,
                        endTime))
                .map(um -> new UserMealWithExcess(
                        um.getDateTime(),
                        um.getDescription(),
                        um.getCalories(),
                        dateAndCalories.get(um.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());

    }
}
