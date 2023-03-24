package ru.mrSergey.MyREST.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mrSergey.MyREST.models.Sensor;
import ru.mrSergey.MyREST.repositories.SensorRepository;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class SensorService {

    private final SensorRepository sensorRepository;

    @Autowired
    public SensorService(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }


    public List<Sensor> findAll() {
        return sensorRepository.findAll(Sort.by("name"));
    }

    public Sensor findOne(int id) {
        Optional<Sensor> foundPerson = sensorRepository.findById(id);
        return foundPerson.orElse(null);
    }

    @Transactional
    public void save(Sensor sensor) {
//        enrichPerson(sensor); //Добавляем перед сохранением
        sensorRepository.save(sensor);
    }

    //Метод добавления данных для Person в базу данных
//    private void enrichPerson(Person person) {
//        person.setCratedAt((LocalDateTime.now()));
//        person.setUpdatedAt((LocalDateTime.now()));
//        person.setCreatedWho("ADMIN");
//    }
}
