package sk.ukf.EmployeeDirectory.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.ukf.EmployeeDirectory.dao.EmployeeRepository;
import sk.ukf.EmployeeDirectory.entity.Employee;
import sk.ukf.EmployeeDirectory.exception.EmailAlreadyExistsException;
import sk.ukf.EmployeeDirectory.exception.ObjectNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private EmployeeRepository employeeRepository ;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository  = employeeRepository ;
    }

    @Override
    public List<Employee> findAll() {
        return employeeRepository .findAll();
    }

//    @Override
//    public Employee findById(int id) {
//        return employeeRepository .findById(id)
//                .orElse(null);
//    }
    @Override
    public Employee findById(int id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Zamestnanec", id));
    }

//    @Transactional
//    @Override
//    public Employee save(Employee employee) {
//        return employeeRepository .save(employee);
//    }
    @Transactional
    @Override
    public Employee save(Employee employee) {
        if (employee.getId() == 0) {
            if (employeeRepository.existsByEmail(employee.getEmail())) {
                throw new EmailAlreadyExistsException(employee.getEmail());
            }
        } else {
            Employee existingWithEmail = employeeRepository.findByEmail(employee.getEmail()).orElse(null);
            if (existingWithEmail != null && existingWithEmail.getId() != employee.getId()) {
                throw new EmailAlreadyExistsException(employee.getEmail());
            }
        }

        return employeeRepository.save(employee);
    }


//    @Transactional
//    @Override
//    public void deleteById(int id) {
//        employeeRepository .deleteById(id);
//    }
@Transactional
@Override
public void deleteById(int id) {
    if (!employeeRepository.existsById(id)) {
        throw new ObjectNotFoundException("Zamestnanec ", id);
    }
    employeeRepository.deleteById(id);
}

    @Override
    public List<Employee> findByFirstNameAndLastName(String firstName, String lastname) {
        return  employeeRepository.findByFirstNameAndLastName(firstName, lastname);
    }

    @Override
    public List<Employee> search(String query) {
        return findAll().stream()
                .filter(e -> e.getFirstName().toLowerCase().contains(query.toLowerCase())
                        || e.getLastName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Employee> sortBy(String by) {
        return employeeRepository.findAll(Sort.by(Sort.Direction.ASC, by));
    }

//    @Override
//    public List<Employee> getSortedEmployees() {
//        return employeeRepository.findAllByOrderByLastNameAsc();
//    }

}