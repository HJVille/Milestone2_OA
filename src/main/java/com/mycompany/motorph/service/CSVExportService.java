/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

public class CSVExportService {

    public void exportPayroll(){

        try{

            BufferedReader br = new BufferedReader(new FileReader("payroll_records.csv"));

            FileWriter writer = new FileWriter("MotorPH_Payroll_Export.csv");

            String line;

            // copy header
            line = br.readLine();
            writer.write(line + "\n");

            while((line = br.readLine()) != null){

                writer.write(line + "\n");

            }

            br.close();
            writer.close();

        }

        catch(Exception e){

            e.printStackTrace();

        }

    }

}