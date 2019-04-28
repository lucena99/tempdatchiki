package ru.psv4.tempdatchiki.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.psv4.tempdatchiki.beans.ControllerService;
import ru.psv4.tempdatchiki.beans.SubscribeService;
import ru.psv4.tempdatchiki.beans.RecipientService;
import ru.psv4.tempdatchiki.beans.SensorService;

import javax.annotation.Resource;
import java.util.Map;

@Controller
public class IndexPageController {

    @Resource
    private RecipientService recipientService;

    @Resource
    private SensorService sensorService;

    @Resource
    private ControllerService controllerService;

    @Resource
    private SubscribeService subscribeService;

    @RequestMapping("/")
    public String index(Map<String, Object> model) {
        model.put("sensors", sensorService.getList());
        model.put("recipients", recipientService.getList());
        model.put("controllers", controllerService.getList());
        //model.put("subscribers", subscribeService.getList());
        return "index";
    }
}
