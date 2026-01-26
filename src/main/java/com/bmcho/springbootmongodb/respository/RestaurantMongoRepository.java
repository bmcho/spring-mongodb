package com.bmcho.springbootmongodb.respository;

import com.bmcho.springbootmongodb.entity.Restaurant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantMongoRepository extends MongoRepository<Restaurant, String> {

    List<Restaurant> findByBorough(String Borough);

    List<Restaurant> findByBorough(String Borough, Pageable pageable);

}
