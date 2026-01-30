package com.bmcho.springbootmongodb.service;

import com.bmcho.springbootmongodb.entity.BoroughCount;
import com.bmcho.springbootmongodb.entity.Restaurant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class RestaurantMongoTemplateServiceTest {

    @Autowired
    private RestaurantMongoTemplateService restaurantMongoTemplateService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findByBoroughWithLimit() {
        String borough = "Brooklyn";
        int limit = 10;

        List<Restaurant> result = restaurantMongoTemplateService.findByBoroughWithLimit(borough, limit);

        assertEquals(limit, result.size());
        System.out.println(objectMapper.writeValueAsString(result));
    }

    @Test
    void findByCuisineWithLimitSortByName() {
        String cuisine = "Pizza";
        int limit = 20;
        Sort.Direction direction = Sort.Direction.ASC;


        List<Restaurant> result =
                restaurantMongoTemplateService.findByCuisineWithLimitSortByName(cuisine, direction, limit);

        assertEquals(limit, result.size());
        System.out.println(objectMapper.writeValueAsString(result));

    }

    @Test
    void aggregateRestaurantCountByBorough() {
        /*
            _id: 'Queens',
            count: 738
            _id: 'Manhattan',
            count: 1883
         */

        List<String> boroughs = List.of("Manhattan", "Queens");
        List<BoroughCount> result =
                restaurantMongoTemplateService.aggregateRestaurantCountByBorough(boroughs);

        assertThat(result).hasSize(2);
        for (BoroughCount boroughCount : result) {
            if (boroughCount.id().equals("Manhattan")) {
                assertThat(boroughCount.count()).isEqualTo(1883);
            } else {
                assertThat(boroughCount.count()).isEqualTo(738);
            }

        }
        System.out.println(objectMapper.writeValueAsString(result));
    }

    @Test
    void findRestaurantsByAddressZipcode() {
        String zipcode = "11225"; // 9개

        List<Restaurant> result = restaurantMongoTemplateService.findRestaurantsByAddressZipcode(zipcode);
        assertThat(result).hasSize(9);
    }

    @Test
    void findRestaurantsByContainingName() {
        String containingName = "Donut"; // 50개
        List<Restaurant> result = restaurantMongoTemplateService.findRestaurantsByContainingName(containingName);
        assertThat(result.getFirst().getName()).contains(containingName);
        assertThat(result.size()).isEqualTo(50);
    }

    @Test
    void findRestaurantsByStartName() {
        String startName = "Wild"; //1개
        List<Restaurant> result = restaurantMongoTemplateService.findRestaurantsByStartName(startName);
        assertThat(result.getFirst().getName()).contains(startName);
        assertThat(result).hasSize(1);
    }

    @Test
    void findRestaurantByRestaurantId() {
        String restaurantId = "40357217";
        Optional<Restaurant> result = restaurantMongoTemplateService.findRestaurantByRestaurantId(restaurantId);
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getRestaurantId()).isEqualTo(restaurantId);
    }

    @Test
    void findByGradesGrade() {
        String grade = "A";
        int limit = 30;
        List<Restaurant> result = restaurantMongoTemplateService.findByGradesGrade(grade, limit);
        assertThat(result).hasSize(limit);

    }

    @Test
    void findByGradesGradeScore() {
        int score = 50; //76
        int limit = 0;
        List<Restaurant> result = restaurantMongoTemplateService.findByGradesGradeScore(score, limit);
        assertThat(result).hasSize(76);

    }

    @Test
    void findByGradesSizeGte() {
        int size = 5; //2252
        List<Restaurant> result = restaurantMongoTemplateService.findByGradesSizeGte(size);
        assertThat(result).hasSize(2252);
    }

    @Test
    void findWithProjection() {
        String[] includes = {"name", "borough", "cuisine"};
        List<Restaurant> result = restaurantMongoTemplateService.findWithProjection(includes);
        Restaurant restaurant = result.getFirst();

        System.out.println(objectMapper.writeValueAsString(restaurant));
    }

    @Test
    void findByBoroughSortByGradeDate() {
        String firstId = "40369461";
        String borough = "Manhattan";
        Sort.Direction direction = Sort.Direction.DESC;
        int limit = 10;
        List<Restaurant> result = restaurantMongoTemplateService.findByBoroughSortByGradeDate(borough, limit, direction);
        Restaurant restaurant = result.getFirst();
        assertThat(restaurant.getRestaurantId()).isEqualTo(firstId);
    }

    @Test
    void findByBoroughWithPagination() {
        String borough = "Queens";
        Pageable pageable = PageRequest.of(2, 10);
        List<Restaurant> result = restaurantMongoTemplateService.findByBoroughWithPagination(borough, pageable);
        assertThat(result).hasSize(pageable.getPageSize());
        assertThat(result.getFirst().getBorough()).isEqualTo(borough);
    }

    @Test
    void findGradeHasAndNo() {
        String has = "A"; //2247개
        String no= "B";
        List<Restaurant> result = restaurantMongoTemplateService.findGradeHasAndNo(has, no);
        assertThat(result).hasSize(2247);
    }
}