package com.bmcho.springbootmongodb.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Document("restaurants")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Restaurant {

    private String borough;
    private String cuisine;
    private String name;

    @Field("restaurant_id")
    private String restaurantId;

    private Address address;
    private List<Grade> grades;


    @Builder
    protected Restaurant(String borough, String cuisine, String name, String restaurantId,
                         Address address, List<Grade> grades
    ) {
        this.borough = borough;
        this.cuisine = cuisine;
        this.name = name;
        this.restaurantId = restaurantId;
        this.address = address;
        this.grades = grades;
    }


    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Address {
        private String building;
        private String street;
        private String zipcode;

        // MongoDB: [ -73.856077, 40.848447 ]
        private List<Double> coord;

        @Builder
        protected Address(String building, String street, String zipcode, List<Double> coord) {
            this.building = building;
            this.street = street;
            this.zipcode = zipcode;
            this.coord = coord;
        }

    }

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Grade {
        private Date date;
        private String grade;
        private Integer score;

        @Builder
        protected Grade(Date date, String grade, Integer score) {
            this.date = date;
            this.grade = grade;
            this.score = score;
        }
    }
}
