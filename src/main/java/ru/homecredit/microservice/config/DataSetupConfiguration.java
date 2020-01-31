package ru.homecredit.microservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.homecredit.microservice.dao.entity.Microservice;
import ru.homecredit.microservice.dao.repository.MicroserviceRepo;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

@Configuration
public class DataSetupConfiguration {
    @Value("${dashboard.xml_export_url}")
    private String xmlExportUrl;
    @Value("${dashboard.xml_saved_file}")
    private String xmlSavedFile;

    // todo
    //  1. e_desc брать в форматированном виде, так как хтмл ругается на пробелы и скобки!
    //  2. RestTemplate мудит при коннекте к ранчеру, поэтому шедулер и загрузка xml отключены
    @Bean
    public ApplicationRunner init(MicroserviceRepo microserviceRepo) {
        return (ApplicationArguments args) -> dataSetup(microserviceRepo);
    }

    void dataSetup(MicroserviceRepo microserviceRepo) {
        NodeList anonElements;
        DocumentBuilderFactory builderFactory;
        DocumentBuilder builder;
        Document document;
        try {
            builderFactory = DocumentBuilderFactory.newInstance();
            builder = builderFactory.newDocumentBuilder();

//            byte[] buffer = restTemplate().getForObject(xmlExportUrl, byte[].class);
//            Files.write(Paths.get(xmlSavedFile), Objects.requireNonNull(buffer));
//            document = builder.parse(new File(xmlSavedFile));

            document = builder.parse(new File("src/main/resources/services_from_dashboard.xml"));
            anonElements = document.getDocumentElement().getElementsByTagName("anon");

            for (int i = 0; i < anonElements.getLength(); i++) {
                Node anon = anonElements.item(i);
                NamedNodeMap attributes = anon.getAttributes();
                microserviceRepo.save(new Microservice(
                        attributes.getNamedItem("e_desc").getNodeValue().replaceAll("\\s+", "+").replaceAll("[()<\\[\\]>]", ""),
                        attributes.getNamedItem("e_name").getNodeValue(),
                        attributes.getNamedItem("se_name").getNodeValue(),
                        attributes.getNamedItem("se_id").getNodeValue(),
                        attributes.getNamedItem("health").getNodeValue(),
                        attributes.getNamedItem("delta_m").getNodeValue(),
                        attributes.getNamedItem("scale").getNodeValue(),
                        attributes.getNamedItem("secrets").getNodeValue(),
                        attributes.getNamedItem("started").getNodeValue(),
                        attributes.getNamedItem("system").getNodeValue(),
                        attributes.getNamedItem("type").getNodeValue(),
                        attributes.getNamedItem("volumes").getNodeValue()
                ));
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }
}
