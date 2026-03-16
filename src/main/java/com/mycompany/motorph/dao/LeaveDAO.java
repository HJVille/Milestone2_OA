package com.mycompany.motorph.dao;

import com.mycompany.motorph.model.LeaveInterface;
import com.mycompany.motorph.model.LeaveRequest;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LeaveDAO implements LeaveInterface {

    private static final String HEADER =
            "employeeNumber,employeeName,leaveType,startDate,endDate,status,remarks";
    private static final Logger LOGGER = Logger.getLogger(LeaveDAO.class.getName());

    private final Path filePath;

    public LeaveDAO() {
        this(CsvFilePaths.LEAVE_REQUESTS);
    }

    public LeaveDAO(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public List<LeaveRequest> loadLeaves() {

        List<LeaveRequest> requests = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return requests;
        }

        try (BufferedReader br = Files.newBufferedReader(filePath)) {

            String line = br.readLine();
            if (line == null) {
                return requests;
            }

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (data.length < 6) {
                    continue;
                }

                LeaveRequest request = new LeaveRequest(
                        Integer.parseInt(data[0].trim()),
                        clean(data[1]),
                        clean(data[2]),
                        clean(data[3]),
                        clean(data[4]),
                        data.length > 6 ? clean(data[6]) : ""
                );
                request.setStatus(clean(data[5]), data.length > 6 ? clean(data[6]) : "");
                requests.add(request);

            }

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to load leave requests.", e);
        }

        return requests;
    }

    @Override
    public void saveLeave(LeaveRequest leave) {

        List<LeaveRequest> requests = loadLeaves();
        requests.add(leave);
        saveAllLeaves(requests);

    }

    public void saveAllLeaves(List<LeaveRequest> requests) {

        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to prepare leave requests file.", e);
            return;
        }

        try (BufferedWriter bw = Files.newBufferedWriter(
                filePath,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {

            bw.write(HEADER);
            bw.newLine();

            for (LeaveRequest request : requests) {
                bw.write(String.join(",",
                        String.valueOf(request.getEmployeeNumber()),
                        escapeCsv(request.getEmployeeName()),
                        escapeCsv(request.getLeaveType()),
                        request.getStartDate(),
                        request.getEndDate(),
                        request.getStatus(),
                        escapeCsv(request.getStatusMessage())));
                bw.newLine();
            }

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to save leave requests.", e);
        }
    }

    private String clean(String value) {

        if (value == null) {
            return "";
        }

        String cleaned = value.trim();
        if (cleaned.startsWith("\"") && cleaned.endsWith("\"") && cleaned.length() >= 2) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }

        return cleaned.replace("\"\"", "\"");
    }

    private String escapeCsv(String value) {

        if (value == null) {
            return "";
        }

        String cleaned = value.trim();
        if (cleaned.contains(",") || cleaned.contains("\"")) {
            return "\"" + cleaned.replace("\"", "\"\"") + "\"";
        }

        return cleaned;
    }
}
