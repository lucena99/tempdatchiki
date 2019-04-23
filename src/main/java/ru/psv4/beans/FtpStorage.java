package ru.psv4.beans;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import ru.psv4.utils.DateUtils;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FtpStorage {

    private static final Logger log = LogManager.getLogger(FtpStorage.class);

    @Value("${ftp.path}")
    private String ftpPath;

    @Value("${ftp.host}")
    private String ftpHost;

    @Value("${ftp.user}")
    private String ftpUser;

    @Value("${ftp.password}")
    private String ftpPassword;

    @Value("${ftp.port}")
    private int ftpPort;

    private final static DateTimeFormatter dateStorageFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void upload(String fileName, LocalDate date, byte[] data) {
        FTPClient ftp = new FTPClient();
        try {
            ftp.connect(ftpHost, ftpPort);
            ftp.login(ftpUser, ftpPassword);
            ftp.enterLocalPassiveMode();

            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);

            String remoteFolder = "";
            for (String folder : (ftpPath + "/" + dateStorageFormatter.format(date)).split("/")) {
                if (!folder.isEmpty()) {
                    ftp.makeDirectory(remoteFolder = remoteFolder + "/" + folder);
                }
            }
            String remoteFile = remoteFolder + "/" +  fileName;

            OutputStream outputStream = ftp.storeFileStream(remoteFile);
            byte[] bytesIn = new byte[4096];
            int read = 0;

            InputStream inputStream = new ByteArrayInputStream(data);
            while ((read = inputStream.read(bytesIn)) != -1) {
                outputStream.write(bytesIn, 0, read);
            }
            inputStream.close();
            outputStream.close();

            boolean completed = ftp.completePendingCommand();
            if (!completed) {
                throw new IllegalStateException("File uploading not successfully " + remoteFile);
            }
            log.info("File uploaded succesfully " + remoteFile);
        } catch (IOException ex) {
            log.error("Error", ex);
            throw new RuntimeException(ex);
        } finally {
            try {
                if (ftp.isConnected()) {
                    ftp.logout();
                    ftp.disconnect();
                }
            } catch (IOException ex) {
                log.error("Error", ex);
                throw new RuntimeException(ex);
            }
        }
    }

    public List<String> getFolders() {
        List<String> folders = new ArrayList<>();
        FTPClient ftp = new FTPClient();
        try {
            ftp.connect(ftpHost, ftpPort);
            ftp.login(ftpUser, ftpPassword);
            ftp.enterLocalPassiveMode();

            ftp.changeWorkingDirectory(ftpPath);
            FTPFile[] ftpFolders = ftp.listDirectories();
            log.info("Succesfully read directories " + ftpFolders.length);
            for (FTPFile ftpFolder : ftpFolders) {
                folders.add(ftpFolder.getName());
            }
            return folders;
        } catch (IOException ex) {
            log.error("Error", ex);
            throw new RuntimeException(ex);
        } finally {
            try {
                if (ftp.isConnected()) {
                    ftp.logout();
                    ftp.disconnect();
                }
            } catch (IOException ex) {
                log.error("Error", ex);
                throw new RuntimeException(ex);
            }
        }
    }

    public void downloadFilesToLocalFolder(String localFolder, LocalDate date1, LocalDate date2) {
        List<LocalDate> dates = DateUtils.getIntervalDates(date1, date2);
        if (dates.isEmpty()) {
            log.warn("Empty dates for download files " + DateUtils.formatInterval(date1, date2));
            return;
        }

        FTPClient ftp = new FTPClient();
        try {
            ftp.connect(ftpHost, ftpPort);
            ftp.login(ftpUser, ftpPassword);
            ftp.enterLocalPassiveMode();

            for (LocalDate date : dates) {
                String path = ftpPath + "/" + dateStorageFormatter.format(date);
                FTPFile[] ftpFiles = ftp.listFiles(path);
                for (FTPFile ftpFile : ftpFiles) {
                    FileOutputStream out = new FileOutputStream(localFolder + File.separator + ftpFile.getName());
                    ftp.retrieveFile(path + "/" + ftpFile.getName(), out);
                    out.close();
                }
            }
        } catch (IOException ex) {
            log.error("Error", ex);
            throw new RuntimeException(ex);
        } finally {
            try {
                if (ftp.isConnected()) {
                    ftp.logout();
                    ftp.disconnect();
                }
            } catch (IOException ex) {
                log.error("Error", ex);
                throw new RuntimeException(ex);
            }
        }
    }
}
