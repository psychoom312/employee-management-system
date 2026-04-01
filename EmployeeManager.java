package in.ac.adit.pwj.miniproject.employees;

import java.io.*;
import java.util.*;

public class EmployeeManager {

    private Map<Integer, Employee> employees = new HashMap<>();
    private Set<Integer> empIds = new HashSet<>();
    private final String FILE_NAME = "employees.txt";

    // ================= ADD =================
    public synchronized void addEmployee(Employee e) {
        if (empIds.contains(e.id)) {
            System.out.println("❌ Employee ID already exists!");
            return;
        }
        employees.put(e.id, e);
        empIds.add(e.id);
        System.out.println("✅ Employee Added");
    }

    // ================= UPDATE =================
    public synchronized void updateEmployee(int id, double salary) {
        try {
            if (salary < 0)
                throw new IllegalArgumentException("Salary cannot be negative");

            Employee e = employees.get(id);
            if (e != null) {
                e.salary = salary;
                System.out.println("✅ Updated Successfully");
            } else {
                System.out.println("❌ Employee Not Found");
            }
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    // ================= DELETE =================
    public synchronized void deleteEmployee(int id) {
        if (employees.containsKey(id)) {
            employees.remove(id);
            empIds.remove(id);
            System.out.println("✅ Deleted Successfully");
        } else {
            System.out.println("❌ Employee Not Found");
        }
    }

    // ================= SEARCH =================
    public void searchEmployee(int id) {
        Employee e = employees.get(id);
        if (e != null) e.display();
        else System.out.println("❌ Employee Not Found");
    }

    // ================= DISPLAY =================
    public void displayAll() {
        if (employees.isEmpty()) {
            System.out.println("No records found.");
            return;
        }
        System.out.println("\nID\tName\tSalary\tType");
        System.out.println("--------------------------------");
        for (Employee e : employees.values()) {
            String type = (e instanceof FullTimeEmployee) ? "Full-Time" : "Part-Time";
            System.out.println(e.id + "\t" + e.name + "\t" + e.salary + "\t" + type);
        }
    }

    // ================= INNER CLASS =================
    class Payroll {
        public double calculate(Employee e) {
            return e.salary;
        }
    }

    // ================= FILE SAVE =================
    public void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Employee e : employees.values()) {
                String type = (e instanceof FullTimeEmployee) ? "F" : "P";
                bw.write(e.id + "," + e.name + "," + e.salary + "," + type);
                bw.newLine();
            }
            System.out.println("💾 Data Saved to File");
        } catch (IOException e) {
            System.out.println("Error saving file");
        }
    }

    // ================= FILE LOAD =================
    public void loadFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                int id = Integer.parseInt(data[0]);
                String name = data[1];
                double salary = Double.parseDouble(data[2]);
                String type = data[3];

                Employee e;
                if (type.equals("F"))
                    e = new FullTimeEmployee(id, name, salary);
                else
                    e = new PartTimeEmployee(id, name, salary);

                employees.put(id, e);
                empIds.add(id);
            }
            System.out.println("📂 Data Loaded from File");
        } catch (IOException e) {
            System.out.println("No previous data found.");
        }
    }

    // ================= THREAD CLASS =================
    class UpdateThread extends Thread {
        int id;
        double salary;

        UpdateThread(int id, double salary) {
            this.id = id;
            this.salary = salary;
        }

        public void run() {
            updateEmployee(id, salary);
        }
    }

    public void applyBonus(double percent) {
    for (Employee e : employees.values()) {
        double bonus = e.salary * percent / 100;
        e.salary += bonus;
    }
    System.out.println("✅ Bonus Applied to All Employees");
}

    public void sortBySalary() {
    List<Employee> list = new ArrayList<>(employees.values());

    list.sort((a, b) -> Double.compare(b.salary, a.salary));

    System.out.println("\nSorted by Salary (High → Low):");
    for (Employee e : list) {
        e.display();
    }
}

public Employee getEmployee(int id) {
    return employees.get(id);
}

public Collection<Employee> getAllEmployees() {
    return employees.values();
}

    // ================= MAIN MENU =================
    public static void main(String[] args) {

        EmployeeManager manager = new EmployeeManager();
        Scanner sc = new Scanner(System.in);

        manager.loadFromFile();

        while (true) {
            System.out.println("\n===== Employee Management System =====");
            System.out.println("1. Add Employee");
            System.out.println("2. Update Employee");
            System.out.println("3. Delete Employee");
            System.out.println("4. Search Employee");
            System.out.println("5. Display All");
            System.out.println("6. Save & Exit");
            System.out.println("7. Apply Bonus");
            System.out.println("8. Sort by Salary");

            int choice = sc.nextInt();

            switch (choice) {

                case 1:
                    System.out.print("Enter ID: ");
                    int id = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Enter Name: ");
                    String name = sc.nextLine();

                    System.out.print("Enter Salary: ");
                    double salary = sc.nextDouble();

                    System.out.println("1. Full-Time  2. Part-Time");
                    int type = sc.nextInt();

                    if (type == 1)
                        manager.addEmployee(new FullTimeEmployee(id, name, salary));
                    else
                        manager.addEmployee(new PartTimeEmployee(id, name, salary));
                    break;

                case 2:
                    System.out.print("Enter ID: ");
                    int uid = sc.nextInt();
                    System.out.print("Enter New Salary: ");
                    double sal = sc.nextDouble();

                    // Thread used here
                    Thread t = manager.new UpdateThread(uid, sal);
                    t.start();
                    break;

                case 3:
                    System.out.print("Enter ID: ");
                    manager.deleteEmployee(sc.nextInt());
                    break;

                case 4:
                    System.out.print("Enter ID: ");
                    manager.searchEmployee(sc.nextInt());
                    break;

                case 5:
                    manager.displayAll();
                    break;

                case 6:
                    manager.saveToFile();
                    System.out.println("Exiting...");
                    return;

                case 7:
                    System.out.print("Enter bonus %: ");
                    double p = sc.nextDouble();
                    manager.applyBonus(p);
                    break;

                case 8:
                    manager.sortBySalary();
                    break;

                default:
                    System.out.println("Invalid choice");
            }
        }
    }
}