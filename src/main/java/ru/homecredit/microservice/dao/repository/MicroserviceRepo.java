package ru.homecredit.microservice.dao.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.homecredit.microservice.dao.entity.Microservice;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface MicroserviceRepo extends CrudRepository<Microservice, Long> {
    @Query("select m from Microservice m")
    Stream<Microservice> findAllToStream();

    @Query("select m from Microservice m where m.envDesc = ?1")
    List<Microservice> getByEnvDesc(String envDesc);
}
