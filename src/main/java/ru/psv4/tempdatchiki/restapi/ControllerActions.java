package ru.psv4.tempdatchiki.restapi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.psv4.tempdatchiki.beans.ControllerService;
import ru.psv4.tempdatchiki.dto.ControllerDto;
import ru.psv4.tempdatchiki.dto.DtoUtils;
import ru.psv4.tempdatchiki.model.Controller;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("restapi")
public class ControllerActions {

    private static final Logger log = LogManager.getLogger(ControllerActions.class);

    @Resource
    private ControllerService controllerService;

    @RequestMapping(path = "/controllers", method = RequestMethod.GET)
    public @ResponseBody List<ControllerDto> getControllersAll() {
        return DtoUtils.convert(Controller.class, ControllerDto.class, controllerService.getList());
    }
}
