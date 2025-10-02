package vn.iostar.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import vn.iostar.entity.*;
import vn.iostar.repository.*;

import java.util.*;

@Controller
@RequiredArgsConstructor
@Transactional
@Validated
public class GraphqlController {

    private final UserRepository userRepo;
    private final CategoryRepository categoryRepo;
    private final ProductRepository productRepo;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @QueryMapping
    public List<Product> productsByPriceAsc() {
        return productRepo.findAllByOrderByPriceAsc();
    }

    @QueryMapping
    public List<Product> productsByCategory(@Argument Long categoryId) {
        return productRepo.findByCategoryViaUser(categoryId);
    }

    @QueryMapping
    public List<User> users() { return userRepo.findAll(); }

    @QueryMapping
    public List<Category> categories() { return categoryRepo.findAll(); }

    @QueryMapping
    public List<Product> products() { return productRepo.findAll(); }

    // ----------------- User CRUD -----------------
    @MutationMapping
    public User createUser(@Argument @Valid UserInput input) {
        if (userRepo.findByEmail(input.email()).isPresent()) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }
        User u = new User();
        applyUserInput(u, input, true);
        return userRepo.save(u);
    }

    @MutationMapping
    public User updateUser(@Argument Long id, @Argument @Valid UserUpdateInput input) {
        User u = userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        applyUserInput(u, input, false);
        return userRepo.save(u);
    }

    @MutationMapping
    public Boolean deleteUser(@Argument Long id) {
        if (!userRepo.existsById(id)) return false;
        userRepo.deleteById(id);
        return true;
    }

    private void applyUserInput(User u, BaseUserInput input, boolean isCreate) {
        if (input.fullname() != null) u.setFullname(input.fullname());
        if (input.email() != null) u.setEmail(input.email());
        if (input.password() != null) u.setPassword(passwordEncoder.encode(input.password())); // mã hoá
        if (input.phone() != null) u.setPhone(input.phone());
        if (input.role() != null) u.setRole(input.role());
        if (input.categoryIds() != null) {
            Set<Category> cats = new HashSet<>(categoryRepo.findAllById(input.categoryIds()));
            u.setCategories(cats);
        }
        if (isCreate && u.getRole() == null) u.setRole(Role.USER);
    }

    // ----------------- Category CRUD -----------------
    @MutationMapping
    public Category createCategory(@Argument @Valid CategoryInput input) {
        Category c = new Category();
        c.setName(input.name());
        c.setImages(input.images());
        return categoryRepo.save(c);
    }

    @MutationMapping
    public Category updateCategory(@Argument Long id, @Argument @Valid CategoryInput input) {
        Category c = categoryRepo.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        if (input.name() != null) c.setName(input.name());
        if (input.images() != null) c.setImages(input.images());
        return categoryRepo.save(c);
    }

    @MutationMapping
    public Boolean deleteCategory(@Argument Long id) {
        if (!categoryRepo.existsById(id)) return false;
        categoryRepo.deleteById(id);
        return true;
    }

    // ----------------- Product CRUD -----------------
    @MutationMapping
    public Product createProduct(@Argument @Valid ProductInput input) {
        Product p = new Product();
        applyProductInput(p, input);
        return productRepo.save(p);
    }

    @MutationMapping
    public Product updateProduct(@Argument Long id, @Argument @Valid ProductInput input) {
        Product p = productRepo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        applyProductInput(p, input);
        return productRepo.save(p);
    }

    @MutationMapping
    public Boolean deleteProduct(@Argument Long id) {
        if (!productRepo.existsById(id)) return false;
        productRepo.deleteById(id);
        return true;
    }

    private void applyProductInput(Product p, ProductInput input) {
        if (input.title() != null) p.setTitle(input.title());
        if (input.quantity() != null) p.setQuantity(input.quantity());
        if (input.desc() != null) p.setDesc(input.desc());
        if (input.price() != null) p.setPrice(input.price());
        if (input.userId() != null) {
            User u = userRepo.findById(input.userId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            p.setUser(u);
        }
    }

    // ====== INPUT TYPES with validation ======
    interface BaseUserInput {
        String fullname();
        String email();
        String password();
        String phone();
        Role role();
        List<Long> categoryIds();
    }

    public record UserInput(
            @NotBlank String fullname,
            @NotBlank @Email String email,
            @NotBlank @Size(min=6, message="Password tối thiểu 6 ký tự") String password,
            @Pattern(regexp = "^(\\+?\\d{8,15})?$", message = "Phone không hợp lệ") String phone,
            Role role,
            List<@NotNull Long> categoryIds
    ) implements BaseUserInput {}

    public record UserUpdateInput(
            @Size(min=1) String fullname,
            @Email String email,
            @Size(min=6, message="Password tối thiểu 6 ký tự") String password,
            @Pattern(regexp = "^(\\+?\\d{8,15})?$", message = "Phone không hợp lệ") String phone,
            Role role,
            List<@NotNull Long> categoryIds
    ) implements BaseUserInput {}

    public record CategoryInput(
            @Size(min=1, max=255) String name,
            @Size(max=1000) String images
    ) {}

    public record ProductInput(
            @Size(min=1) String title,
            @Min(0) Integer quantity,
            @Size(max=2000) String desc,
            @DecimalMin(value="0.0", inclusive=true, message="price >= 0") Double price,
            Long userId
    ) {}
}
