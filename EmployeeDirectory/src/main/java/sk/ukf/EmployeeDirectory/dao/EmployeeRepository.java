//package sk.ukf.EmployeeDirectory.dao;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.TypedQuery;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Repository;
//import sk.ukf.EmployeeDirectory.entity.Employee;
//
//import java.util.List;
//
//@Repository
//public class EmploeeDAOJpaImpl implements EmployeeDAO {
//    private EntityManager entityManager;
//
//    @Autowired
//    public EmploeeDAOJpaImpl(EntityManager entityManager) {
//        this.entityManager = entityManager;
//    }
//    @Override
//    public List<Employee> findAll() {
//        TypedQuery<Employee> query = entityManager.createQuery("from Employee ", Employee.class);
//        List<Employee> employees = query.getResultList();
//        return employees;
//    }
//
//    @Override
//    public Employee findById(int id) {
//        Employee employee = entityManager.find(Employee.class, id);
//        return employee;
//    }
//
//    @Override
//    public Employee save(Employee employee) {
//        Employee employee_db = entityManager.merge(employee);
//        return employee_db;
//    }
//
//    @Override
//    public void deleteById(int id) {
//        Employee employee = entityManager.find(Employee.class, id);
//        entityManager.remove(employee);
//    }
//
//    @Override
//    public List<Employee> findByFirstNameAndLastName(String firstname, String lastname) {
//        TypedQuery<Employee> query = entityManager.createQuery("from Employee where firstName=:firstName and lastName=:lastName", Employee.class);
//        query.setParameter("firstName", firstname);
//        query.setParameter("lastName", lastname);
//        List<Employee> employees = query.getResultList();
//        return employees;
//    }
//}

package sk.ukf.EmployeeDirectory.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sk.ukf.EmployeeDirectory.entity.Employee;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    List<Employee> findByFirstNameAndLastName(String firstName, String lastName);

    boolean existsByEmail(String email);

    Optional<Employee> findByEmail(String email);

    List<Employee> findAllByOrderByLastNameAsc();
}
