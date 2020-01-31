package ru.homecredit.microservice.dao.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.homecredit.microservice.dao.entity.Microservice;

import java.util.List;

@Repository
public interface MicroserviceRepo extends CrudRepository<Microservice, Long> {
//todo запомнить контрукцию
//    @Query("select m from Microservice m where m.envDesc = ?1")
//    List<Microservice> getByEnvDesc(String envDesc);

    List<Microservice> findByEnvDesc(String envDesc);
}
