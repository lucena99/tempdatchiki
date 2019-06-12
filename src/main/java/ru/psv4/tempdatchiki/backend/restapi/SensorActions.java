package ru.psv4.tempdatchiki.backend.restapi;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;
import ru.psv4.tempdatchiki.backend.service.ControllerService;
import ru.psv4.tempdatchiki.backend.service.NotFoundException;
import ru.psv4.tempdatchiki.backend.service.SensorService;
import ru.psv4.tempdatchiki.backend.restapi.dto.DtoUtils;
import ru.psv4.tempdatchiki.backend.restapi.dto.SensorCreateDto;
import ru.psv4.tempdatchiki.backend.restapi.dto.SensorDto;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.data.Sensor;
import ru.psv4.tempdatchiki.utils.UIDUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("restapi")
@Api(tags = {"Sensors Resource"}, description = "Действия с датчиками")
@SwaggerDefinition(tags = {
        @Tag(name = "Sensors Resource", description = "Действия с датчиками")
})
public class SensorActions {

    private static final Logger log = LogManager.getLogger(SensorActions.class);

    @Resource
    private SensorService sensorService;

    @Resource
    private ControllerService controllerService;

    @ApiOperation(value = "Получить всех датчики, отсортированных по номеру", response = Iterable.class)
    @RequestMapping(path = "/sensors", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody List<SensorDto> getSensorsAll() {
        return DtoUtils.convert(SensorDto.class, sensorService.getListOrderByControllerNameAndNum());
    }

    @ApiOperation(value = "Завести новый датчик", response = SensorDto.class)
    @RequestMapping(path = "/sensors", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody SensorDto createRecipient(@RequestBody SensorCreateDto dto) {
        String controllerUid = dto.getControllerUid();
        Controller c;
        try {
            c = controllerService.getByUid(controllerUid);
        } catch (NotFoundException e) {
            throw new ConflictRestException(String.format("Контроллер с uid %s не найден", controllerUid));
        }
        String name = dto.getName();
        List<Sensor> existedList = sensorService.getByNameIgnoreCase(name, c);
        if (existedList.size() > 0) {
            throw new ConflictRestException(String.format("Уже существует датчик " +
                    "с названием %s для контроллера %s", name, c.getName()));
        }
        try {
            Sensor s = new Sensor();
            s.setUid(UIDUtils.generate());
            s.setName(dto.getName());
            s.setCreatedDatetime(LocalDateTime.now());
            s.setNum(dto.getNum());
            s.setMinValue(dto.getMinValue());
            s.setMaxValue(dto.getMaxValue());
            s.setController(c);
            s = sensorService.save(s);
            return DtoUtils.convert(SensorDto.class, s);
        } catch (Exception e) {
            throw new SystemRestException(e.getMessage());
        }
    }

    @ApiOperation(value = "Получить датчик по uid", response = SensorDto.class)
    @RequestMapping(path = "/sensors/{uid}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody SensorDto info(@PathVariable("uid") String uid) {
        try {
            Sensor s = sensorService.getByUid(uid);
            return DtoUtils.convert(SensorDto.class, s);
        } catch (NotFoundException e) {
            throw new NotFoundRestException(String.format("Датчик не найден по uid=%s", uid));
        }
    }

    @ApiOperation(value = "Удалить датчик по uid")
    @RequestMapping(path = "/sensors/{uid}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("uid") String uid) {
        try {
            sensorService.deleteByUid(uid);
        } catch (NotFoundException e) {
            throw new NotFoundRestException(String.format("По uid=%s датчик не найден", uid));
        } catch (Exception e) {
            throw new SystemRestException(e.getMessage());
        }
    }
}
