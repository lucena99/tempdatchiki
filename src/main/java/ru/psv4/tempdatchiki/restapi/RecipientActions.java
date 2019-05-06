package ru.psv4.tempdatchiki.restapi;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;
import ru.psv4.tempdatchiki.beans.NotFoundException;
import ru.psv4.tempdatchiki.beans.RecipientService;
import ru.psv4.tempdatchiki.dto.DtoUtils;
import ru.psv4.tempdatchiki.dto.RecipientCreateDto;
import ru.psv4.tempdatchiki.dto.RecipientDto;
import ru.psv4.tempdatchiki.model.Recipient;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("restapi")
@Api(tags = {"Recipients Resource"}, description = "Действия с получателями")
@SwaggerDefinition(tags = {
        @Tag(name = "Recipients Resource", description = "Действия с получателями")
})
public class RecipientActions {

    private static final Logger log = LogManager.getLogger(RecipientActions.class);

    @Resource
    private RecipientService recipientService;

    @ApiOperation(value = "Получить всех получателей, отсортированных по дате создания", response = Iterable.class)
    @RequestMapping(path = "/recipients", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody List<RecipientDto> getRecipientsAll() {
        return DtoUtils.convert(RecipientDto.class, recipientService.getList());
    }

    @ApiOperation(value = "Создать нового получателя", response = RecipientDto.class)
    @RequestMapping(path = "/recipients", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody RecipientDto createRecipient(@RequestBody RecipientCreateDto dto) {
        String name = dto.getName();
        List<Recipient> existedList = recipientService.getByNameIgnoreCase(name);
        if (existedList.size() > 0) {
            throw new ConflictRestException(String.format("Уже существует получатель с именем %s", name));
        }
        try {
            Recipient r = new Recipient();
            r.setUid(dto.getUid());
            r.setName(dto.getName());
            r.setCreatedDatetime(LocalDateTime.now());
            r = recipientService.save(r);
            return DtoUtils.convert(RecipientDto.class, r);
        } catch (Exception e) {
            throw new SystemRestException(e.getMessage());
        }
    }

    @ApiOperation(value = "Получить получателя по uid", response = RecipientDto.class)
    @RequestMapping(path = "/recipients/{uid}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody RecipientDto info(@PathVariable("uid") String uid) {
        try {
            Recipient r = recipientService.getByUid(uid);
            return DtoUtils.convert(RecipientDto.class, r);
        } catch (NotFoundException e) {
            throw new NotFoundRestException(String.format("Получатель не найден по uid=%s", uid));
        }
    }

    @ApiOperation(value = "Удалить получателя по uid")
    @RequestMapping(path = "/recipients/{uid}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("uid") String uid) {
        try {
            recipientService.deleteByUid(uid);
        } catch (NotFoundException e) {
            throw new NotFoundRestException(String.format("По uid=%s получатель не найден", uid));
        } catch (Exception e) {
            throw new SystemRestException(e.getMessage());
        }
    }
}
