package com.mycompany.motorph.model;

import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author heartvillegas
 */
public interface AttendanceInterface {
    
public void loadAttendance(String filePath,
                               List<Employee> employees,
                               LocalDate startDate,
                               LocalDate endDate);
    
}
