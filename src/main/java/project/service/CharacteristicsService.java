package project.service;

import project.mapper.CharacteristicsMapper;
import project.model.Characteristics;
import project.model.create.CharacteristicsCreateDto;
import project.model.dto.CharacteristicsDto;
import org.springframework.stereotype.Service;
import project.repository.repository.CharacteristicsRepository;
import project.validator.CharacteristicsValidator;

import java.util.List;

@Service
public class CharacteristicsService extends AbstractService
        <CharacteristicsRepository,
                CharacteristicsMapper,
                CharacteristicsValidator> implements CrudService<
        CharacteristicsDto, CharacteristicsCreateDto, CharacteristicsDto, Long> {

    protected CharacteristicsService(CharacteristicsRepository repository, CharacteristicsMapper mapper, CharacteristicsValidator validator) {
        super(repository, mapper, validator);
    }

    @Override
    public CharacteristicsDto create(CharacteristicsCreateDto dto) {
        validator.onCreate(dto);
        Characteristics characteristics = mapper.fromCreateDto(dto);
        return mapper.toDto(repository.save(characteristics));
    }

    @Override
    public CharacteristicsDto get(Long id) {
        Characteristics characteristics = repository.findUserByIdIsAndDeleted(id,false).orElseThrow(() -> new RuntimeException("Characteristic not found"));
        return mapper.toDto(characteristics);
    }

    @Override
    public List<CharacteristicsDto> getAll(String search) {
        return mapper.toDtoList(repository.findAll(search));
    }

    @Override
    public CharacteristicsDto update(CharacteristicsDto dto) {
        return null;
    }

    @Override
    public void delete(Long id) {
        boolean b = repository.existsById(id);
        if (!b) {
            throw new RuntimeException("Characteristic not found");
        }
        repository.deleteById(id);
    }
}
