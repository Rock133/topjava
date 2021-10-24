package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static java.time.LocalDateTime.of;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {

    public static final int USER_FIRST_MEAL = START_SEQ + 2;

    public static final List<Meal> USER_MEALS = Arrays.asList(
            new Meal(START_SEQ + 8, of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410),
            new Meal(START_SEQ + 7, of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
            new Meal(START_SEQ + 6, of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
            new Meal(START_SEQ + 5, of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
            new Meal(START_SEQ + 4, of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
            new Meal(START_SEQ + 3, of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
            new Meal(START_SEQ + 2, of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500)
    );

    public static final List<Meal> ADMIN_MEALS = Arrays.asList(
            new Meal(START_SEQ + 9, of(2015, Month.JUNE, 1, 14, 0), "Админ ланч", 510),
            new Meal(START_SEQ + 10, of(2015, Month.JUNE, 1, 20, 0), "Админ ужин", 1500)
    );

    public static Meal getNew() {
        return new Meal(null, of(2020, Month.JANUARY, 31, 14, 0), "New meal from MealTestData", 1000);
    }

    public static Meal getUpdated() {
        Meal updated = new Meal();
        updated.setId(USER_FIRST_MEAL);
        updated.setDateTime(of(2020, Month.JANUARY, 30, 11, 0));
        updated.setDescription("Update meal from MealTestData");
        updated.setCalories(510);
        return updated;
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