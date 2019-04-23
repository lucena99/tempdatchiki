package ru.psv4.servlet;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import ru.psv4.beans.AntifrodEventParser;
import ru.psv4.beans.ExcelSerializer;
import ru.psv4.beans.FtpStorage;
import ru.psv4.utils.DateUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@WebServlet(urlPatterns = "/report-servlet", loadOnStartup = 1)
public class ReportServlet extends HttpServlet {

    private static final Logger log = LogManager.getLogger(ReportServlet.class);

    @Autowired
    private FtpStorage ftpStorage;

    @Autowired
    private AntifrodEventParser antifrodEventParser;

    @Autowired
    private ExcelSerializer excelSerializer;

    private final DateTimeFormatter date_formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet(request,response);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LocalDate date1 = LocalDate.parse(request.getParameter("date1"), date_formatter);
        LocalDate date2 = LocalDate.parse(request.getParameter("date2"), date_formatter);

        String localFolder = Files.createTempDirectory("antifrod-proxy").getFileName().toFile().getAbsolutePath();
        AntifrodEventParser.Data data;
        try {
            new File(localFolder).mkdirs();
            ftpStorage.downloadFilesToLocalFolder(localFolder, date1, date2);
            data = antifrodEventParser.parseFiles(localFolder);
        } finally {
            if (localFolder != null) {
                FileUtils.deleteDirectory(new File(localFolder));
            }
        }

        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" +
                DateUtils.formatInterval(date1, date2) + ".xlsx");
        ServletOutputStream out = response.getOutputStream();
        excelSerializer.serialize(data).write(out);
        out.flush();
        out.close();
    }
}
