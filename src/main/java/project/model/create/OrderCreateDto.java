package project.model.create;

import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class OrderCreateDto {
    private Double totalPrice;
    private Long buyerId;
    private List<Long> orderItemIds = new ArrayList<>();
}
