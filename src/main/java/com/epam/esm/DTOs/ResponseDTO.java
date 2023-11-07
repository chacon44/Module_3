package com.epam.esm.DTOs;

public record ResponseDTO (long index, String name, String description, long price, long duration, String createUpdate, String LastUpdateDate) {
}
