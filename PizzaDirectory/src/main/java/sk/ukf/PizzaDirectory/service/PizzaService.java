package sk.ukf.PizzaDirectory.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.ukf.PizzaDirectory.entity.Pizza;
import sk.ukf.PizzaDirectory.exception.ResourceNotFoundException;
import sk.ukf.PizzaDirectory.repository.PizzaRepository;

import java.util.List;

@Service
@Transactional
public class PizzaService {

    private final PizzaRepository pizzaRepository;

    public PizzaService(PizzaRepository pizzaRepository) {
        this.pizzaRepository = pizzaRepository;
    }

    @Transactional(readOnly = true)
    public List<Pizza> findAll() {
        return pizzaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Pizza> findAllWithTags() {
        return pizzaRepository.findAllWithTags();
    }

    @Transactional(readOnly = true)
    public Pizza findById(Integer id) {
        return pizzaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pizza", id));
    }

    @Transactional(readOnly = true)
    public Pizza findByIdWithDetails(Integer id) {
        return pizzaRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pizza", id));
    }

    @Transactional(readOnly = true)
    public List<Pizza> findByTagId(Integer tagId) {
        return pizzaRepository.findByTagId(tagId);
    }

    @Transactional(readOnly = true)
    public List<Pizza> searchByName(String name) {
        return pizzaRepository.findByNameContainingIgnoreCase(name);
    }

    public Pizza save(Pizza pizza) {
        return pizzaRepository.save(pizza);
    }

    public void deleteById(Integer id) {
        if (!pizzaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pizza", id);
        }
        pizzaRepository.deleteById(id);
    }
}

