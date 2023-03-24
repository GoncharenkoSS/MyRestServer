package ru.mrSergey.MyREST.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mrSergey.MyREST.models.Data;

public interface DataRepository extends JpaRepository<Data, Integer> {

}
