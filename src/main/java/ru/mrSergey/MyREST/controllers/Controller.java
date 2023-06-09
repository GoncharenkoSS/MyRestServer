package ru.mrSergey.MyREST.controllers;


import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.mrSergey.MyREST.dto.DataDTO;
import ru.mrSergey.MyREST.dto.SensorDTO;
import ru.mrSergey.MyREST.models.Data;
import ru.mrSergey.MyREST.models.Sensor;
import ru.mrSergey.MyREST.repositories.SensorRepository;
import ru.mrSergey.MyREST.services.DataService;
import ru.mrSergey.MyREST.services.SensorService;
import ru.mrSergey.MyREST.util.ErrorResponse;
import ru.mrSergey.MyREST.util.NotCreatedException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class Controller {

    private final DataService dataService;
    private final SensorService sensorService;
    private final ModelMapper modelMapper;
    private final SensorRepository sensorRepository;

    @Autowired
    public Controller(DataService dataService, SensorService sensorService, ModelMapper modelMapper, SensorRepository sensorRepository) {
        this.dataService = dataService;
        this.sensorService = sensorService;
        this.modelMapper = modelMapper;
        this.sensorRepository = sensorRepository;
    }

    //Получаем список сенсоров из БД
    @GetMapping("/sensors")
    public List<SensorDTO> getSensor() {
        return sensorService.findAll().stream().map(this::convertToSensorDTO).collect(Collectors.toList());
    }

    //Метод регистрации сенсора
    @PostMapping("/sensors/registrations")
    public ResponseEntity<HttpStatus> createSensor(
            @RequestBody @Valid SensorDTO sensorDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) answer(bindingResult);
        if (sensorDTO.getName().equals(sensorService.findByName(convertToSensor(sensorDTO).getName()).getName()))
            throw new NotCreatedException("Такой сенсор уже существует в базе данных");
        sensorService.save(convertToSensor(sensorDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    //Получаем список данных с сенсора
    @GetMapping("/measurements")
    public List<DataDTO> getData() {
        return dataService.findAll().stream().map(this::convertToDataDTO).collect(Collectors.toList());
    }

    //Получаем количество дождливых дней
    @GetMapping("/measurements/rainyDaysCount")
    public String sumRainingDay() {
        return "Количество дождливых дней: " + dataService.sumRainingDay(true).size();
    }

    //Добавляем в БД данные из JSON
    @PostMapping("/measurements/add")
    public ResponseEntity<HttpStatus> addDate(
            //ObjectNode позволяет вычитывать JSON по ключу(!!!но не работает валидация!!!)
            @RequestBody ObjectNode json, DataDTO dataDTO, BindingResult bindingResult) {
        //Собственная валидация
        if (json.get("value").asInt() > 50 || json.get("value").asInt() < -50 || json.get("value") == null)
            throw new NotCreatedException("Температура должна быть в значениях от -50 до 50");
        else
            dataDTO.setValue(json.get("value").asInt());
        //Собственная валидация
        if (json.get("raining") == null)
            throw new NotCreatedException("Поле не может быть пустым");
        else
            dataDTO.setRaining(json.get("raining").asBoolean());
        //Метод для построения ошибок из bindingResult
        if (bindingResult.hasErrors()) answer(bindingResult);
        //Проверка, есть ли в БД данный сенсор
        if (sensorRepository.findByName(json.at("/sensor/owner").asText()) == null)
            throw new NotCreatedException("Такого сенсора нет в базе данных");
        //Присвоение данных
        dataDTO.setOwner(sensorService.findByName(json.at("/sensor/owner").asText()));
        //Сохранение
        dataService.save(convertToData(dataDTO));
        //Ответ клиенту
        return ResponseEntity.ok(HttpStatus.OK);
    }

    //Методы конвертации ИЗ/В ДТО
    private Sensor convertToSensor(SensorDTO sensorDTO) {
        return modelMapper.map(sensorDTO, Sensor.class);
    }

    private SensorDTO convertToSensorDTO(Sensor sensor) {
        return modelMapper.map(sensor, SensorDTO.class);
    }

    private Data convertToData(DataDTO dataDTO) {
        return modelMapper.map(dataDTO, Data.class);
    }

    private DataDTO convertToDataDTO(Data data) {
        return modelMapper.map(data, DataDTO.class);
    }

    // Отображение ошибки клиенту
    @ExceptionHandler//Аннотация для своих исключений(!!!!!!!!!!!!если была нарушена валидация!!!!!!!!)
    private ResponseEntity<ErrorResponse> handleException(NotCreatedException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage(), System.currentTimeMillis());

        //В HTTP ответе тело ответа(response) и статус в заголовке
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //Метод для построения ошибок из bindingResult
    public void answer(BindingResult bindingResult) {
        StringBuilder errorMsg = new StringBuilder();
        List<FieldError> errors = bindingResult.getFieldErrors();

        for (FieldError error : errors) {
            errorMsg.append(error.getField())
                    .append(" - ")
                    .append(error.getDefaultMessage())
                    .append(";");
        }
        throw new NotCreatedException(errorMsg.toString());
    }
}