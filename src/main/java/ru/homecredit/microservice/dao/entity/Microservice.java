package ru.homecredit.microservice.dao.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@ToString
@Getter
@JsonIgnoreType
@NoArgsConstructor
public class Microservice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String envDesc;
    private String envName;
    private String serviceName;
    private String serviceId;
    private String health;
    private String deltaM;
    private String scale;
    private String secrets;
    private String started;
    private String system;
    private String type;
    private String volumes;

    public Microservice(String envDesc, String envName, String serviceName, String serviceId, String health,
                        String deltaM, String scale, String secrets, String started, String system, String type, String volumes) {
        this.envDesc = envDesc;
        this.envName = envName;
        this.serviceName = serviceName;
        this.serviceId = serviceId;
        this.health = health;
        this.deltaM = deltaM;
        this.scale = scale;
        this.secrets = secrets;
        this.started = started;
        this.system = system;
        this.type = type;
        this.volumes = volumes;
    }
}