package project.service;

import org.springframework.stereotype.Service;
import project.mapper.SubCategoryMapper;
import project.mapper.SubSubCategoryMapper;
import project.model.SubSubCategory;
import project.model.create.SubSubCategoryCreateDto;
import project.model.dto.SubCategoryDto;
import project.model.dto.SubSubCategoryDto;
import project.repository.repository.ProductRepository;
import project.repository.repository.SubCategoryRepository;
import project.repository.repository.SubSubCategoryRepository;
import project.validator.SubSubCategoryValidator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SubSubCategoryService extends AbstractService<
        SubSubCategoryRepository,
        SubSubCategoryMapper, SubSubCategoryValidator>
        implements CrudService<SubSubCategoryDto, SubSubCategoryCreateDto,SubSubCategoryDto,Long>{
    final ProductRepository productRepository;
    final SubCategoryRepository subCategoryRepository;
    final SubCategoryMapper subCategoryMapper;
    protected SubSubCategoryService(SubSubCategoryRepository repository, SubSubCategoryMapper mapper, SubSubCategoryValidator validator, ProductRepository productRepository, SubCategoryRepository subCategoryRepository, SubCategoryMapper subCategoryMapper) {
        super(repository, mapper, validator);
        this.productRepository = productRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.subCategoryMapper = subCategoryMapper;
    }

    @Override
    public SubSubCategoryDto create(SubSubCategoryCreateDto dto) {
        validator.onCreate(dto);
        SubSubCategory subSubCategory = mapper.fromCreateDto(dto);
        Map<SubSubCategory, SubCategoryDto> dtoMap = new HashMap<>();
        dtoMap.put(repository.save(subSubCategory),subCategoryMapper.toDto(subCategoryRepository.findById
                (dto.getSubCategoryId()).orElseThrow(()-> new RuntimeException("sub Category not found"))));
        return mapper.toDto(dtoMap);
    }

    @Override
    public SubSubCategoryDto get(Long id) {
        SubSubCategory subSubCategory = repository.findSubSubCategoryByIdIsAndDeleted(id,false)
                .orElseThrow(() -> new RuntimeException("subCategory not found"));
        Map<SubSubCategory, SubCategoryDto> dtoMap = new HashMap<>();
        dtoMap.put(subSubCategory,subCategoryMapper.toDto(subCategoryRepository.findById
                (subSubCategory.getId()).orElseThrow(()-> new RuntimeException("sub Category not found"))));
        return mapper.toDto(dtoMap);
    }

    @Override
    public List<SubSubCategoryDto> getAll(String subCategoryId) {
        Map<SubCategoryDto, List<SubSubCategory>> map = new HashMap<>();
        map.put(subCategoryMapper.toDto(subCategoryRepository.findById
                (Long.valueOf(subCategoryId)).orElseThrow(()-> new RuntimeException("no sub category was found"))),
                repository.findAllBySubCategory_Id(Long.parseLong(subCategoryId)));
        return mapper.toDtoList(map);
    }

    @Override
    public SubSubCategoryDto update(SubSubCategoryDto dto) {
        return null;
    }

    @Override
    public void delete(Long id) {
        validator.onDelete(id);
        productRepository.deleteAllByCategory_Id(id);
        repository.deleteById(id);
    }
}
