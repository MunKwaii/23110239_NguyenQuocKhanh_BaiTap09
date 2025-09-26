package vn.iostar.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import vn.iostar.entity.*;
import vn.iostar.repository.*;

import java.util.*;

@Controller
@RequiredArgsConstructor
@Transactional
public class GraphqlController {

    private final UserRepository userRepo;
    private final CategoryRepository categoryRepo;
    private final ProductRepository productRepo;

    // ================== Query ==================
    @QueryMapping
    public List<Product> productsByPriceAsc() {
        return productRepo.findAllByOrderByPriceAsc();
    }

    @QueryMapping
    public List<Product> productsByCategory(@Argument Long categoryId) {
        // Lấy tất cả user thuộc category
        Category c = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        List<Product> result = new ArrayList<>();
        for (User u : c.getUsers()) {
            result.addAll(productRepo.findByUser_Id(u.getId()));
        }
        return result;
    }

    @QueryMapping
    public List<User> users() { return userRepo.findAll(); }

    @QueryMapping
    public List<Category> categories() { return categoryRepo.findAll(); }

    @QueryMapping
    public List<Product> products() { return productRepo.findAll(); }

    // ================== Mutation: User ==================
    @MutationMapping
    public User createUser(@Argument UserInput input) {
        User u = new User();
        applyUserInput(u, input);
        return userRepo.save(u);
    }

    @MutationMapping
    public User updateUser(@Argument Long id, @Argument UserInput input) {
        User u = userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        applyUserInput(u, input);
        return userRepo.save(u);
    }

    @MutationMapping
    public Boolean deleteUser(@Argument Long id) {
        if (!userRepo.existsById(id)) return false;
        userRepo.deleteById(id);
        return true;
    }

    private void applyUserInput(User u, UserInput input) {
        if (input.fullname() != null) u.setFullname(input.fullname());
        if (input.email() != null) u.setEmail(input.email());
        if (input.password() != null) u.setPassword(input.password());
        if (input.phone() != null) u.setPhone(input.phone());
        if (input.categoryIds() != null) {
            Set<Category> cats = new HashSet<>(categoryRepo.findAllById(input.categoryIds().stream().map(Long::valueOf).toList()));
            u.setCategories(cats);
        }
    }

    // ================== Mutation: Category ==================
    @MutationMapping
    public Category createCategory(@Argument CategoryInput input) {
        Category c = new Category();
        c.setName(input.name());
        c.setImages(input.images());
        return categoryRepo.save(c);
    }

    @MutationMapping
    public Category updateCategory(@Argument Long id, @Argument CategoryInput input) {
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

    // ================== Mutation: Product ==================
    @MutationMapping
    public Product createProduct(@Argument ProductInput input) {
        Product p = new Product();
        applyProductInput(p, input);
        return productRepo.save(p);
    }

    @MutationMapping
    public Product updateProduct(@Argument Long id, @Argument ProductInput input) {
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
            User u = userRepo.findById(Long.valueOf(input.userId())).orElseThrow(() -> new RuntimeException("User not found"));
            p.setUser(u);
        }
    }

    // ================== Input Types ==================
    public record UserInput(String fullname, String email, String password, String phone, List<String> categoryIds) {}
    public record CategoryInput(String name, String images) {}
    public record ProductInput(String title, Integer quantity, String desc, Double price, String userId) {}
}
