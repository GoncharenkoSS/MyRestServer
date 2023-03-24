package ru.mrSergey.MyREST.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.mrSergey.MyREST.models.Sensor;


@Repository
public interface SensorRepository extends JpaRepository<Sensor, Integer> {

}
