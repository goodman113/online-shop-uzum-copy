package project.service;

import jakarta.persistence.EntityNotFoundException;
import project.mapper.ReviewMapper;
import project.model.Product;
import project.model.Review;
import project.model.create.ReviewCreateDto;
import project.model.dto.ReviewDto;
import org.springframework.stereotype.Service;
import project.repository.repository.ReviewRepository;
import project.validator.ReviewValidator;

import java.util.List;

@Service
public class ReviewService  extends AbstractService
        <ReviewRepository, ReviewMapper, ReviewValidator>
        implements CrudService<ReviewDto, ReviewCreateDto, ReviewDto, Long> {


    protected ReviewService(ReviewRepository repository, ReviewMapper mapper, ReviewValidator validator) {
        super(repository, mapper, validator);
    }

    @Override
    public ReviewDto create(ReviewCreateDto dto) {
        validator.onCreateDto(dto);
        Review review = mapper.fromCreateDto(dto);
        return mapper.toDto(repository.save(review));
    }

    @Override
    public ReviewDto get(Long id) {
        Review review = repository.findReviewByIdIsAndDeleted(id,false).orElseThrow(() -> new EntityNotFoundException("Review not found"));
        return mapper.toDto(review);

    }

    @Override
    public List<ReviewDto> getAll(String search) {
        return mapper.toDtoList(repository.findAll());
    }

    @Override
    public ReviewDto update(ReviewDto dto) {
        return null;
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<Review> getAllReviewForProduct(Product product) {
        return repository.getReviewsByProduct(product);
    }
}
