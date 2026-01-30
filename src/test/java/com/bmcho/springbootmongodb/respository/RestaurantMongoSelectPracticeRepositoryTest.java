package com.bmcho.springbootmongodb.respository;

import com.bmcho.springbootmongodb.entity.Restaurant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class RestaurantMongoSelectPracticeRepositoryTest {

    @Autowired
    private RestaurantMongoSelectPracticeRepository restaurantMongoSelectPracticeRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void findByBorough() {
        String borough = "Brooklyn";
        Pageable  pageable = PageRequest.of(0, 10);
        Slice<Restaurant> result =
                restaurantMongoSelectPracticeRepository.findByBorough(borough, pageable);

        assertEquals(pageable.getPageSize(), result.getContent().size());
        System.out.println(objectMapper.writeValueAsString(result.getContent()));
    }

    @Test
    void findByCuisineOrderByNameAsc() {
        String cuisine = "Pizza";
        Pageable pageable = PageRequest.of(0, 20);
        Slice<Restaurant> result =
                restaurantMongoSelectPracticeRepository.findByCuisineOrderByNameAsc(cuisine, pageable);

        assertEquals(pageable.getPageSize(), result.getContent().size());
        System.out.println(objectMapper.writeValueAsString(result.getContent()));
    }
}