package ru.mrSergey.MyREST.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mrSergey.MyREST.models.Data;
import ru.mrSergey.MyREST.repositories.DataRepository;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DataService {

    private final DataRepository dataRepository;

    @Autowired
    public DataService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    public List<Data> findAll() {
        return dataRepository.findAll(Sort.by("value"));
    }

    public List<Data> sumRainingDay(boolean boo){
      return dataRepository.findByRaining(boo);
    }

    @Transactional
    public void save(Data data) {
        data.setTime(LocalDateTime.now());
        dataRepository.save(data);
    }
}
