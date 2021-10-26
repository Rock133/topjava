package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static java.time.LocalDateTime.of;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {

    public static final int OUT_OF_BOUNDS_INDEX = 10;
    public static final int FIRST_MEAL_ID = START_SEQ + 2;

    public static final Meal userMeal1 = new Meal(FIRST_MEAL_ID, of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500);
    public static final Meal userMeal2 = new Meal(FIRST_MEAL_ID + 1, of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000);
    public static final Meal userMeal3 = new Meal(FIRST_MEAL_ID + 2, of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500);
    public static final Meal userMeal4 = new Meal(FIRST_MEAL_ID + 3, of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100);
    public static final Meal userMeal5 = new Meal(FIRST_MEAL_ID + 4, of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000);
    public static final Meal userMeal6 = new Meal(FIRST_MEAL_ID + 5, of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500);
    public static final Meal userMeal7 = new Meal(FIRST_MEAL_ID + 6, of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410);

    public static final List<Meal> userMeals = Arrays.asList(userMeal7, userMeal6, userMeal5, userMeal4, userMeal3, userMeal2, userMeal1);

    public static Meal getNew() {
        return new Meal(null, of(2020, Month.JANUARY, 31, 16, 0), "New meal from MealTestData", 1000);
    }

    public static Meal getUpdated() {
        return new Meal(FIRST_MEAL_ID, of(2020, Month.JANUARY, 30, 11, 0), "Update meal from MealTestData", 510);
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingRecursiveFieldByFieldElementComparator().isEqualTo(expected);
    }
}