package ru.mrSergey.MyREST.dto;

import ru.mrSergey.MyREST.models.Sensor;

import java.time.LocalDateTime;
import java.util.Date;

public class DataDTO {

    private int value;

    private boolean raining;

    private Sensor owner;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isRaining() {
        return raining;
    }

    public void setRaining(boolean raining) {
        this.raining = raining;
    }

    public Sensor getOwner() {
        return owner;
    }

    public void setOwner(Sensor owner) {
        this.owner = owner;
    }
}
