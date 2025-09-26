package vn.iostar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.iostar.entity.Product;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // lấy product theo price tăng dần
    List<Product> findAllByOrderByPriceAsc();

    // nếu bạn muốn lấy product theo userId (sau này để join với category qua user)
    List<Product> findByUser_Id(Long userId);
}
