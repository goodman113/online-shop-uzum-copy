package project.service;

import org.springframework.stereotype.Service;
import project.mapper.SubCategoryMapper;
import project.model.SubCategory;
import project.model.create.SubCategoryCreateDto;
import project.model.dto.SubCategoryDto;
import project.model.dto.SubSubCategoryDto;
import project.repository.repository.SubCategoryRepository;
import project.repository.repository.SubSubCategoryRepository;
import project.validator.SubCategoryValidator;

import java.util.List;

@Service
public class SubCategoryService extends AbstractService
        <SubCategoryRepository, SubCategoryMapper,
                SubCategoryValidator>
        implements CrudService<SubCategoryDto, SubCategoryCreateDto, SubCategoryDto,Long>{
    final SubSubCategoryRepository subSubCategoryRepository;
    final SubSubCategoryService subSubCategoryService;
    protected SubCategoryService(SubCategoryRepository repository, SubCategoryMapper mapper, SubCategoryValidator validator, SubSubCategoryRepository subSubCategoryRepository, SubSubCategoryService subSubCategoryService) {
        super(repository, mapper, validator);
        this.subSubCategoryRepository = subSubCategoryRepository;
        this.subSubCategoryService = subSubCategoryService;
    }

    @Override
    public SubCategoryDto create(SubCategoryCreateDto dto) {
        validator.onCreate(dto);
        SubCategory subCategory = mapper.fromCreateDto(dto);
        return mapper.toDto(repository.save(subCategory));
    }

    @Override
    public SubCategoryDto get(Long id) {
        return mapper.toDto(repository.findSubCategoryByIdIsAndDeleted(id,false)
                .orElseThrow(()-> new RuntimeException("Sub category not found!")));
    }

    @Override
    public List<SubCategoryDto> getAll(String categoryId) {
        return mapper.toDtoList(repository.findAllByCategory_Id(Long.valueOf((categoryId))));
    }

    @Override
    public SubCategoryDto update(SubCategoryDto dto) {
        return null;
    }

    @Override
    public void delete(Long id) {
        validator.onDelete(id);
        for (SubSubCategoryDto subSubCategoryDto : subSubCategoryService.getAll(String.valueOf(id))) {
            subSubCategoryService.delete(subSubCategoryDto.getId());
        }
        repository.deleteById(id);
    }
}
