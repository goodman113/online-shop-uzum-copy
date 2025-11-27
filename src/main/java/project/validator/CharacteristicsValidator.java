package project.validator;

import lombok.RequiredArgsConstructor;
import project.model.create.CharacteristicsCreateDto;
import org.springframework.stereotype.Component;
import project.repository.repository.CharacteristicsRepository;

@Component
@RequiredArgsConstructor
public class CharacteristicsValidator {
    final CharacteristicsRepository characteristicsRepository;
    public void onCreate(CharacteristicsCreateDto dto) {
        if (dto.getName().isBlank()){
            throw new RuntimeException("name of characteristic can't be empty");
        }
        boolean b = characteristicsRepository.existsByName(dto.getName());
        if (b){
            throw new RuntimeException("name of characteristic already exists");
        }
    }
}
