/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.service;

import com.mycompany.motorph.model.LeaveRequest;
import com.mycompany.motorph.dao.LeaveDAO;

import java.util.List;

public class LeaveService implements LeaveManager {

    private LeaveDAO leaveDAO = new LeaveDAO();
    private List<LeaveRequest> requests;

    public LeaveService() {

        requests = leaveDAO.loadLeaves();

    }

    @Override
    public void submitLeave(LeaveRequest leave) {

        requests.add(leave);

        leaveDAO.saveLeave(leave);

        System.out.println("Leave request submitted.");

    }

    @Override
    public List<LeaveRequest> getRequests() {

        return requests;

    }

    @Override
    public void approveLeave(int index) {

        if (index >= 0 && index < requests.size()) {

            requests.get(index).approve();

        }

    }

    @Override
    public void rejectLeave(int index) {

        if (index >= 0 && index < requests.size()) {

            requests.get(index).reject();

        }

    }

}