package com.github.luishdezortega.challenge.integration.impl;

import com.github.luishdezortega.challenge.integration.IPercentageService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class PercentageService implements IPercentageService {

    static final double PERCENTAGE = 10.0;

    @Override
    @Cacheable("percentage")
    public double getPercentage() {
        return fetchPercentageFromExternalService();
    }

    private double fetchPercentageFromExternalService() {
        return PERCENTAGE;
    }

}
