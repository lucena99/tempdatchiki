package ru.psv4.tempdatchiki.restapi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;
import ru.psv4.tempdatchiki.beans.NotFoundException;
import ru.psv4.tempdatchiki.beans.RecipientService;
import ru.psv4.tempdatchiki.dto.RecipientDto;
import ru.psv4.tempdatchiki.dto.DtoUtils;
import ru.psv4.tempdatchiki.model.Recipient;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("restapi")
public class RecipientActions {

    private static final Logger log = LogManager.getLogger(RecipientActions.class);

    @Resource
    private RecipientService recipientService;

    @RequestMapping(path = "/recipients", method = RequestMethod.GET)
    public @ResponseBody List<RecipientDto> getRecipientsAll() {
        return DtoUtils.convert(Recipient.class, RecipientDto.class, recipientService.getList());
    }

    @RequestMapping(path = "/recipients", method = RequestMethod.POST)
    public @ResponseBody RecipientDto createRecipient(@RequestBody RecipientDto dto) {
        Recipient r = DtoUtils.createReferenceFromDto(RecipientDto.class, Recipient.class, dto);
        r = recipientService.save(r);
        return DtoUtils.convert(Recipient.class, RecipientDto.class, r);
    }

    @RequestMapping(path = "/recipients/{uid}", method = RequestMethod.GET)
    public @ResponseBody RecipientDto info(@PathVariable("uid") String uid) {
        try {
            Recipient r = recipientService.getByUid(uid);
            return DtoUtils.convert(Recipient.class, RecipientDto.class, r);
        } catch (NotFoundException e) {
            throw new NotFoundRestException();
        }
    }

    @RequestMapping(path = "/recipients/{uid}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("uid") String uid) {
        try {
            recipientService.deleteByUid(uid);
        } catch (NotFoundException e) {
            throw new NotFoundRestException();
        } catch (Exception e) {
            throw new SystemRestException();
        }
    }
}
