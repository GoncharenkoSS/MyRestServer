package ru.mrSergey.MyREST.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mrSergey.MyREST.models.Data;
import java.util.List;

public interface DataRepository extends JpaRepository<Data, Integer> {
    List<Data> findByRaining(boolean boo);
}
