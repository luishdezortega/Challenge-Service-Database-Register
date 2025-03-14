package com.github.luishdezortega.challenge.integration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.luishdezortega.challenge.exception.PercentageUnavailableException;
import com.github.luishdezortega.challenge.integration.impl.PercentageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PercentageServiceTest {

    private PercentageService percentageService;

    @Mock
    private Cache<String, Double> cacheMock;

    @BeforeEach
    void setup() {
        percentageService = spy(new PercentageService(5, 100));
        ReflectionTestUtils.setField(percentageService, "cache", cacheMock);
    }

    @Nested
    class WhenCacheIsEmpty {

        @ParameterizedTest
        @ValueSource(doubles = {5.0, 10.0, 15.5})
        void shouldFetchFromServiceAndCacheResult(double mockPercentage) {
            when(cacheMock.getIfPresent(anyString())).thenReturn(null);
            doReturn(mockPercentage).when(percentageService).fetchPercentageFromExternalService();

            double result = percentageService.getPercentage();

            assertEquals(mockPercentage, result);
            verify(percentageService, times(1)).fetchPercentageFromExternalService();
            verify(cacheMock, times(1)).put(("percentage"), (mockPercentage));
        }

        @Test
        void shouldThrowExceptionWhenServiceFails() {
            when(cacheMock.getIfPresent("percentage")).thenReturn(null);
            doThrow(new PercentageUnavailableException("Service error")).when(percentageService).fetchPercentageFromExternalService();

            assertThrows(PercentageUnavailableException.class, () -> percentageService.getPercentage());
        }
    }

    @Nested
    class WhenCacheIsAvailable {

        @ParameterizedTest
        @ValueSource(doubles = {8.0, 12.5, 20.0})
        void shouldReturnCachedPercentage(double cachedPercentage) {
            when(cacheMock.getIfPresent("percentage")).thenReturn(cachedPercentage);
            double result = percentageService.getPercentage();
            assertEquals(cachedPercentage, result);
            verify(percentageService, never()).fetchPercentageFromExternalService();
        }

    }

}
