package in.ac.adit.pwj.miniproject.employees;

import java.io.Serializable;

public class Employee implements Serializable {
    protected int id;
    protected String name;
    protected double salary;

    public Employee(int id, String name, double salary) {
        this.id = id;
        this.name = name;
        this.salary = salary;
    }

    public void display() {
        System.out.println(id + " " + name + " " + salary);
    }
}