package in.ac.adit.pwj.miniproject.employees;

public class FullTimeEmployee extends Employee {

    public FullTimeEmployee(int id, String name, double salary) {
        super(id, name, salary);
    }

    @Override
    public void display() {
        System.out.println("Full-Time Employee:");
        super.display();
    }
}