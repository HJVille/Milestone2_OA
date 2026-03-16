package com.mycompany.motorph.service;

import com.mycompany.motorph.dao.LeaveDAO;
import com.mycompany.motorph.model.LeaveRequest;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LeaveServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void persistsApprovedLeaveRequests() {
        Path leaveFile = tempDir.resolve("leave_requests.csv");
        LeaveService service = new LeaveService(new LeaveDAO(leaveFile));

        service.submitLeave(new LeaveRequest(
                10001,
                "Alice Dela Cruz",
                "Vacation Leave",
                "2026-03-20",
                "2026-03-22"
        ));

        assertTrue(service.respondToLeave(10001, "2026-03-20", true, "Approved for schedule coverage."));

        LeaveService reloaded = new LeaveService(new LeaveDAO(leaveFile));
        LeaveRequest savedRequest = reloaded.getRequestsForEmployee(10001).getFirst();

        assertEquals("APPROVED", savedRequest.getStatus());
        assertEquals("Approved for schedule coverage.", savedRequest.getStatusMessage());
    }

    @Test
    void returnsFalseWhenTheLeaveRequestDoesNotExist() {
        LeaveService service = new LeaveService(new LeaveDAO(tempDir.resolve("leave_requests.csv")));

        assertFalse(service.respondToLeave(99999, "2026-03-20", false, "No matching request."));
    }
}
