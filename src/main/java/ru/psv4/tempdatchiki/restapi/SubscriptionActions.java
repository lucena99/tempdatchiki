package ru.psv4.tempdatchiki.restapi;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;
import ru.psv4.tempdatchiki.backend.service.ControllerService;
import ru.psv4.tempdatchiki.backend.service.NotFoundException;
import ru.psv4.tempdatchiki.backend.service.RecipientService;
import ru.psv4.tempdatchiki.backend.service.SubscribtionService;
import ru.psv4.tempdatchiki.dto.DtoUtils;
import ru.psv4.tempdatchiki.dto.SubscriptionCreateDto;
import ru.psv4.tempdatchiki.dto.SubscriptionDto;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.data.Recipient;
import ru.psv4.tempdatchiki.backend.data.Subscription;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

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
    @Resource
    private SubscribtionService subscribtionService;

    @ApiOperation(value = "Получить все подписки, отсортированных по дате создания", response = Iterable.class)
    @RequestMapping(path = "/subcriptions", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody List<SubscriptionDto> getSubscriptionsAll() {
        return DtoUtils.convert(SubscriptionDto.class, subscribtionService.getList());
    }

    @ApiOperation(value = "Подписать слушателя к контроллеру", response = SubscriptionDto.class)
    @RequestMapping(path = "/subcriptions", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody SubscriptionDto createSubscription(@RequestBody SubscriptionCreateDto dto) {
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
        Optional<Subscription> s = subscribtionService.get(r, c);
        if (s.isPresent()) {
            throw new ConflictRestException(String.format("Уже есть подписка " +
                    "для слушателя %s на контроллер %s", r.getName(), c.getName()));
        }

        try {
            return DtoUtils.convert(SubscriptionDto.class, subscribtionService.create(r, c, dto.isNotifyOver(), dto.isNotifyError()));
        } catch (Exception e) {
            log.error(e);
            throw new SystemRestException(e.getMessage());
        }
    }

    @ApiOperation(value = "Удалить подписку по uid")
    @RequestMapping(path = "/subcriptions/{uid}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("uid") String uid) {
        try {
            subscribtionService.deleteByUid(uid);
        } catch (NotFoundException e) {
            throw new NotFoundRestException(String.format("По uid=%s подписка не найдена", uid));
        } catch (Exception e) {
            throw new SystemRestException(e.getMessage());
        }
    }
}
