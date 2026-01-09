package sk.ukf.PizzaDirectory.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.ukf.PizzaDirectory.entity.PizzaSize;
import sk.ukf.PizzaDirectory.exception.ResourceNotFoundException;
import sk.ukf.PizzaDirectory.repository.SizeRepository;

import java.util.List;

@Service
@Transactional
public class SizeService {

    private final SizeRepository sizeRepository;

    public SizeService(SizeRepository sizeRepository) {
        this.sizeRepository = sizeRepository;
    }

    @Transactional(readOnly = true)
    public List<PizzaSize> findAll() {
        return sizeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public PizzaSize findById(Integer id) {
        return sizeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Size", id));
    }

    public PizzaSize save(PizzaSize size) {
        return sizeRepository.save(size);
    }

    public void deleteById(Integer id) {
        if (!sizeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Size", id);
        }
        sizeRepository.deleteById(id);
    }
}

