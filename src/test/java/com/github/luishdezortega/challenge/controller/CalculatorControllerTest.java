package com.github.luishdezortega.challenge.controller;

import com.github.luishdezortega.challenge.service.ICalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class CalculatorControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ICalculatorService calculatorService;

    @InjectMocks
    private CalculatorController calculatorController;

    @BeforeEach
    void setup() {
        mockMvc = standaloneSetup(calculatorController).build();
    }

    @ParameterizedTest
    @MethodSource("provideValidRequests")
    void shouldReturnCalculatedPercentage(double numberOne, double numberTwo, double expectedResult) throws Exception {
        when(calculatorService.sumWithPercentage(numberOne, numberTwo)).thenReturn(expectedResult);

        String requestJson = """
                {
                  "numberOne": %s,
                  "numberTwo": %s
                }
                """.formatted(numberOne, numberTwo);

        mockMvc.perform(post("/api/percentage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "result": %s
                        }
                        """.formatted(expectedResult)));
    }

    private static Stream<Arguments> provideValidRequests() {
        return Stream.of(
                Arguments.of(3, 4, 7.2),
                Arguments.of(10, 20, 33.0),
                Arguments.of(100, 200, 330.0),
                Arguments.of(-5, -10, -16.5)
        );
    }
}


