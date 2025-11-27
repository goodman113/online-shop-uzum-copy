package project.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ActivityLog {
    String action;
    String shopName;
    LocalDateTime timestamp;
}