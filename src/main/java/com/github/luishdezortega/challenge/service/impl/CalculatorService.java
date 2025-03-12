package com.github.luishdezortega.challenge.service.impl;

import com.github.luishdezortega.challenge.integration.IPercentageService;
import com.github.luishdezortega.challenge.service.ICalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalculatorService implements ICalculatorService {

    private final IPercentageService percentageService;


    @Override
    public double sumWithPercentage(double numberOne, double numberTwo) {
        double sum = numberOne + numberTwo;
        double percentage = percentageService.getPercentage();
        return sum + (sum * percentage / 100);
    }

}
