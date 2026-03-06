/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.service;

import java.awt.*;
import java.awt.print.*;

public class PrintPayslipService {

    public void print(String payslipText){

        PrinterJob job = PrinterJob.getPrinterJob();

        job.setPrintable(new Printable() {

            public int print(Graphics g, PageFormat pf, int pageIndex) {

                if(pageIndex > 0){
                    return Printable.NO_SUCH_PAGE;
                }

                Graphics2D g2 = (Graphics2D) g;

                g2.translate(pf.getImageableX(), pf.getImageableY());

                g.setFont(new Font("Monospaced", Font.PLAIN, 11));

                int y = 20;

                for(String line : payslipText.split("\n")){

                    g.drawString(line, 10, y);

                    y += 15;

                }

                return Printable.PAGE_EXISTS;

            }

        });

        boolean doPrint = job.printDialog();

        if(doPrint){

            try{

                job.print();

            }catch(Exception e){

                e.printStackTrace();

            }

        }

    }

}