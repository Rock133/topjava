package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(meal -> save(meal, 0));
        MealsUtil.meals2.forEach(meal -> save(meal, 1));
    }

    @Override
    // null if updated meal do not belong to userId
    public Meal save(Meal meal, int userId) {
        Map<Integer, Meal> meals = repository.computeIfAbsent(userId, ConcurrentHashMap::new);
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            log.info("save meal {} for user {}", meal.getId(), userId);
            meals.put(meal.getId(), meal);
            return meal;
        }
        // handle case: update, but not present in storage
        log.info("update oldmeal {} for user {}", meal.getId(), userId);
        return meals.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    // false if meal do not belong to userId
    public boolean delete(int id, int userId) {
        Map<Integer, Meal> meals = repository.get(userId);
        log.info("save meal {} for user {}", id, userId);
        return meals != null && meals.remove(id) != null;
    }

    @Override
    // null if meal do not belong to userId
    public Meal get(int id, int userId) {
        Map<Integer, Meal> meals = repository.get(userId);
        if (meals == null) {
            return null;
        }
        log.info("get meal {} for user {}", meals.get(id).getId(), userId);
        return meals.get(id);
    }

    @Override
    // ORDERED dateTime desc
    public List<Meal> getAll(int userId) {
        Map<Integer, Meal> meals = repository.get(userId);
        log.info("getAll for user {}", userId);
        return meals == null ? Collections.emptyList() :
                meals.values().parallelStream()
                        .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                        .collect(Collectors.toList());
    }
}

