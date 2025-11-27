package project.mapper;

import lombok.RequiredArgsConstructor;
import project.model.Characteristics;
import project.model.create.CharacteristicsCreateDto;
import project.model.dto.CharacteristicsDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CharacteristicsMapper {
    final ProductMapper productMapper;
    public Characteristics fromCreateDto(CharacteristicsCreateDto dto) {
        Characteristics characteristics = new Characteristics();
        characteristics.setName(dto.getName());
        characteristics.setPrice(dto.getPrice());
        characteristics.setProduct(dto.getProduct());
        return characteristics;
    }

    public CharacteristicsDto toDto(Characteristics save) {
        CharacteristicsDto characteristicsDto = new CharacteristicsDto();
        characteristicsDto.setId(save.getId());
        characteristicsDto.setName(save.getName());
        characteristicsDto.setPrice(save.getPrice());
        characteristicsDto.setProduct(productMapper.toDto(save.getProduct()));
        return characteristicsDto;
    }

    public List<CharacteristicsDto> toDtoList(List<Characteristics> all) {
        return all.stream().map(this::toDto).collect(Collectors.toList());
    }
}
