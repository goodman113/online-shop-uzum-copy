package project.service;

import jakarta.transaction.Transactional;
import project.mapper.ProductMapper;
import project.mapper.VendorMapper;
import project.model.Vendor;
import project.model.create.VendorCreateDto;
import project.model.dto.VendorDto;
import org.springframework.stereotype.Service;
import project.repository.repository.ProductRepository;
import project.repository.repository.VendorRepository;
import project.validator.VendorValidator;

import java.util.List;

@Service
public class VendorService  extends AbstractService
        <VendorRepository, VendorMapper, VendorValidator>
        implements CrudService<VendorDto, VendorCreateDto, VendorDto, Long> {
    final ProductMapper productMapper;
    final ProductRepository productRepository;
    protected VendorService(VendorRepository repository, VendorMapper mapper, VendorValidator validator, ProductMapper productMapper, ProductRepository productRepository) {
        super(repository, mapper, validator);
        this.productMapper = productMapper;
        this.productRepository = productRepository;
    }

    @Override
    public VendorDto create(VendorCreateDto dto) {
        Vendor vendor = mapper.fromCreateDto(dto);
        return mapper.toDto(repository.save(vendor));
    }

    @Override
    public VendorDto get(Long id) {
        Vendor vendor = repository.findUserByIdIsAndDeleted(id,false).orElseThrow(() -> new RuntimeException("vendor not found!"));
        VendorDto vendorDto = mapper.toDto(vendor);
        vendorDto.setProducts(productMapper.toDtoList(productRepository.findProductsByVendor(vendor)));
        return vendorDto;
    }

    @Override
    public List<VendorDto> getAll(String search) {
        return List.of();
    }

    @Override
    public VendorDto update(VendorDto dto) {
        return null;
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
