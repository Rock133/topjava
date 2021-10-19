package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(meal -> save(meal, 1));
        MealsUtil.meals2.forEach(meal -> save(meal, 2));
    }

    @Override
    // null if updated meal do not belong to userId
    public Meal save(Meal meal, int userId) {
        log.info("save meal {} for user {}", meal, userId);
        Map<Integer, Meal> meals = repository.computeIfAbsent(userId, map -> new ConcurrentHashMap<>());
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            meals.put(meal.getId(), meal);
            return meal;
        }
        // handle case: update, but not present in storage
        return meals.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    // false if meal do not belong to userId
    public boolean delete(int id, int userId) {
        log.info("save meal {} for user {}", id, userId);
        Map<Integer, Meal> meals = repository.get(userId);
        return meals != null && meals.remove(id) != null;
    }

    @Override
    // null if meal do not belong to userId
    public Meal get(int id, int userId) {
        log.info("get meal {} for user {}", id, userId);
        Map<Integer, Meal> meals = repository.get(userId);
        return meals == null ? null : meals.get(id);
    }

    @Override
    // ORDERED dateTime desc
    public List<Meal> getAll(int userId) {
        log.info("getAll for user {}", userId);
        Map<Integer, Meal> meals = repository.get(userId);
        return meals == null ? Collections.emptyList() :
                meals.values().parallelStream()
                        .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                        .collect(Collectors.toList());
    }

    @Override
    public List<MealTo> getFiltered(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId, int caloriesPerDay) {
        log.info("getFiltered for user {}", userId);
        Map<Integer, Meal> meals = repository.get(userId);
        List<MealTo> mealsTo = MealsUtil.getTos(meals.values(), caloriesPerDay);
        return mealsTo.stream()
                .filter(meal -> DateTimeUtil.filterByDate(meal.getDateTime().toLocalDate(), startDateTime.toLocalDate(), endDateTime.toLocalDate()))
                .filter(meal -> DateTimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startDateTime.toLocalTime(), endDateTime.toLocalTime()))
                .sorted(Comparator.comparing(MealTo::getDateTime).reversed())
                .collect(Collectors.toList());
    }
}

