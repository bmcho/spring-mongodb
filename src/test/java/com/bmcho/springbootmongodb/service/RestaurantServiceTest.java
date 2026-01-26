package com.bmcho.springbootmongodb.service;

import com.bmcho.springbootmongodb.entity.Restaurant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RestaurantServiceTest {

    @Autowired
    RestaurantService restaurantService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void findByBorough() {
        String borough = "Brooklyn";
        int limit = 10;
        Pageable pageable = PageRequest.of(0, limit);
        List<Restaurant> restaurants = restaurantService.findByBoroughWithLimit(borough, pageable);

        assertEquals(limit, restaurants.size());

        String stringList = objectMapper.writeValueAsString(restaurants);

        System.out.println(stringList);

    }

}