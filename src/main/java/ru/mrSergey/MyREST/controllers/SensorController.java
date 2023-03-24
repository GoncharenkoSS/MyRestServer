package ru.mrSergey.MyREST.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.mrSergey.MyREST.models.Sensor;
import ru.mrSergey.MyREST.services.SensorService;
import ru.mrSergey.MyREST.util.ErrorResponse;
import ru.mrSergey.MyREST.util.NotCreatedException;
import ru.mrSergey.MyREST.util.NotFoundException;

import java.util.List;

@RestController
@RequestMapping("/people")
public class SensorController {

    private final SensorService sensorService;

    @Autowired
    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @GetMapping()
    public List<Sensor> getSensor() {
        //Автоматически Jackson конвертирует эти объекты в JSON из класса !!ДТО!!
        return sensorService.findAll();//.stream().map(this::convertToPersonDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public Sensor getSensor(@PathVariable("id") int id) {
        return sensorService.findOne(id);
    }

    @PostMapping
    public ResponseEntity<HttpStatus> create(
            @RequestBody @Valid Sensor sensor, /*С этой аннотацией Джексон сконвертирует JSON в объект Sensor*/
            BindingResult bindingResult) { /*Ловим ошибку валидации*/
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();/* Построение красивого поля с ошибкой валидации*/
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMsg.append(error.getField())/*На каком поле была совершена ошибка*/
                        .append(" - ")
                        .append(error.getDefaultMessage())/*Какая ошибка была на данном поле*/
                        .append(";");
            }
            throw new NotCreatedException(errorMsg.toString());
        }
        sensorService.save(sensor);

        //отправляем HTTP ответ с пустым телом и со статусом 200
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler//Аннотация для своих исключений(!!!!!!!!!!если ID человека не найдено!!!!!!!!!!!!!)
    private ResponseEntity<ErrorResponse> handleException(NotFoundException e) {
        ErrorResponse response = new ErrorResponse(
                "Человек с данным ID не найден", System.currentTimeMillis());
        //В HTTP ответе тело ответа(response) и статус в заголовке
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler//Аннотация для своих исключений(!!!!!!!!!!!!если была нарушена валидация!!!!!!!!)
    private ResponseEntity<ErrorResponse> handleException(NotCreatedException e) {
        ErrorResponse response = new ErrorResponse(
                e.getMessage(), System.currentTimeMillis());
        //В HTTP ответе тело ответа(response) и статус в заголовке
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

/*
    //Метод конвертирования из PersonDTO in Person
    private Person convertToPerson(PersonDTO personDTO) {
        ModelMapper modelMapper = new ModelMapper();
        //Зависимость modelmapper автоматически конвертирует из PersonDTO in Person как показано ниже.
        //person.setName(personDTO.getName());
        return modelMapper.map(personDTO, Person.class);
    }

    private PersonDTO convertToPersonDTO(Person person) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(person, PersonDTO.class);
    }

 */

}