package vn.iostar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.iostar.entity.Product;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByOrderByPriceAsc();

    List<Product> findByUser_Id(Long userId);

    @Query("""
           select p
           from Product p
             join p.user u
             join u.categories c
           where c.id = :categoryId
           """)
    List<Product> findByCategoryViaUser(@Param("categoryId") Long categoryId);
}
