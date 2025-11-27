package project.service;

import lombok.RequiredArgsConstructor;
import project.mapper.ProductMapper;
import project.model.Image;
import project.model.Product;
import project.model.User;
import project.model.create.ProductCreateDto;
import project.model.dto.ProductDto;
import project.model.update.ProductUpdateDto;
import org.springframework.stereotype.Service;
import project.repository.repository.ProductRepository;
import project.validator.ProductValidator;

import java.util.List;
import java.util.Map;

@Service
public class ProductService  extends AbstractService
        <ProductRepository, ProductMapper, ProductValidator>
        implements CrudService<ProductDto, ProductCreateDto, ProductUpdateDto, Long> {
    final OrderItemService orderItemService;
    protected ProductService(ProductRepository repository, ProductMapper mapper, ProductValidator validator, OrderItemService orderItemService) {
        super(repository, mapper, validator);
        this.orderItemService = orderItemService;
    }

    @Override
    public ProductDto create(ProductCreateDto dto) {
        validator.onCreate(dto);
        Product product = mapper.fromCreateDto(dto);
        return mapper.toDto(repository.save(product));
    }

    @Override
    public ProductDto get(Long id) {
        Product product = repository.findProductByIdIsAndDeleted(id,false).orElseThrow(() -> new RuntimeException("product was not found"));
        return mapper.toDto(product);
    }

    @Override
    public List<ProductDto> getAll(String search) {
        return List.of();
    }

    public List<ProductDto> getAll(String name, String description, Long vendorId,String categoryName, Double priceFrom, Double priceTo) {
        return mapper.toDtoList(repository.findAll(name, description,vendorId,categoryName,priceFrom,priceTo));
    }

    @Override
    public ProductDto update(ProductUpdateDto dto) {
        Product  product = repository.findById(dto.getId()).orElseThrow(() -> new RuntimeException("product was not found"));
        product.setPrice(dto.getPrice());
        product.setOldPrice(dto.getOldPrice());
        product.setStockQuantity(product.getStockQuantity()+dto.getStockQuantity());
        return mapper.toDto(repository.save(product));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }




}
