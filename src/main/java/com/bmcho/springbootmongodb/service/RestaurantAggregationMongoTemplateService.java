package com.bmcho.springbootmongodb.service;

import com.bmcho.springbootmongodb.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantAggregationMongoTemplateService {

    private final MongoTemplate mongoTemplate;

    /*
    AGGREGATION 연습 문제 18개
    1. borough별 식당 수 집계 (내림차순 Top 5)
    2. cuisine별 식당 수 집계 (Top 10)
    3. borough="Brooklyn"에서 cuisine별 식당 수 Top 5
    4. grades unwind 후, cuisine별 평균 score 구하기 (Top 10)
    5. borough별 평균 score 구하기 (unwind + group)
    6. “A 등급 받은 횟수”가 가장 많은 식당 Top 10(group[name,borough] (name + count)
    7. 식당별 최고 점수(max score)와 최저 점수(min score) 구하기
    8. borough별로 score 90 이상 기록이 있는 식당 수 집계
    9. cuisine별로 “A 등급 비율(A count / 전체 grades count)” 구해서 Top 10
    10. grades.date 기준 “가장 최근 검사일”을 식당별로 구하고, 최근순 Top 20
    11. borough="Manhattan"에서 “최근 검사 등급”이 A인 식당만 추려서 20개
    12. address.zipcode별 식당 수 집계 Top 10
    13. 같은 (borough, cuisine) 조합 별 식당 수 집계 Top 20
    14. borough별로 상위 3개 cuisine 뽑기 (group + push + slice)
    15. cuisine별 평균 score 상위 5개 식당 리스트 뽑기 (topN 형태로 push + slice)
    16. grades.score가 0~100 범위 밖 데이터가 있는지 이상치 탐지(count)
    17. coords를 이용해 “위도 범위/경도 범위” 필터 후 borough별 count
    18. $facet 사용:
    * facet1: borough Top 5
    * facet2: cuisine Top 5
    * facet3: score 평균 Top 5
     */

    //1. borough별 식당 수 집계
    public List<BoroughCount> findGroupByBoroughWithCount() {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.group("borough")
                        .count().as("count")
        );


        return mongoTemplate.aggregate(
                        agg, "restaurants", BoroughCount.class)
                .getMappedResults();
    }

    //2. cuisine별 식당 수 집계 (Top 10)
    public List<CuisineCount> findGroupByCuisineWithCount() {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.group("cuisine")
                        .count().as("count")
        );


        return mongoTemplate.aggregate(
                        agg, "restaurants", CuisineCount.class)
                .getMappedResults();
    }

    //3. borough="Brooklyn"에서 cuisine별 식당 수
    public List<CuisineCountWithBorough> findGroupByCuisineWithBorough(String borough) {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(
                        Criteria.where("borough").is(borough)
                ),
                Aggregation.group("cuisine")
                        .count().as("count"),
                Aggregation.count().as("rowCount")
        );
        return mongoTemplate.aggregate(
                        agg, "restaurants", CuisineCountWithBorough.class)
                .getMappedResults();
    }

    //4. grades unwind 후, cuisine별 평균 score 구하기
    public List<CuisineScoreAvg> findGroupByCuisineScoreWithAvg() {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.unwind("grades"),
                Aggregation.group("cuisine")
                        .avg("grades.score").as("avgScore"),
                Aggregation.project()
                        .and("_id").as("cuisine")
                        .and(ArithmeticOperators.Round.roundValueOf("avgScore").place(0)).as("avgScore")
                        .andExclude("_id")
        );

        return mongoTemplate.aggregate(
                        agg, "restaurants", CuisineScoreAvg.class)
                .getMappedResults();
    }

    //5. borough별 평균 score 구하기 (unwind + group)
    public List<CuisineScoreAvg> findGroupByBoroughScoreWithAvg() {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.unwind("grades"),
                Aggregation.group("borough")
                        .avg("grades.score").as("avgScore"),
                Aggregation.project()
                        .and("_id").as("borough")
                        .and(ArithmeticOperators.Round.roundValueOf("avgScore").place(0)).as("avgScore")
                        .andExclude("_id")
        );

        return mongoTemplate.aggregate(
                        agg, "restaurants", CuisineScoreAvg.class)
                .getMappedResults();
    }

    //6. “A 등급 받은 횟수”가 가장 많은 식당 Top 10 (name + count)
    public List<BestRestaurant>  findGroupByBestRestaurantWithCount() {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.unwind("grades"),
                Aggregation.match(
                        Criteria.where("grades.grade").is("A")
                ),
                Aggregation.group("restaurant_id")
                        .count().as("countOfA")
                        .first("name").as("name"),
                Aggregation.sort(Sort.Direction.DESC, "countOfA"),
                Aggregation.limit(10),
                Aggregation.project()
                        .and("_id").as("restaurantId")
                        .and("_id.borough").as("borough")
                        .and("countOfA").as("countOfA")
                        .andExclude("_id")
        );
        return mongoTemplate.aggregate(
                        agg, "restaurants", BestRestaurant.class)
                .getMappedResults();
    }

    //7. 식당별 최고 점수(max score)와 최저 점수(min score) 구하기
    public List<RestaurantMinMaxScore>  findGroupByRestaurantWithMinMaxScore() {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.addFields()
                        .addField("minScore")
                        .withValue(
                                AccumulatorOperators.Min.minOf("grades.score")
                        ).build(),
                Aggregation.addFields()
                        .addField("maxScore")
                        .withValue(
                                AccumulatorOperators.Max.maxOf("grades.score")
                        ).build(),

                Aggregation.project()
                        .and("restaurant_id").as("restaurantId")
                        .and(ConditionalOperators.ifNull("maxScore").then(0)).as("maxScore")
                        .and(ConditionalOperators.ifNull("minScore").then(0)).as("minScore")
                        .andExclude("_id")

        );

        return mongoTemplate.aggregate(
                        agg, "restaurants", RestaurantMinMaxScore.class)
                .getMappedResults();
    }



}
