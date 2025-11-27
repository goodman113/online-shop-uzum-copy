    package project.model.dto;


    import lombok.*;

    import java.util.ArrayList;
    import java.util.List;

    @Setter
    @Getter
    @NoArgsConstructor
    @ToString
    @AllArgsConstructor
    public class VendorDto {
        private Long id;
        private String shopName;
        private String description;
        private boolean approved;
        private List<ProductDto> products = new ArrayList<>();
    }
