package project.service;

import project.mapper.NotificationMapper;
import project.model.Notification;
import project.model.create.NotificationCreateDto;
import project.model.dto.NotificationDto;
import org.springframework.stereotype.Service;
import project.repository.repository.NotificationRepository;
import project.validator.NotificationValidator;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService extends AbstractService
        <NotificationRepository, NotificationMapper, NotificationValidator>
        implements CrudService<NotificationDto, NotificationCreateDto,NotificationDto, Long>  {

    protected NotificationService(NotificationRepository repository, NotificationMapper mapper, NotificationValidator validator) {
        super(repository, mapper, validator);
    }

    @Override
    public NotificationDto create(NotificationCreateDto dto) {
        validator.onCreate(dto);
        Notification notification= mapper.fromCreateDto(dto);
        return mapper.toDto(repository.save(notification));
    }

    @Override
    public NotificationDto get(Long id) {
        Notification notification = repository.findUserByIdIsAndDeleted(id,false).orElseThrow(() -> new RuntimeException("notification not found"));
        return mapper.toDto(notification);

    }

    @Override
    public List<NotificationDto> getAll(String searchNoUse) {
        return List.of();
    }
    public List<NotificationDto> getAll(String search, LocalDateTime from, LocalDateTime to) {
        return mapper.toDtoList(repository.findAll(search,from,to));
    }

    @Override
    public NotificationDto update(NotificationDto dto) {
        return null;
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
