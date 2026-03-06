/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.ui;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;

public class PayrollAnalyticsFrame extends JFrame {

    private double[] monthlyTotals = new double[12];

    private final DecimalFormat peso = new DecimalFormat("₱#,##0.00");

    public PayrollAnalyticsFrame(){

        setTitle("MotorPH Payroll Analytics");
        setSize(700,450);
        setLocationRelativeTo(null);

        loadPayrollData();

        add(new ChartPanel());

    }

    private void loadPayrollData(){

        try{

            BufferedReader br = new BufferedReader(new FileReader("payroll_records.csv"));

            String line;

            br.readLine();

            while((line = br.readLine()) != null){

                String[] data = line.split(",");

                String periodStart = data[2];

                int month = Integer.parseInt(periodStart.substring(5,7));

                double net = Double.parseDouble(data[13]);

                monthlyTotals[month-1] += net;

            }

            br.close();

        }catch(Exception e){

            JOptionPane.showMessageDialog(this,
                    "Unable to load payroll data.");

        }

    }

    class ChartPanel extends JPanel{

        protected void paintComponent(Graphics g){

            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;

            int width = getWidth();
            int height = getHeight();

            int barWidth = width / 14;

            double max = 0;

            for(double v : monthlyTotals){
                if(v > max) max = v;
            }

            String[] months = {
                    "Jan","Feb","Mar","Apr","May","Jun",
                    "Jul","Aug","Sep","Oct","Nov","Dec"
            };

            for(int i=0;i<12;i++){

                int barHeight = (int)((monthlyTotals[i]/max) * (height-120));

                int x = 50 + i*barWidth;
                int y = height - barHeight - 60;

                g2.fillRect(x,y,barWidth-10,barHeight);

                g2.drawString(months[i],x,height-40);

                g2.drawString(peso.format(monthlyTotals[i]),x,y-5);

            }

            g2.drawString("Monthly Payroll Net Distribution",width/2-80,30);

        }

    }

}