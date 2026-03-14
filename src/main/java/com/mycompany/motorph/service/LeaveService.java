package com.mycompany.motorph.service;

import com.mycompany.motorph.dao.LeaveDAO;
import com.mycompany.motorph.model.LeaveRequest;
import java.util.ArrayList;
import java.util.List;

public class LeaveService implements LeaveManager {

    private final LeaveDAO leaveDAO = new LeaveDAO();
    private final List<LeaveRequest> requests;

    public LeaveService() {
        requests = new ArrayList<>(leaveDAO.loadLeaves());
    }

    @Override
    public void submitLeave(LeaveRequest leave) {

        requests.add(leave);
        leaveDAO.saveAllLeaves(requests);

    }

    @Override
    public List<LeaveRequest> getRequests() {
        return new ArrayList<>(requests);
    }

    public List<LeaveRequest> getRequestsForEmployee(int employeeNumber) {

        List<LeaveRequest> matches = new ArrayList<>();

        for (LeaveRequest request : requests) {
            if (request.getEmployeeNumber() == employeeNumber) {
                matches.add(request);
            }
        }

        return matches;
    }

    @Override
    public void approveLeave(int index) {

        if (index >= 0 && index < requests.size()) {
            requests.get(index).approve();
            leaveDAO.saveAllLeaves(requests);
        }

    }

    @Override
    public void rejectLeave(int index) {

        if (index >= 0 && index < requests.size()) {
            requests.get(index).reject();
            leaveDAO.saveAllLeaves(requests);
        }

    }

    public boolean respondToLeave(int employeeNumber, String startDate, boolean approved) {
        return respondToLeave(employeeNumber, startDate, approved, "");
    }

    public boolean respondToLeave(int employeeNumber,
                                  String startDate,
                                  boolean approved,
                                  String statusMessage) {

        for (LeaveRequest request : requests) {
            if (request.getEmployeeNumber() == employeeNumber
                    && request.getStartDate().equals(startDate)) {

                if (approved) {
                    request.approve(statusMessage);
                } else {
                    request.reject(statusMessage);
                }

                leaveDAO.saveAllLeaves(requests);
                return true;
            }
        }

        return false;
    }
}
