package victor.testing.spring.repo;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import victor.testing.spring.WaitForDatabase;
import victor.testing.spring.domain.Product;
import victor.testing.spring.domain.ProductCategory;
import victor.testing.spring.domain.Supplier;
import victor.testing.spring.service.ProductService;
import victor.testing.spring.web.dto.ProductSearchCriteria;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(initializers = WaitForDatabase.class)
@ActiveProfiles("db-mysql")
@Tag("integration")
public class ProductRepoSearchTest extends AbstractTestBase {
    @Autowired
    private ProductRepo repo;
    @Autowired
    private ProductService service;

    private ProductSearchCriteria criteria = new ProductSearchCriteria();


    @BeforeEach
    public void checkNoProductInDB() { // uneori [in app f mari] e util sa scrii pre-assumptii.
        assertThat(repo.findAll()).isEmpty();
    }


//    @Test
//    public void asjdksadjksajs() {
//        service.cheamMetodaAparentInocenta();
//    }

    @Test
    public void noCriteria() {

        repo.save(new Product());
        assertThat(repo.search(criteria)).hasSize(1);
    }

    @Test
    public void byName() {
        repo.save(new Product("GirAfa"));
        criteria.name="Girafa";
        assertThat(repo.search(criteria)).hasSize(1);
        criteria.name="Caine";
        assertThat(repo.search(criteria)).isEmpty();
        criteria.name="girafa";
        assertThat(repo.search(criteria)).hasSize(1);
        criteria.name="iRa";
        assertThat(repo.search(criteria)).hasSize(1);
    }

    @Test
    public void byCategory() {
        repo.save(new Product().setCategory(ProductCategory.WIFE));
        criteria.category = ProductCategory.WIFE;
        assertThat(repo.search(criteria)).hasSize(1);
        criteria.category = ProductCategory.ME;
        assertThat(repo.search(criteria)).isEmpty();
    }
    @Test
    public void bySupplier() {
        repo.save(new Product().setSupplier(supplier));
        criteria.supplierId = supplier.getId();
        assertThat(repo.search(criteria)).hasSize(1);
        criteria.supplierId = -1L;
        assertThat(repo.search(criteria)).isEmpty();
    }

    // TODO base test class persisting supplier

    // TODO replace with composition
}

