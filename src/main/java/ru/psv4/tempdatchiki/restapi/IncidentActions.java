package ru.psv4.tempdatchiki.restapi;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.psv4.tempdatchiki.backend.data.Sensor;
import ru.psv4.tempdatchiki.backend.service.IncidentService;
import ru.psv4.tempdatchiki.backend.service.SensorService;
import ru.psv4.tempdatchiki.dto.DtoUtils;
import ru.psv4.tempdatchiki.dto.IncidentDto;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("restapi")
@Api(tags = {"Incidents Resource"}, description = "Действия с инцидентами")
@SwaggerDefinition(tags = {
        @Tag(name = "Incidents Resource", description = "Действия с инцидентами")
})
public class IncidentActions {

    private static final Logger log = LogManager.getLogger(IncidentActions.class);

    @Resource
    private IncidentService incidentService;

    @Resource
    private SensorService sensorService;

    @ApiOperation(value = "Получить всех инциденты, отсортированных по дате создания", response = Iterable.class)
    @RequestMapping(path = "/incidents", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody List<IncidentDto> getIncidentsAll(String sensorUid) {
        Optional<Sensor> opSensor = sensorService.getRepository().findById(sensorUid);
        if (!opSensor.isPresent()) {
            throw new ConflictRestException(String.format("Датчик с uid %s не найден", sensorUid));
        }
        return DtoUtils.convert(IncidentDto.class, incidentService.getRepository()
                .getListBySensorOrderByCreatedDatetime(opSensor.get()));
    }
}
