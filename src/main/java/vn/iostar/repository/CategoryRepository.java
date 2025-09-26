package vn.iostar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.iostar.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
