package project.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import project.config.VendorUpdateWebSocketHandler;
import project.mapper.*;
import project.model.*;
import project.model.create.CategoryCreateDto;
import project.model.create.SubCategoryCreateDto;
import project.model.create.SubSubCategoryCreateDto;
import project.model.dto.CategoryDto;
import project.model.dto.SubCategoryDto;
import project.model.dto.SubSubCategoryDto;
import project.model.dto.UserDto;
import project.model.enums.OrderStatus;
import project.model.enums.Role;
import project.repository.repository.*;
import project.service.CategoryService;
import project.service.SubCategoryService;
import project.service.SubSubCategoryService;
import project.service.VendorService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAnyRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {
    final VendorService vendorService;
    final UserRepository userRepository;
    final UserMapper userMapper;
    final VendorRepository vendorRepository;
    final VendorMapper vendorMapper;
    final CategoryRepository categoryRepository;
    final CategoryMapper categoryMapper;
    final SubSubCategoryRepository subSubCategoryRepository;
    private final VendorUpdateWebSocketHandler webSocketHandler;
    final SubSubCategoryMapper subSubCategoryMapper;
    final SubCategoryRepository subCategoryRepository;
    final SubCategoryMapper subCategoryMapper;
    final SubCategoryService subCategoryService;
    final CategoryService categoryService;
    final SubSubCategoryService  subSubCategoryService;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model,HttpServletRequest request) {
        DashboardStats stats = new DashboardStats();
        stats.setTotalVendors(vendorRepository.count());
        stats.setPendingVendors(vendorRepository.countVendorsByApproved(false));
        stats.setApprovedVendors(vendorRepository.countVendorsByApproved(true));
        stats.setTotalUsers(userRepository.countUsersByRoleCustomer(Role.CUSTOMER));
        List<ActivityLog> logs = vendorRepository.findTop5ByOrderByUpdatedAtDesc()
                .stream()
                .map(v -> new ActivityLog(
                        v.isApproved() ? "Approved" : "Rejected",
                        v.getShopName(),
                        v.getUpdatedAt()
                ))
                .collect(Collectors.toList());
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        model.addAttribute("stats", stats);
        model.addAttribute("recentLogs", logs);
        List<Long> growth = vendorRepository.countNewVendorsLast7Days(weekAgo);
        model.addAttribute("vendorGrowth", growth);
        model.addAttribute("roleStats", new RoleStats(
                userRepository.countUsersByRoleCustomer(Role.CUSTOMER),
                vendorRepository.count(),
                userRepository.countUsersByRole(Role.ADMIN)));
        model.addAttribute("currentPage",
                GlobalControllerAdvice.populateCurrentPage(request));
        return "admin/admin_dashboard";
    }

    @GetMapping("/orders")
    public String orders(@RequestParam(required = false) String search,
                         @RequestParam(required = false) String status,
                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFrom,
                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateTo,
                         @PageableDefault(size = 20, sort = {"createdAt", "desc"}) Pageable pageable,
                         Model model,HttpServletRequest request) {
        LocalDateTime fromDateTime = dateFrom != null ?
                dateFrom.atStartOfDay() : null;
        LocalDateTime toDateTime = dateTo != null ?
                dateTo.atTime(23, 59, 59) : null;
        List<Order> all = orderRepository.findAll(fromDateTime, toDateTime);
        long count = all.stream().filter(order -> order.getStatus().equals(OrderStatus.PENDING)).count();
        double sum = all.stream().mapToDouble(Order::getTotalPrice).sum();
        model.addAttribute("totalRevenue", sum);
        model.addAttribute("pendingCount", count);
        model.addAttribute("searchQuery", search);
        model.addAttribute("statusFilter", status);
        model.addAttribute("dateFrom", dateFrom);
        model.addAttribute("dateTo", dateTo);
        model.addAttribute("orders", all);
        model.addAttribute("currentWebPage", GlobalControllerAdvice.populateCurrentPage(request));
        return "admin/orders";
    }


    @PostMapping("/orders/{orderId}/taken")
    @ResponseBody
    public Map<String, Object> markAsTaken(@PathVariable Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        if (order.getStatus() == OrderStatus.READY_FOR_PICKUP || order.getStatus() == OrderStatus.UNPAID) {

            order.setStatus(OrderStatus.TAKEN_BY_CUSTOMER);
            orderRepository.save(order);

            return Map.of("success", true);
        }
        return Map.of("Fail",false);
    }


    @GetMapping("/vendor_accept")
    public String vendorAccept(Model model,HttpServletRequest request) {
        List<UserDto> userDtos = userMapper.toDtoList(userRepository.findAllByVendorProfile_Approved(false));
        userDtos.forEach(u -> System.out.println("User: " + u.getUsername() + ", Email: " + u.getEmail()));
        model.addAttribute("userDtos", userDtos);
        List<User> all = userRepository.findAll();
        model.addAttribute("users", all);
        model.addAttribute("currentPage",
                GlobalControllerAdvice.populateCurrentPage(request));
        return "admin/vendor_accept";
    }

    @GetMapping("/vendor/{id}")
    public String vendor(Model model, @PathVariable Long id, HttpServletRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("userDto", userMapper.toDto(user,null,null));
        model.addAttribute("currentWebPage",
                GlobalControllerAdvice.populateCurrentPage(request));
        List<Product> productsByVendorId = productRepository.findProductsByVendor_Id(user.getVendorProfile().getId());
        long sum = productsByVendorId.stream().mapToLong(Product::getSoldQuantity).sum();
        model.addAttribute("totalSold",sum);
        return "admin/vendor";
    }

    @GetMapping("/vendors")
    public String vendors(@RequestParam(defaultValue = "0")int page,
                          @RequestParam(required = false)String search,
                          Model model,
                          HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, 10,
                Sort.by("createdAt").descending());
        Page<User> userPage = search !=null ?
                userRepository.findByVendorProfile_ShopNameContainingIgnoreCase(search,pageable)
                : userRepository.findUsersByVendorProfile_Approved(true,pageable);
        model.addAttribute("users", userMapper.toDtoList(userPage.getContent()));
        model.addAttribute("currentPage",page);
        model.addAttribute("currentWebPage",
                GlobalControllerAdvice.populateCurrentPage(request));
        model.addAttribute("totalPages",userPage.getTotalPages());
        return "admin/vendors";

    }

    @PostMapping("/vendor/approve/{vendorId}")
    public String approveVendor(@PathVariable Long vendorId, RedirectAttributes ra) {
        try {
            Vendor vendor = vendorRepository.findById(vendorId)
                    .orElseThrow(() -> new IllegalArgumentException("Vendor not found"));
            vendor.setApproved(true);
            vendorRepository.save(vendor);

            ra.addFlashAttribute("successMessage",
                    "Vendor '" + vendor.getShopName() + "' has been approved.");

            // BROADCAST REAL-TIME
            webSocketHandler.broadcastUpdate(vendor, "APPROVED");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to approve vendor.");
        }
        return "redirect:/admin/vendor_accept";
    }

    @PostMapping("/vendor/reject/{vendorId}")
    public String rejectVendor(@PathVariable Long vendorId, RedirectAttributes ra) {
        try {
            Vendor vendor = vendorRepository.findById(vendorId)
                    .orElseThrow(() -> new IllegalArgumentException("Vendor not found"));
            String shopName = vendor.getShopName();
            User user = userRepository.findByVendorProfile_Id(vendorId)
                    .orElseThrow(() -> new RuntimeException("User with id " + vendorId
                            + " not found."));
            userRepository.delete(user);
            vendorRepository.delete(vendor); // or set a "rejected" flag

            ra.addFlashAttribute("successMessage",
                    "Vendor '" + shopName + "' has been rejected.");

            // BROADCAST REAL-TIME
            webSocketHandler.broadcastUpdate(vendor, "REJECTED");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to reject vendor.");
        }
        return "redirect:/admin/vendor_accept";
    }

    @GetMapping("/categories")
    public String categories(Model model, HttpServletRequest request) {

        model.addAttribute("currentWebPage",
                GlobalControllerAdvice.populateCurrentPage(request));
        Map<CategoryDto, List<SubCategoryDto>> allWithSubCategories = categoryService.getAllWithSubCategories();
        model.addAttribute("subsubCategories",subSubCategoryRepository.findAll());
        model.addAttribute("categoryMap", allWithSubCategories);
        return "category/categories";
    }

    // Add category
    @PostMapping("/category")
    @ResponseBody
    public CategoryDto addCategory(@RequestBody CategoryCreateDto dto) {
        return categoryService.create(dto);
    }

    // Add subcategory
    @PostMapping("/category/{catId}/sub")
    @ResponseBody
    public SubCategoryDto addSub(@PathVariable Long catId,
                                 @RequestBody SubCategoryCreateDto dto) {
        categoryRepository.findById(catId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        System.out.println("Category Id: " + catId);
        System.out.println("subCategory-->" + dto);
        dto.setCategoryId(catId);
        return subCategoryService.create(dto);
    }

    @PostMapping("/sub/{subId}/subsub")
    @ResponseBody
    public SubSubCategoryDto addSubSub(@PathVariable Long subId,
                                       @RequestBody SubSubCategoryCreateDto dto) {
        SubCategory subCategory = subCategoryRepository.findById(subId).orElseThrow(() -> new IllegalArgumentException("Sub-category not found"));
        dto.setSubCategoryId(subCategory.getId());
        return subSubCategoryService.create(dto);
    }
}
