package com.mycompany.motorph.model;

import java.util.List;

/**
 *
 * @author heartvillegas
 */
public interface LeaveInterface {
    
public List<LeaveRequest> loadLeaves();
public void saveLeave(LeaveRequest leave);
}
