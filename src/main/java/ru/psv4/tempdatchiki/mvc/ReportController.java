package ru.psv4.tempdatchiki.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.psv4.tempdatchiki.dto.DateRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class ReportController {

    @RequestMapping("/")
    public String report(Map<String, Object> model) {
        model.put("rows", "rows");
        return "report";
    }
}
