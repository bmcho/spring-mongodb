package com.bmcho.springbootmongodb.respository;

import com.bmcho.springbootmongodb.entity.Restaurant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RestaurantMongoSelectPracticeRepository extends MongoRepository<Restaurant, String> {
    /*
     ** SELECT (find) 연습 문제 15개 **
    1.borough = "Brooklyn" 인 식당 10개 조회 (name, borough만)
    2.cuisine = "Pizza" 인 식당을 이름 오름차순으로 20개 조회
    3.borough in ["Manhattan","Queens"] 인 식당 조회 + borough별로 몇 개인지 같이 확인
    4.address.zipcode = "10019" 인 식당 조회 (name, address만)
    5.name에 "Donut"이 포함된 식당 조회 (대소문자 무시)
    6.name이 "Wild"로 시작하는 식당 조회
    7.restaurant_id가 특정 값인 식당 1건 조회
    8.grades.grade = "A"를 한 번이라도 받은 식당 조회 (최대 30개)
    9.grades.score >= 90을 한 번이라도 받은 식당 조회 (name, borough, grades만)
    10.grades 배열이 존재하고 길이가 5 이상인 식당 조회
    11.address.coord(lng, lat)가 둘 다 존재하는 식당만 조회
    12.projection 연습: _id 제외하고 (name, borough, cuisine)만 조회
    13.sort + limit: borough="Manhattan" 중 최신 grade.date 기준으로 상위 10개
    14.pagination: borough="Queens"를 page=2, size=10으로 조회 (skip/limit)
    15.“A는 있고 B는 없는” 식당 조회 (배열 조건 올바르게)
     */

    //1
    Slice<Restaurant> findByBorough(String Borough, Pageable pageable);
    //2
    Slice<Restaurant> findByCuisineOrderByNameAsc(String cuisine, Pageable pageable);

}
