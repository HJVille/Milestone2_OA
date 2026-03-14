package com.mycompany.motorph.service;

import com.mycompany.motorph.model.LeaveRequest;
import java.util.List;

public interface LeaveManager {

    void submitLeave(LeaveRequest leave);

    List<LeaveRequest> getRequests();

    void approveLeave(int index);

    void rejectLeave(int index);
}