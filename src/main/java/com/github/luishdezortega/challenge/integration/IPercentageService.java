package com.github.luishdezortega.challenge.integration;

public interface IPercentageService {

    double PERCENTAGE = 0.19;

    default double getPercentage() {
        return PERCENTAGE;
    }

}