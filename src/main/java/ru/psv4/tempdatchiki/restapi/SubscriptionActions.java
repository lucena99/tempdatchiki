package ru.psv4.tempdatchiki.restapi;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;
import ru.psv4.tempdatchiki.beans.ControllerService;
import ru.psv4.tempdatchiki.beans.NotFoundException;
import ru.psv4.tempdatchiki.beans.RecipientService;
import ru.psv4.tempdatchiki.beans.SensorService;
import ru.psv4.tempdatchiki.dto.DtoUtils;
import ru.psv4.tempdatchiki.dto.SensorCreateDto;
import ru.psv4.tempdatchiki.dto.SensorDto;
import ru.psv4.tempdatchiki.dto.SubscriptionDto;
import ru.psv4.tempdatchiki.model.Controller;
import ru.psv4.tempdatchiki.model.Recipient;
import ru.psv4.tempdatchiki.model.Sensor;
import ru.psv4.tempdatchiki.utils.UIDUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("restapi")
@Api(tags = {"Subscription Resource"}, description = "Действия с подписками")
@SwaggerDefinition(tags = {
        @Tag(name = "Subscription Resource", description = "Действия с подписками")
})
public class SubscriptionActions {

    private static final Logger log = LogManager.getLogger(SubscriptionActions.class);

    @Resource
    private RecipientService recipientService;
    @Resource
    private ControllerService controllerService;

    @ApiOperation(value = "Подписать слушателя к контроллеру", response = SensorDto.class)
    @RequestMapping(path = "/sensors", method = RequestMethod.POST, produces = "application/json")
    public void createSubscription(@RequestBody SubscriptionDto dto) {
        String controllerUid = dto.getControllerUid();
        Controller c;
        try {
            c = controllerService.getByUid(controllerUid);
        } catch (NotFoundException e) {
            throw new ConflictRestException(String.format("Контроллер с uid %s не найден", controllerUid));
        }
        String recipientUid = dto.getRecipientUid();
        Recipient r;
        try {
            r = recipientService.getByUid(recipientUid);
        } catch (NotFoundException e) {
            throw new ConflictRestException(String.format("Слушатель с uid %s не найден", recipientUid));
        }
        List<Sub> existedList = sensorService.getByNameIgnoreCase(name, c);
        if (existedList.size() > 0) {
            throw new ConflictRestException(String.format("Уже существует датчик " +
                    "с названием %s для контроллера %s", name, c.getName()));
        }
        try {
            Sensor s = new Sensor();
            s.setUid(UIDUtils.generate());
            s.setName(dto.getName());
            s.setCreatedDatetime(LocalDateTime.now());
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
