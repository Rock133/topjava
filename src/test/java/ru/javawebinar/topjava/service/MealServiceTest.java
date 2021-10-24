package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    private final int OUT_OF_BOUNDS_INDEX = 10;

    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    public void get() {
        Meal meal = service.get(USER_MEALS.get(0).getId(), USER_ID);
        assertMatch(meal, USER_MEALS.get(0));
    }

    @Test
    public void delete() {
        service.delete(USER_FIRST_MEAL, USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(USER_FIRST_MEAL, USER_ID));
    }

    @Test
    public void getBetweenInclusive() {
        assertMatch(service.getBetweenInclusive(LocalDate.of(2020, Month.JANUARY, 30),
                LocalDate.of(2020, Month.JANUARY, 30), USER_ID),
                USER_MEALS.get(USER_MEALS.size()-3), USER_MEALS.get(USER_MEALS.size()-2), USER_MEALS.get(USER_MEALS.size()-1));
    }

    @Test
    public void getAll() {
        assertMatch(service.getAll(USER_ID), USER_MEALS);
    }

    @Test
    public void update() {
        Meal updated = getUpdated();
        service.update(updated, USER_ID);
        assertMatch(service.get(USER_FIRST_MEAL, USER_ID), getUpdated());
    }

    @Test
    public void create() {
        Meal created = service.create(MealTestData.getNew(), USER_ID);
        Integer newId = created.getId();
        Meal newMeal = MealTestData.getNew();
        newMeal.setId(newId);
        assertMatch(created, newMeal);
        assertMatch(service.get(newId, USER_ID), newMeal);
    }

    @Test
    public void duplicateDateTimeCreate() {
        assertThrows(DataAccessException.class, () ->
                service.create(new Meal(null, USER_MEALS.get(0).getDateTime(), "Duplicate datetime create test meal", 1000), USER_ID));
    }

    @Test
    public void deletedNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(OUT_OF_BOUNDS_INDEX, USER_ID));
    }

    @Test
    public void deleteOtherUserMeal() {
        assertThrows(NotFoundException.class, () -> service.delete(USER_FIRST_MEAL, ADMIN_ID));
    }

    @Test
    public void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(OUT_OF_BOUNDS_INDEX, USER_ID));
    }

    @Test
    public void getOtherUserMeal() {
        assertThrows(NotFoundException.class, () -> service.get(USER_FIRST_MEAL, ADMIN_ID));
    }

    @Test
    public void updateOtherUserMeal() {
        assertThrows(NotFoundException.class, () -> service.update(USER_MEALS.get(USER_MEALS.size()-1), ADMIN_ID));
        assertMatch(service.get(USER_FIRST_MEAL, USER_ID), USER_MEALS.get(USER_MEALS.size()-1));
    }
}
