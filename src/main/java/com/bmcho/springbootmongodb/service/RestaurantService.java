package com.bmcho.springbootmongodb.service;

import com.bmcho.springbootmongodb.entity.Restaurant;
import com.bmcho.springbootmongodb.respository.RestaurantMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantMongoRepository restaurantMongoRepository;

    public List<Restaurant> findByBorough(String Borough) {
        return restaurantMongoRepository.findByBorough(Borough);
    }

    public List<Restaurant> findByBoroughWithLimit(String Borough, Pageable pageable) {
        return restaurantMongoRepository.findByBorough(Borough, pageable);
    }
}
