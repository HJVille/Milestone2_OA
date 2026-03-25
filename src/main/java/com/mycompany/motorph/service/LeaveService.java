package com.mycompany.motorph.service;

import com.mycompany.motorph.dao.LeaveDAO;
import com.mycompany.motorph.model.LeaveRequest;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LeaveService implements LeaveManager {

    private final LeaveDAO leaveDAO;
    private final Clock clock;

    public LeaveService() {
        this(new LeaveDAO(), AppClock.clock());
    }

    public LeaveService(LeaveDAO leaveDAO) {
        this(leaveDAO, AppClock.clock());
    }

    public LeaveService(LeaveDAO leaveDAO, Clock clock) {
        this.leaveDAO = Objects.requireNonNull(leaveDAO, "leaveDAO");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    @Override
    public void submitLeave(LeaveRequest leave) {
        validateLeaveDates(leave);

        List<LeaveRequest> requests = loadRequests();
        requests.add(leave);
        saveRequests(requests);

    }

    @Override
    public List<LeaveRequest> getRequests() {
        return loadRequests();
    }

    public List<LeaveRequest> getRequestsForEmployee(int employeeNumber) {

        List<LeaveRequest> matches = new ArrayList<>();

        for (LeaveRequest request : loadRequests()) {
            if (request.getEmployeeNumber() == employeeNumber) {
                matches.add(request);
            }
        }

        return matches;
    }

    @Override
    public void approveLeave(int index) {

        List<LeaveRequest> requests = loadRequests();
        if (index >= 0 && index < requests.size()) {
            requests.get(index).approve();
            saveRequests(requests);
        }

    }

    @Override
    public void rejectLeave(int index) {

        List<LeaveRequest> requests = loadRequests();
        if (index >= 0 && index < requests.size()) {
            requests.get(index).reject();
            saveRequests(requests);
        }

    }

    public boolean respondToLeave(int employeeNumber, String startDate, boolean approved) {
        return respondToLeave(employeeNumber, startDate, null, approved, "");
    }

    public boolean respondToLeave(int employeeNumber,
                                  String startDate,
                                  boolean approved,
                                  String statusMessage) {
        return respondToLeave(employeeNumber, startDate, null, approved, statusMessage);
    }

    public boolean respondToLeave(int employeeNumber,
                                  String startDate,
                                  String endDate,
                                  boolean approved,
                                  String statusMessage) {

        List<LeaveRequest> requests = loadRequests();
        for (int index = requests.size() - 1; index >= 0; index--) {
            LeaveRequest request = requests.get(index);
            if (request.getEmployeeNumber() == employeeNumber
                    && request.getStartDate().equals(startDate)
                    && matchesEndDate(request, endDate)
                    && "PENDING".equalsIgnoreCase(request.getStatus())) {

                if (approved) {
                    request.approve(statusMessage);
                } else {
                    request.reject(statusMessage);
                }

                saveRequests(requests);
                return true;
            }
        }

        return false;
    }

    private boolean matchesEndDate(LeaveRequest request, String endDate) {
        return endDate == null || endDate.isBlank() || request.getEndDate().equals(endDate);
    }

    private List<LeaveRequest> loadRequests() {
        return new ArrayList<>(leaveDAO.loadLeaves());
    }

    private void saveRequests(List<LeaveRequest> requests) {
        leaveDAO.saveAllLeaves(requests);
    }

    private void validateLeaveDates(LeaveRequest leave) {
        if (leave == null) {
            throw new IllegalArgumentException("Leave request cannot be empty.");
        }

        LocalDate startDate = LocalDate.parse(leave.getStartDate());
        LocalDate endDate = LocalDate.parse(leave.getEndDate());
        LocalDate today = LocalDate.now(clock);

        if (startDate.isBefore(today) || endDate.isBefore(today)) {
            throw new IllegalArgumentException("Leave dates cannot be earlier than today.");
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be earlier than start date.");
        }
    }
}
