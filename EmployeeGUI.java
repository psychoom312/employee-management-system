package in.ac.adit.pwj.miniproject.employees;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class EmployeeGUI {

    private EmployeeManager manager = new EmployeeManager();
    private DefaultTableModel model;

    public EmployeeGUI() {

        JFrame frame = new JFrame("Employee Management System");

        // 🌙 DARK THEME COLORS
        Color bg = new Color(30, 30, 30);
        Color fg = Color.WHITE;
        Color btnColor = new Color(50, 150, 250);

        // ===== INPUTS =====
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField salaryField = new JTextField();

        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Full-Time", "Part-Time"});

        // Style inputs
        JTextField[] fields = {idField, nameField, salaryField};
        for (JTextField f : fields) {
            f.setBackground(new Color(50,50,50));
            f.setForeground(Color.WHITE);
            f.setCaretColor(Color.WHITE);
        }

        typeBox.setBackground(new Color(50,50,50));
        typeBox.setForeground(Color.WHITE);

        // ===== BUTTONS =====
        JButton addBtn = createButton("Add", btnColor);
        JButton updateBtn = createButton("Update", btnColor);
        JButton deleteBtn = createButton("Delete", new Color(200, 80, 80));
        JButton searchBtn = createButton("Search", new Color(100, 200, 100));
        JButton refreshBtn = createButton("Refresh", btnColor);
        JButton sortBtn = createButton("Sort Salary", new Color(180, 120, 255));
        JButton bonusBtn = createButton("Apply Bonus", new Color(255, 180, 60));

        // ===== TABLE =====
        model = new DefaultTableModel(new String[]{"ID", "Name", "Salary", "Type"}, 0);
        JTable table = new JTable(model);
        table.setBackground(bg);
        table.setForeground(fg);
        table.setGridColor(Color.GRAY);
        table.setRowHeight(25);
        table.getTableHeader().setBackground(new Color(70,70,70));
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(table);

        // ===== LAYOUT =====
        frame.setLayout(new BorderLayout(10,10));
        frame.getContentPane().setBackground(bg);

        JPanel inputPanel = new JPanel(new GridLayout(4,2,10,10));
        inputPanel.setBackground(bg);
        inputPanel.setBorder(BorderFactory.createTitledBorder("Employee Details"));

        addLabel(inputPanel, "ID:", fg);
        inputPanel.add(idField);
        addLabel(inputPanel, "Name:", fg);
        inputPanel.add(nameField);
        addLabel(inputPanel, "Salary:", fg);
        inputPanel.add(salaryField);
        addLabel(inputPanel, "Type:", fg);
        inputPanel.add(typeBox);

        JPanel buttonPanel = new JPanel(new GridLayout(2,4,10,10));
        buttonPanel.setBackground(bg);

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(searchBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(sortBtn);
        buttonPanel.add(bonusBtn);

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(scroll, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // ===== LOAD =====
        manager.loadFromFile();
        refreshTable();

        // ================= ACTIONS =================

        addBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                String name = nameField.getText();
                double salary = Double.parseDouble(salaryField.getText());

                if (name.isEmpty()) throw new Exception();

                if (typeBox.getSelectedIndex() == 0)
                    manager.addEmployee(new FullTimeEmployee(id, name, salary));
                else
                    manager.addEmployee(new PartTimeEmployee(id, name, salary));

                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "❌ Invalid Input");
            }
        });

        updateBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                double salary = Double.parseDouble(salaryField.getText());

                Thread t = manager.new UpdateThread(id, salary);
                t.start();
                Thread.sleep(100);

                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "❌ Update Failed");
            }
        });

        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(frame, "Delete this employee?");
            if (confirm == 0) {
                int id = Integer.parseInt(idField.getText());
                manager.deleteEmployee(id);
                refreshTable();
            }
        });

        searchBtn.addActionListener(e -> {
            int id = Integer.parseInt(idField.getText());
            Employee emp = manager.getEmployee(id);

            if (emp != null) {
                JOptionPane.showMessageDialog(frame,
                        "✅ Found\nName: " + emp.name + "\nSalary: " + emp.salary);
            } else {
                JOptionPane.showMessageDialog(frame, "❌ Not Found");
            }
        });

        refreshBtn.addActionListener(e -> refreshTable());

        sortBtn.addActionListener(e -> sortTable());

        bonusBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog("Enter Bonus %:");
            try {
                double percent = Double.parseDouble(input);
                manager.applyBonus(percent);
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "❌ Invalid value");
            }
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.getSelectedRow();

                idField.setText(model.getValueAt(row, 0).toString());
                nameField.setText(model.getValueAt(row, 1).toString());
                salaryField.setText(model.getValueAt(row, 2).toString());
                typeBox.setSelectedItem(model.getValueAt(row, 3));
            }
        });

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                manager.saveToFile();
            }
        });

        frame.setSize(700, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    // ===== UTIL METHODS =====

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }

    private void addLabel(JPanel panel, String text, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        panel.add(label);
    }

    private void refreshTable() {
        model.setRowCount(0);

        for (Employee e : manager.getAllEmployees()) {
            String type = (e instanceof FullTimeEmployee) ? "Full-Time" : "Part-Time";
            model.addRow(new Object[]{e.id, e.name, e.salary, type});
        }
    }

    private void sortTable() {
        java.util.List<Employee> list = new java.util.ArrayList<>(manager.getAllEmployees());
        list.sort((a, b) -> Double.compare(b.salary, a.salary));

        model.setRowCount(0);
        for (Employee e : list) {
            String type = (e instanceof FullTimeEmployee) ? "Full-Time" : "Part-Time";
            model.addRow(new Object[]{e.id, e.name, e.salary, type});
        }
    }

    public static void main(String[] args) {
        new EmployeeGUI();
    }
}