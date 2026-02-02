package com.bmcho.springbootmongodb.service;

import com.bmcho.springbootmongodb.entity.BoroughCount;
import com.bmcho.springbootmongodb.entity.Restaurant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantSelectMongoTemplateService {

    private final MongoTemplate mongoTemplate;

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
    12.projection 연습: _id 제외하고 (name, borough, cuisine)만 조회
    13.sort + limit: borough="Manhattan" 중 최신 grade.date 기준으로 상위 10개
    14.pagination: borough="Queens"를 page=2, size=10으로 조회 (skip/limit)
    15.“A는 있고 B는 없는” 식당 조회 (배열 조건 올바르게)
     */

    //1.borough = "Brooklyn" 인 식당 10개 조회 (name, borough만)
    public List<Restaurant> findByBoroughWithLimit(String borough, int limit) {
        Query q = Query.query(Criteria.where("borough").is(borough))
                .limit(limit);
        q.fields()
                .exclude("_id")
                .include("name")
                .include("borough");

        return mongoTemplate.find(q, Restaurant.class);
    }

    //2.cuisine = "Pizza" 인 식당을 이름 오름차순으로 20개 조회
    public List<Restaurant> findByCuisineWithLimitSortByName(String cuisine, Sort.Direction direction, int limit) {
        Query q = Query.query(Criteria.where("cuisine").is(cuisine))
                .with(Sort.by(direction, "name"))
                .limit(limit);
        q.fields().exclude("_id").include("name").include("cuisine");

        return mongoTemplate.find(q, Restaurant.class);
    }

    //3.borough in ["Manhattan","Queens"] 인 식당 조회 + borough별로 몇 개인지 같이 확인
    public List<BoroughCount> aggregateRestaurantCountByBorough(List<String> boroughs) {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(
                        Criteria.where("borough").in(boroughs)
                ),
                Aggregation.group("borough").count().as("count")
        );

        return mongoTemplate.aggregate(agg, "restaurants", BoroughCount.class).getMappedResults();
    }

    //4.address.zipcode = "10019" 인 식당 조회 (name, address만)
    public List<Restaurant> findRestaurantsByAddressZipcode(String addressZipcode) {
        Query q = Query.query(
                Criteria.where("address.Zipcode").is(addressZipcode)
        );
        q.fields()
                .exclude("_id")
                .include("name")
                .include("address");


        return mongoTemplate.find(q, Restaurant.class);
    }

    //5.name에 "Donut"이 포함된 식당 조회 (대소문자 무시)
    public List<Restaurant> findRestaurantsByContainingName(String name) {
        Query q = Query.query(
                Criteria.where("name").regex(name, "i")
        );

        return mongoTemplate.find(q, Restaurant.class);
    }

    //6.name이 "Wild"로 시작하는 식당 조회
    public List<Restaurant> findRestaurantsByStartName(String name) {
        Query q = Query.query(
                Criteria.where("name").regex("^" + name, "i")
        );
        return mongoTemplate.find(q, Restaurant.class);
    }

    //7.restaurant_id가 특정 값인 식당 1건 조회
    public Optional<Restaurant> findRestaurantByRestaurantId(String restaurantId) {
        Query q = Query.query(
                Criteria.where("restaurant_id").is(restaurantId)
        );

        Restaurant result = mongoTemplate.findOne(q, Restaurant.class);
        return Optional.ofNullable(result);
    }

    //8.grades.grade = "A"를 한 번이라도 받은 식당 조회 (최대 30개)
    public List<Restaurant> findByGradesGrade(String grade, int limit) {
        Query q = Query.query(
                Criteria.where("grades.grade").in(grade)
        ).limit(limit);

        return mongoTemplate.find(q, Restaurant.class);
    }

    // 9.grades.score >= 90을 한 번이라도 받은 식당 조회 (name, borough, grades만)
    public List<Restaurant> findByGradesGradeScore(int score, int limit) {
        Query q = Query.query(
                Criteria.where("grades.score").gte(score)
        );

        if (limit > 0) q.limit(limit);

        q.fields()
                .exclude("_id")
                .include("name")
                .include("borough")
                .include("grade");

        return mongoTemplate.find(q, Restaurant.class);
    }

    //10.grades 배열이 존재하고 길이가 5 이상인 식당 조회
    public List<Restaurant> findByGradesSizeGte(int size) {
        Query q = Query.query(
                Criteria.expr(
                        ComparisonOperators.Gte.valueOf(
                                ArrayOperators.Size.lengthOfArray("grades")
                        ).greaterThanEqualToValue(size)
                )
        );

        return mongoTemplate.find(q, Restaurant.class);
    }


    //12.projection 연습: _id 제외하고 (name, borough, cuisine)만 조회
    public List<Restaurant> findWithProjection(String... includes) {
        Query q = new Query();
        q.fields().exclude("_id");
        for (String f : includes) {
            q.fields().include(f);
        }
        return mongoTemplate.find(q, Restaurant.class);
    }

    //13.sort + limit: borough="Manhattan" 중 최신 grade.date 기준으로 상위 10개
    public List<Restaurant> findByBoroughSortByGradeDate(String borough, int limit, Sort.Direction direction) {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(
                        Criteria.where("borough").is(borough)
                ),
                Aggregation.addFields()
                        .addField("lastGradeDate")
                        .withValue(
                                AccumulatorOperators.Max.maxOf("grades.date")
                        ).build(),
                Aggregation.sort(direction, "lastGradeDate"),
                Aggregation.limit(limit)
        );

        return mongoTemplate.aggregate(
                        agg, "restaurants", Restaurant.class)
                .getMappedResults();
    }

    //14.pagination: borough="Queens"를 page=2, size=10으로 조회 (skip/limit)
    public List<Restaurant> findByBoroughWithPagination(String borough, Pageable pageable) {
        Query q = Query.query(
                        Criteria.where("borough").is(borough)
                )
                .skip(pageable.getPageNumber())
                .limit(pageable.getPageSize());

        return mongoTemplate.find(q, Restaurant.class);
    }

    //15.“A는 있고 B는 없는” 식당 조회 (배열 조건 올바르게)
    public List<Restaurant> findGradeHasAndNo(String has, String no) {
        Criteria c = new Criteria().andOperator(
                Criteria.where("grades.grade").is(has),
                Criteria.where("grades.grade").nin(no)
        );
        Query query = Query.query(c);

        return mongoTemplate.find(query, Restaurant.class);
    }

}
