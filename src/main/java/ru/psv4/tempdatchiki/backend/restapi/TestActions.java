package ru.psv4.tempdatchiki.backend.restapi;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;
import ru.psv4.tempdatchiki.backend.data.Recipient;
import ru.psv4.tempdatchiki.backend.restapi.dto.DtoUtils;
import ru.psv4.tempdatchiki.backend.restapi.dto.RecipientCreateDto;
import ru.psv4.tempdatchiki.backend.restapi.dto.RecipientDto;
import ru.psv4.tempdatchiki.backend.service.NotFoundException;
import ru.psv4.tempdatchiki.backend.service.RecipientService;
import ru.psv4.tempdatchiki.utils.UIDUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("test")
public class TestActions {

    private static final Logger log = LogManager.getLogger(TestActions.class);

    @RequestMapping(path = "/pause", method = RequestMethod.GET)
    public String getRecipientsAll() {
        int pause = 3*60;
        try {
            TimeUnit.SECONDS.sleep(pause);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "Time pause " + pause + " sec";
    }
}
