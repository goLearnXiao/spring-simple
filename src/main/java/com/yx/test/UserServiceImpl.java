package com.yx.test;

import com.yx.spring.annotation.Autowired;
import com.yx.spring.annotation.Component;

/**
 * @author yangxiao
 * @date 2021/6/5 10:57
 */
@Component("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    FoodService foodService;

    @Override
    public void printName(String name) {
        System.out.println("========printName method======name:" + name);
        foodService.printFoodName();
    }

}
