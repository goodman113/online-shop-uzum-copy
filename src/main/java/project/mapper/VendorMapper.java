package project.mapper;

import lombok.RequiredArgsConstructor;
import project.model.Vendor;
import project.model.create.VendorCreateDto;
import project.model.dto.VendorDto;
import org.springframework.stereotype.Component;
import project.repository.repository.ProductRepository;

@Component
@RequiredArgsConstructor
public class VendorMapper {
    public Vendor fromCreateDto(VendorCreateDto dto) {
        if (dto == null) {
            return null;
        }
        Vendor vendor = new Vendor();
        vendor.setApproved(false);
        vendor.setDescription(dto.getDescription());
        vendor.setShopName(dto.getShopName());
        return vendor;
    }

    public Vendor fromDto(VendorDto vendorProfile) {
        if (vendorProfile == null) {
            return null;
        }
        Vendor vendorDto = new Vendor();
        vendorDto.setId(vendorProfile.getId());
        vendorDto.setApproved(vendorProfile.isApproved());
        vendorDto.setDescription(vendorProfile.getDescription());
        vendorDto.setShopName(vendorProfile.getShopName());
        return vendorDto;
    }

    public VendorDto toDto(Vendor save) {
        VendorDto vendorDto = new VendorDto();
        vendorDto.setId(save.getId());
        vendorDto.setApproved(save.isApproved());
        vendorDto.setDescription(save.getDescription());
        vendorDto.setShopName(save.getShopName());
        return vendorDto;
    }
}
