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
import ru.psv4.tempdatchiki.dto.ControllerCreateDto;
import ru.psv4.tempdatchiki.dto.ControllerDto;
import ru.psv4.tempdatchiki.dto.DtoUtils;
import ru.psv4.tempdatchiki.model.Controller;
import ru.psv4.tempdatchiki.utils.UIDUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("restapi")
@Api(tags = {"Controllers Resource"}, description = "Действия с контроллерами")
@SwaggerDefinition(tags = {
        @Tag(name = "Controllers Resource", description = "Действия с контроллерами")
})
public class ControllerActions {

    private static final Logger log = LogManager.getLogger(ControllerActions.class);

    @Resource
    private ControllerService controllerService;

    @ApiOperation(value = "Получить всех контроллеры, отсортированных по дате создания", response = Iterable.class)
    @RequestMapping(path = "/controllers", method = RequestMethod.GET)
    public @ResponseBody List<ControllerDto> getControllersAll() {
        return DtoUtils.convert(ControllerDto.class, controllerService.getList());
    }

    @ApiOperation(value = "Создать новый контроллер", response = ControllerDto.class)
    @RequestMapping(path = "/controllers", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody ControllerDto createRecipient(@RequestBody ControllerCreateDto dto) {
        String name = dto.getName();
        List<Controller> existedList = controllerService.getByNameIgnoreCase(name);
        if (existedList.size() > 0) {
            throw new ConflictRestException(String.format("Уже существует контроллер с названием %s", name));
        }
        try {
            Controller c = new Controller();
            c.setUid(UIDUtils.generate());
            c.setName(dto.getName());
            c.setCreatedDatetime(LocalDateTime.now());
            c = controllerService.save(c);
            return DtoUtils.convert(ControllerDto.class, c);
        } catch (Exception e) {
            throw new SystemRestException(e.getMessage());
        }
    }

    @ApiOperation(value = "Получить контроллер по uid", response = ControllerDto.class)
    @RequestMapping(path = "/controllers/{uid}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody ControllerDto info(@PathVariable("uid") String uid) {
        try {
            Controller c = controllerService.getByUid(uid);
            return DtoUtils.convert(ControllerDto.class, c);
        } catch (NotFoundException e) {
            throw new NotFoundRestException(String.format("Получатель не найден по uid=%s", uid));
        }
    }

    @ApiOperation(value = "Удалить контроллер по uid")
    @RequestMapping(path = "/controllers/{uid}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("uid") String uid) {
        try {
            controllerService.deleteByUid(uid);
        } catch (NotFoundException e) {
            throw new NotFoundRestException(String.format("По uid=%s контроллер не найден", uid));
        } catch (Exception e) {
            throw new SystemRestException(e.getMessage());
        }
    }
}
