package project.mapper;

import lombok.RequiredArgsConstructor;
import project.model.Brand;
import project.model.create.BrandCreateDto;
import project.model.dto.BrandDto;
import org.springframework.stereotype.Component;
import project.repository.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BrandMapper {
    final ProductRepository productRepository;
    public Brand fromCreateDto(BrandCreateDto dto) {
        Brand brand = new Brand();
        brand.setName(dto.getName());
        return brand;
    }

    public BrandDto toDto(Brand save) {
        BrandDto brandDto = new BrandDto();
        brandDto.setId(save.getId());
        brandDto.setName(save.getName());
        return  brandDto;
    }

    public List<BrandDto> toDtoList(List<Brand> all) {
        return all.stream().map(this::toDto).collect(Collectors.toList());
    }
}
