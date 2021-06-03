package victor.testing.spring.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import victor.testing.spring.domain.Product;

import java.util.Optional;

public interface ProductRepo extends ProductRepoSearch, JpaRepository<Product, Long> {
   @Query("SELECT p FROM Product p where p.name = ?1")
   Product findByName(String name);

}
