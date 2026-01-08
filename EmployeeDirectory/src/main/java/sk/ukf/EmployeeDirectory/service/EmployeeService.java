package sk.ukf.EmployeeDirectory.service;

import sk.ukf.EmployeeDirectory.entity.Employee;

import java.util.List;

public interface EmployeeService {

    List<Employee> findAll();

    Employee findById(int id);

    Employee save(Employee student);

    void deleteById(int id);

    List<Employee> findByFirstNameAndLastName(String firstName, String lastName);

    List<Employee> search(String query);

    List<Employee> sortBy(String by);

}