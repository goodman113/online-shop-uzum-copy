package project.service;

import project.mapper.BrandMapper;
import project.model.Brand;
import project.model.create.BrandCreateDto;
import project.model.dto.BrandDto;
import org.springframework.stereotype.Service;
import project.repository.repository.BrandRepository;
import project.repository.repository.ProductRepository;
import project.validator.BrandValidator;

import java.util.List;
@Service
public class BrandService extends
        AbstractService<BrandRepository,
                BrandMapper, BrandValidator> implements CrudService<BrandDto, BrandCreateDto,BrandDto,Long> {
    final ProductRepository productRepository;

    protected BrandService(BrandRepository repository, BrandMapper mapper, BrandValidator validator, ProductRepository productRepository) {
        super(repository, mapper, validator);
        this.productRepository = productRepository;
    }

    @Override
    public BrandDto create(BrandCreateDto dto) {
        validator.onCreate(dto);
        Brand brand = mapper.fromCreateDto(dto);
        return mapper.toDto(repository.save(brand));
    }

    @Override
    public BrandDto get(Long id) {
        Brand brand = repository.findUserByIdIsAndDeleted(id,false).orElseThrow(() -> new RuntimeException("brand not found"));
        return mapper.toDto(brand);
    }

    @Override
    public List<BrandDto> getAll(String search) {
        return mapper.toDtoList(repository.findAllByDeleted(false));
    }

    @Override
    public BrandDto update(BrandDto dto) {
        return null;
    }

    @Override
    public void delete(Long id) {
        productRepository.findAllProductsByBrandId(id);
        repository.deleteById(id);
    }
}
