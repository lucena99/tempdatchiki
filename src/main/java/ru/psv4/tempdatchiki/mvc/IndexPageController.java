package ru.psv4.tempdatchiki.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.psv4.tempdatchiki.beans.SensorService;

import javax.annotation.Resource;
import java.util.Map;

@Controller
public class IndexPageController {

    @Resource
    private SensorService sensorService;

    @RequestMapping("/")
    public String index(Map<String, Object> model) {
        model.put("sensors", sensorService.getList());
        return "index";
    }
}
