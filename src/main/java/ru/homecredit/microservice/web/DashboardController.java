package ru.homecredit.microservice.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.homecredit.microservice.dao.entity.Microservice;
import ru.homecredit.microservice.dao.service.IMicroserviceDaoService;
import ru.homecredit.microservice.schedulers.AutomaticCheckHealth;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard/v1")
@RequiredArgsConstructor
public class DashboardController {
    private final IMicroserviceDaoService microserviceDaoService;
    private final AutomaticCheckHealth automaticCheckHealth;

    @RequestMapping(value = "/envs", method = {RequestMethod.GET, RequestMethod.POST})
    public String getEnvList(@RequestParam(required = false) String envFilter, Model model) {
        List<String> list;
        if (envFilter != null && !envFilter.isEmpty()) {
            list = microserviceDaoService.getDistinctEnvDescs().stream()
                    // change all spaces to '+', for searching
                    .filter(m -> m.toLowerCase().contains(envFilter.toLowerCase().replaceAll("\\s+", "+")))
                    .collect(Collectors.toList());
        } else {
            list = microserviceDaoService.getDistinctEnvDescs();
        }

        model.addAttribute("envDescList", list);
        return "environment_list";
    }

    @RequestMapping(value = "/envs/{env}", method = {RequestMethod.GET, RequestMethod.POST})
    public String getServiceListByEnv(@PathVariable("env") String envDesc,
                                      @RequestParam(required = false) String serviceFilter,
                                      Model model) {
        List<Microservice> serviceNames;
        if (serviceFilter != null && !serviceFilter.isEmpty()) {
            serviceNames = microserviceDaoService.getServicesByEnvDesc(envDesc).stream()
                    .filter(m -> m.getServiceName().toLowerCase().contains(serviceFilter.toLowerCase().replaceAll("\\s+", "+")))
                    .collect(Collectors.toList());
        } else {
            serviceNames = microserviceDaoService.getServicesByEnvDesc(envDesc);
        }

        model.addAttribute("env", envDesc);
        model.addAttribute("serviceList", serviceNames);
        return "service_list";
    }

    @RequestMapping(value = "/{env}/{service}", method = RequestMethod.GET)
    public String getPossibleActuatorMethods(@PathVariable("env") String envDesc,
                                             @PathVariable String service,
                                             Model model) {
        String envName = microserviceDaoService.getServicesByEnvDesc(envDesc).stream()
                .filter(microservice -> microservice.getServiceName().equalsIgnoreCase(service))
                .collect(Collectors.toList()).get(0).getEnvName();

        model.addAttribute("envName", envName);
        model.addAttribute("service", service);
        return "in_service";
    }

    @RequestMapping(value = "/{env}/{service}/{method}", method = RequestMethod.GET)
    @ResponseBody
    public String getRequestFromService(@PathVariable("env") String envName,
                                        @PathVariable String service,
                                        @PathVariable String method) {
        return automaticCheckHealth.getRequestFromCache(envName, service, method);
    }

//    "/envs?env=fp&service=transfer"
//    @RequestMapping(value = "/envs2", method = {RequestMethod.GET, RequestMethod.POST})
//    public String what(@RequestParam(required = false) String envFilter,
//                       @RequestParam(required = false) String envDesc,
//                       @RequestParam(required = false) String serviceFilter,
//                       @RequestParam(required = false) String service,
//                       Model model) {
//        List<String> list;
//        if (envFilter != null && !envFilter.isEmpty()) {
//            list = microserviceDaoService.getDistinctEnvDescs().stream()
//                    // в слове из поиска заменяем пробелы на +, шобы искалось хорошо
//                    .filter(m -> m.toLowerCase().contains(envFilter.toLowerCase().replaceAll("\\s+", "+")))
//                    .collect(Collectors.toList());
//        } else {
//            list = microserviceDaoService.getDistinctEnvDescs();
//        }
//
//        model.addAttribute("envDescList", list);
//
//        if (envDesc == null)
//            return "environment_list2";
//
//        List<Microservice> serviceNames;
//        if (serviceFilter != null && !serviceFilter.isEmpty()) {
//            serviceNames = microserviceDaoService.getServicesByEnvDesc(envDesc).stream()
//                    .filter(m -> m.getServiceName().toLowerCase().contains(serviceFilter.toLowerCase().replaceAll("\\s+", "+")))
//                    .collect(Collectors.toList());
//        } else {
//            serviceNames = microserviceDaoService.getServicesByEnvDesc(envDesc);
//        }
//
//        model.addAttribute("env", envDesc);
//        model.addAttribute("serviceList", serviceNames);
//
//        if (service == null)
//            return "service_list2";
//
//        String envName = microserviceDaoService.getServicesByEnvDesc(envDesc).stream()
//                .filter(microservice -> microservice.getServiceName().equalsIgnoreCase(service))
//                .collect(Collectors.toList()).get(0).getEnvName();
//
//        model.addAttribute("envName", envName);
//        model.addAttribute("service", service);
//        return "in_service2";
//    }

//    @GetMapping("/x")
//    @ResponseBody
//    public void whaat1() {
//        automaticCheckHealth.refreshCache();
//    }
}
