package project.mapper;

import lombok.RequiredArgsConstructor;
import project.model.Notification;
import project.model.create.NotificationCreateDto;
import project.model.dto.NotificationDto;
import org.springframework.stereotype.Component;
import project.repository.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NotificationMapper {
    final UserRepository userRepository;
    final UserMapper userMapper;
    public Notification fromCreateDto(NotificationCreateDto dto) {
        Notification notification= new Notification();
        notification.setMessage(dto.getMessage());
        userRepository.findById(dto.getReceiverId()).ifPresent(notification::setReceiver);
        return notification;
    }

    public NotificationDto toDto(Notification save) {
        NotificationDto notificationDto= new NotificationDto();
        notificationDto.setMessage(save.getMessage());
        notificationDto.setId( save.getId());
        notificationDto.setRead(save.isRead());
        notificationDto.setCreatedAt( save.getCreatedAt());
        notificationDto.setReceiver(userMapper.toDto(save.getReceiver(),null,null));
        return notificationDto;
    }

    public List<NotificationDto> toDtoList(List<Notification> all) {
        return  all.stream().map(this::toDto).collect(Collectors.toList());
    }
}
