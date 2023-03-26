package ru.mrSergey.MyREST.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
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

    @GetMapping("/sensors")
    public List<SensorDTO> getSensor() {
        return sensorService.findAll().stream().map(this::convertToSensorDTO).collect(Collectors.toList());
    }

    @GetMapping("/sensors/{id}")
    public Sensor getSensor(@PathVariable("id") int id) {
        return sensorService.findOne(id);
    }

    @PostMapping("/sensors/registrations")
    public ResponseEntity<HttpStatus> createSensor(
            @RequestBody @Valid SensorDTO sensorDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) answer(bindingResult);
        if (sensorDTO.getName().equals(sensorService.findByName(convertToSensor(sensorDTO).getName()).getName()))
            throw new NotCreatedException("Такой сенсор уже существует в базе данных");
        sensorService.save(convertToSensor(sensorDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/measurements")
    public List<DataDTO> getData() {
        return dataService.findAll().stream().map(this::convertToDataDTO).collect(Collectors.toList());
    }


    @GetMapping("/measurements/rainyDaysCount")
    public String sumRainingDay() {
        return "Количество дождливых дней: " + dataService.sumRainingDay(true).size();
    }

    @PostMapping("/measurements/add")
    public ResponseEntity<HttpStatus> addDate(
            @RequestBody ObjectNode json, DataDTO dataDTO, BindingResult bindingResult) {

        if (json.get("value").asInt() > 50 || json.get("value").asInt() < -50 || json.get("value") == null)
            throw new NotCreatedException("Температура должна быть в значениях от -50 до 50");
        else
            dataDTO.setValue(json.get("value").asInt());
        if (json.get("raining") == null)
            throw new NotCreatedException("Поле не может быть пустым");
        else
            dataDTO.setRaining(json.get("raining").asBoolean());

        if (bindingResult.hasErrors()) answer(bindingResult);

        if (sensorRepository.findByName(json.at("/sensor/owner").asText()) == null)
            throw new NotCreatedException("Такого сенсора нет в базе данных");

        dataDTO.setOwner(sensorService.findByName(json.at("/sensor/owner").asText()));
        dataService.save(convertToData(dataDTO));

        return ResponseEntity.ok(HttpStatus.OK);

    }

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

    @ExceptionHandler//Аннотация для своих исключений(!!!!!!!!!!!!если была нарушена валидация!!!!!!!!)
    private ResponseEntity<ErrorResponse> handleException(NotCreatedException e) {
        ErrorResponse response = new ErrorResponse(
                e.getMessage(), System.currentTimeMillis());
        //В HTTP ответе тело ответа(response) и статус в заголовке
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    public void answer(BindingResult bindingResult) {
        StringBuilder errorMsg = new StringBuilder();
        List<FieldError> errors = bindingResult.getFieldErrors();
        for (FieldError error : errors) {
            errorMsg.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append(";");
        }
        throw new NotCreatedException(errorMsg.toString());
    }
}