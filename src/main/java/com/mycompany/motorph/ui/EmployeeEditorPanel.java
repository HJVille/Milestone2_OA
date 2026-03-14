package com.mycompany.motorph.ui;

import com.mycompany.motorph.model.Employee;
import com.mycompany.motorph.model.EmployeeFormData;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class EmployeeEditorPanel extends JPanel {

    private final JTextField employeeNumberField = new JTextField(18);
    private final JTextField firstNameField = new JTextField(18);
    private final JTextField lastNameField = new JTextField(18);
    private final JTextField birthDateField = new JTextField(18);
    private final JTextField positionField = new JTextField(18);
    private final JTextField statusField = new JTextField(18);
    private final JTextField supervisorField = new JTextField(18);
    private final JTextField addressField = new JTextField(18);
    private final JTextField phoneField = new JTextField(18);
    private final JTextField sssField = new JTextField(18);
    private final JTextField philhealthField = new JTextField(18);
    private final JTextField tinField = new JTextField(18);
    private final JTextField pagibigField = new JTextField(18);
    private final JTextField basicSalaryField = new JTextField(18);
    private final JTextField riceSubsidyField = new JTextField(18);
    private final JTextField phoneAllowanceField = new JTextField(18);
    private final JTextField clothingAllowanceField = new JTextField(18);

    public EmployeeEditorPanel(Employee employee) {
        setLayout(new GridBagLayout());
        BrandTheme.styleSurface(this);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BrandTheme.LAVENDER, 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        setPreferredSize(new Dimension(720, 620));
        setMinimumSize(new Dimension(680, 580));
        buildForm();
        populate(employee);
    }

    public EmployeeFormData getFormData() {

        EmployeeFormData data = new EmployeeFormData();
        data.setEmployeeNumber(employeeNumberField.getText());
        data.setFirstName(firstNameField.getText());
        data.setLastName(lastNameField.getText());
        data.setBirthDate(birthDateField.getText());
        data.setPosition(positionField.getText());
        data.setStatus(statusField.getText());
        data.setSupervisor(supervisorField.getText());
        data.setAddress(addressField.getText());
        data.setPhone(phoneField.getText());
        data.setSss(sssField.getText());
        data.setPhilhealth(philhealthField.getText());
        data.setTin(tinField.getText());
        data.setPagibig(pagibigField.getText());
        data.setBasicSalary(basicSalaryField.getText());
        data.setRiceSubsidy(riceSubsidyField.getText());
        data.setPhoneAllowance(phoneAllowanceField.getText());
        data.setClothingAllowance(clothingAllowanceField.getText());
        return data;
    }

    private void buildForm() {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;
        row = addField("Employee Number", employeeNumberField, gbc, row);
        row = addField("First Name", firstNameField, gbc, row);
        row = addField("Last Name", lastNameField, gbc, row);
        row = addField("Birth Date", birthDateField, gbc, row);
        row = addField("Position", positionField, gbc, row);
        row = addField("Status", statusField, gbc, row);
        row = addField("Supervisor", supervisorField, gbc, row);
        row = addField("Address", addressField, gbc, row);
        row = addField("Phone", phoneField, gbc, row);
        row = addField("SSS", sssField, gbc, row);
        row = addField("PhilHealth", philhealthField, gbc, row);
        row = addField("TIN", tinField, gbc, row);
        row = addField("Pag-IBIG", pagibigField, gbc, row);
        row = addField("Basic Salary", basicSalaryField, gbc, row);
        row = addField("Rice Subsidy", riceSubsidyField, gbc, row);
        row = addField("Phone Allowance", phoneAllowanceField, gbc, row);
        row = addField("Clothing Allowance", clothingAllowanceField, gbc, row);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        JPanel filler = new JPanel();
        filler.setOpaque(false);
        add(filler, gbc);
    }

    private int addField(String labelText, JTextField field, GridBagConstraints gbc, int row) {

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        JLabel label = new JLabel(labelText);
        label.setFont(BrandTheme.BUTTON_FONT.deriveFont(15f));
        label.setForeground(BrandTheme.TEXT);
        label.setPreferredSize(new Dimension(150, 30));
        add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        BrandTheme.styleInputField(field);
        field.setPreferredSize(new Dimension(420, 34));
        field.setMinimumSize(new Dimension(320, 34));
        add(field, gbc);

        return row + 1;
    }

    private void populate(Employee employee) {

        if (employee == null) {
            return;
        }

        employeeNumberField.setText(String.valueOf(employee.getEmployeeNumber()));
        firstNameField.setText(employee.getFirstName());
        lastNameField.setText(employee.getLastName());
        birthDateField.setText(employee.getBirthDate());
        positionField.setText(employee.getPosition());
        statusField.setText(employee.getStatus());
        supervisorField.setText(employee.getSupervisor());
        addressField.setText(employee.getAddress());
        phoneField.setText(employee.getPhone());
        sssField.setText(employee.getSss());
        philhealthField.setText(employee.getPhilhealth());
        tinField.setText(employee.getTin());
        pagibigField.setText(employee.getPagibig());
        basicSalaryField.setText(String.valueOf(employee.getBasicSalary()));
        riceSubsidyField.setText(String.valueOf(employee.getRiceSubsidy()));
        phoneAllowanceField.setText(String.valueOf(employee.getPhoneAllowance()));
        clothingAllowanceField.setText(String.valueOf(employee.getClothingAllowance()));
    }
}
