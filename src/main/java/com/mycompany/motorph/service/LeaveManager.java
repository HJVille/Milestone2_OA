/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.service;

import com.mycompany.motorph.model.LeaveRequest;
import java.util.List;

public interface LeaveManager {

    void submitLeave(LeaveRequest leave);

    List<LeaveRequest> getRequests();

    void approveLeave(int index);

    void rejectLeave(int index);
}