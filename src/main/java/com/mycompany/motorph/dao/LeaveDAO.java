/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.dao;

import com.mycompany.motorph.model.LeaveRequest;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LeaveDAO {

    private final String filePath = "leave_requests.csv";

    public List<LeaveRequest> loadLeaves() {

        List<LeaveRequest> requests = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {

                String[] data = line.split(",");

                int empNum = Integer.parseInt(data[0]);
                String name = data[1];
                String type = data[2];
                String start = data[3];
                String end = data[4];
                String status = data[5];

                LeaveRequest req = new LeaveRequest(empNum, name, type, start, end);

                if (status.equals("APPROVED")) req.approve();
                if (status.equals("REJECTED")) req.reject();

                requests.add(req);

            }

        } catch (Exception e) {

            System.out.println("No existing leave records.");

        }

        return requests;

    }

    public void saveLeave(LeaveRequest leave) {

        try (FileWriter fw = new FileWriter(filePath, true);
             BufferedWriter bw = new BufferedWriter(fw)) {

            bw.write(
                    leave.getEmployeeNumber() + "," +
                    leave.getEmployeeName() + "," +
                    leave.getLeaveType() + "," +
                    leave.getStartDate() + "," +
                    leave.getEndDate() + "," +
                    leave.getStatus()
            );

            bw.newLine();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

}