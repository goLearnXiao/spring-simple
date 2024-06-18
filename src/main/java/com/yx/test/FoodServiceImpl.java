package com.yx.test;

import com.yx.spring.annotation.Component;

/**
 * @author yangxiao
 * @date 2021/6/5 11:00
 */
@Component("foodService")
public class FoodServiceImpl implements FoodService {

    @Override
    public void printFoodName() {
        System.out.println("==========printFoodName method===========");

    }
}
