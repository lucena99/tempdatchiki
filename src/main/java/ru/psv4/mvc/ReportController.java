package ru.psv4.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.psv4.beans.FtpStorage;
import ru.psv4.dto.DateRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class ReportController {

    @Autowired
    private FtpStorage ftpStorage;

    @RequestMapping("/")
    public String report(Map<String, Object> model) {
        model.put("rows", readRows());
        return "report";
    }

    private List<DateRow> readRows() {
        List<String> folders = ftpStorage.getFolders();
        DateRowsComposer composer = new DateRowsComposer();
        for (String folder : folders) {
            composer.add(folder);
        }
        return composer.getRows();
    }

    private class DateRowsComposer {
        private List<DateRow> rows = new ArrayList<>();
        private List<String> current = new ArrayList<>();

        public void add(String date) {
            if (current.size() == 3) flush();
            current.add(date);
        }

        private void flush() {
            if (!current.isEmpty()) {
                DateRow row = new DateRow(
                        current.size() >= 1 ? current.get(0) : "",
                        current.size() >= 2 ? current.get(1) : "",
                        current.size() == 3 ? current.get(2) : ""
                );
                rows.add(row);
                current.clear();
            }
        }

        public List<DateRow> getRows() {
            flush();
            return rows;
        }
    }
}
