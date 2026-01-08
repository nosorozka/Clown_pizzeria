package sk.ukf.EmployeeDirectory.rest;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.ukf.EmployeeDirectory.dto.ApiResponse;
import sk.ukf.EmployeeDirectory.entity.Employee;
import sk.ukf.EmployeeDirectory.service.EmployeeService;


import java.util.List;

@RestController
@RequestMapping("/api")
public class EmployeeRestController {
    private EmployeeService employeeService;

    @Autowired
    public EmployeeRestController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

//    @GetMapping("/employees")
//    public List<Employee> findAll() {
//        return employeeService.findAll();
//    }
    @GetMapping("/employees")
    public ResponseEntity<ApiResponse<List<Employee>>> findAll() {
        List<Employee> employees = employeeService.findAll();
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(employees,"Zoznam zamestnancov: "));
    }

//    @GetMapping("/employees/{id}")
//    public Employee getEmployee(@PathVariable int id) {
//
//        Employee employee = employeeService.findById(id);
//
//        if (employee == null) {
//            throw new RuntimeException("Employee id not found - " + id);
//        }
//
//        return employee;
//    }
    @GetMapping("/employees/{id}")
    public ResponseEntity<ApiResponse<Employee>> findById(@PathVariable int id) {
        Employee employee = employeeService.findById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(employee, "Zamestnanec bol nájdený"));
    }

//    @PostMapping("/employees")
//    public Employee addEmployee(@RequestBody Employee employee) {
//        employee.setId(0);
//        Employee employee_db = employeeService.save(employee);
//        return employee_db;
//    }
    @PostMapping("/employees")
    public ResponseEntity<ApiResponse<Employee>> save(@RequestBody @Valid Employee employee) {
        Employee employeeSave =  employeeService.save(employee);
        return ResponseEntity.status(HttpStatus.CREATED).
                body(ApiResponse.success(employeeSave,"Zamestnanec úspešne vytvorený"));
    }

//    @PutMapping("/employees/{id}")
//    public Employee updateEmployee(@PathVariable int id, @RequestBody Employee employee) {
//        Employee existingEmployee = employeeService.findById(id);
//        if (existingEmployee == null) {
//            throw new RuntimeException("Employee id not found - " + id);
//        }
//        employee.setId(id);
//        Employee updatedEmployee = employeeService.save(employee);
//        return updatedEmployee;
//    }
    @PutMapping("/employees/{id}")
    public ResponseEntity<ApiResponse<Employee>> updateEmployee(@PathVariable int id, @Valid @RequestBody Employee employee) {

        employeeService.findById(id);

        employee.setId(id);
        Employee updatedEmployee = employeeService.save(employee);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(updatedEmployee, "Zamestnanec úspešne aktualizovaný"));
    }

//    @DeleteMapping("/employees/{id}")
//    public String deleteEmployee(@PathVariable int id) {
//
//        Employee employee = employeeService.findById(id);
//
//        if (employee == null) {
//            throw new RuntimeException("Employee id not found - " + id);
//        }
//
//        employeeService.deleteById(id);
//
//        return "Deleted employee id - " + id;
//    }
    @DeleteMapping("/employees/{id}")
    public ResponseEntity<ApiResponse<Employee>> delete(@PathVariable int id) {
        Employee existingEmployee = employeeService.findById(id);
        if (existingEmployee != null) {
            employeeService.deleteById(id); // реально удаляем
        }
        return ResponseEntity
                .status(HttpStatus.OK).
                body(ApiResponse.success(null,"Zamestnanec úspešne odstránený"));
    }

//    @GetMapping("/employeeSearch")
//    public List<Employee> search(@RequestParam String firstName, @RequestParam String lastName) {
//        return employeeService.findByFirstNameAndLastName(firstName, lastName);
//    }
    @GetMapping("/employees/search")
    public ResponseEntity<ApiResponse<List<Employee>>> searchEmployees(@RequestParam String query) {
        List<Employee> students = employeeService.search(query);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(students, "Výsledky vyhľadávania"));
    }

    @GetMapping("/employees/sort")
    public ResponseEntity<ApiResponse<List<Employee>>> sortEmployees(@RequestParam String by) {
        List<Employee> students = employeeService.sortBy(by);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(students, "Zoznam zamestnancov zoradený podľa " + by));
    }
}