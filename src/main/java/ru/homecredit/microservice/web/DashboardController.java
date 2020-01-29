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

    @RequestMapping(value = "/envs", method = RequestMethod.GET)
    public String getEnvList(Model model) {
        List<String> distinctDescs = microserviceDaoService.getDistinctEnvDescs();

        model.addAttribute("envDescList", distinctDescs);
        return "environment_list";
    }

    @RequestMapping(value = "/envs", method = RequestMethod.POST)
    public String getEnvList(@RequestParam String filter, Model model) {
        List<String> list;
        if (filter != null && !filter.isEmpty()) {
            list = microserviceDaoService.getDistinctEnvDescs().stream()
                    // в слове из поиска заменяем пробелы на +, шобы искалось хорошо
                    .filter(m -> m.toLowerCase().contains(filter.toLowerCase().replaceAll("\\s+", "+")))
                    .collect(Collectors.toList());
        } else {
            list = microserviceDaoService.getDistinctEnvDescs();
        }

        model.addAttribute("envDescList", list);
        return "environment_list";
    }

    @RequestMapping(value = "/envs/{env}", method = RequestMethod.GET)
    public String getServiceListByEnv(@PathVariable String env, Model model) {
        List<Microservice> serviceNames = microserviceDaoService.getServicesByEnvDesc(env);

        model.addAttribute("env", env);
        model.addAttribute("serviceList", serviceNames);
        return "service_list";
    }

    @RequestMapping(value = "/envs/{env}", method = RequestMethod.POST)
    public String postServiceListByEnv(@PathVariable String env, String filter, Model model) {
        List<Microservice> serviceNames;
        if (filter != null && !filter.isEmpty()) {
            serviceNames = microserviceDaoService.getServicesByEnvDesc(env).stream()
                    .filter(m -> m.getServiceName().toLowerCase().contains(filter.toLowerCase().replaceAll("\\s+", "+")))
                    .collect(Collectors.toList());
        } else {
            serviceNames = microserviceDaoService.getServicesByEnvDesc(env);
        }

        model.addAttribute("env", env);
        model.addAttribute("serviceList", serviceNames);
        return "service_list";
    }

    @RequestMapping(value = "/{env}/{service}", method = RequestMethod.GET)
    public String getSmth1(@PathVariable String env,
                           @PathVariable String service,
                           Model model) {
        String envName = microserviceDaoService.getServicesByEnvDesc(env).stream()
                .filter(microservice -> microservice.getServiceName().equalsIgnoreCase(service))
                .collect(Collectors.toList()).get(0).getEnvName();

        model.addAttribute("envName", envName);
        model.addAttribute("service", service);
        return "in_service";
    }

    @RequestMapping(value = "/{envName}/{service}/{method}", method = RequestMethod.GET)
    @ResponseBody
    public String getActuatorsMethods(@PathVariable String envName,
                                       @PathVariable String service,
                                       @PathVariable String method) {
        return automaticCheckHealth.getRequestFromCache(envName, service, method);
    }
//
//    @GetMapping("/x")
//    @ResponseBody
//    public void whaat1() {
//        automaticCheckHealth.refreshCache();
//    }
}
