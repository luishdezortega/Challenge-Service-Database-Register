package com.github.luishdezortega.challenge.dto;

import java.time.LocalDateTime;

public record CallLogDTO(LocalDateTime time, String route, String parameters, String result) {
}
