package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;

import java.util.Collection;

import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

@Controller
public class MealRestController extends AbstractMealController {
    //private MealService service;

    public Collection<MealTo> getAll() {
        return super.getAll();
    }

    public Meal get(int id) {
        return super.get(id);
    }

    public Meal create(Meal meal) {
        return super.create(meal);
    }

    public void delete(int id) {
        super.delete(id);
    }

    public void update(Meal meal, int id) {
        super.update(meal, id);
    }


}