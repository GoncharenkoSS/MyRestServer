package ru.mrSergey.MyREST.dto;

import jakarta.validation.constraints.Size;

public class SensorDTO {

    @Size(min = 2, max = 30, message = "Имя должно быть от 2 до 30 символов")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}


