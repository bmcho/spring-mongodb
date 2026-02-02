package com.bmcho.springbootmongodb.entity;

public record RestaurantMinMaxScore(String restaurantId, String name, int minScore, int maxScore) {
}
