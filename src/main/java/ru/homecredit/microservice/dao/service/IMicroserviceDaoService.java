package ru.homecredit.microservice.dao.service;

import ru.homecredit.microservice.dao.entity.Microservice;

import java.util.List;
import java.util.stream.Stream;

public interface IMicroserviceDaoService {
    Stream<Microservice> getAllMicroservices();

    List<String> getDistinctEnvDescs();

    List<Microservice> getServicesByEnvDesc(String env);
}
