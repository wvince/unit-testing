package victor.testing.spring.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import victor.testing.spring.domain.Product;
import victor.testing.spring.domain.Supplier;
import victor.testing.spring.repo.ProductRepo;
import victor.testing.spring.repo.SupplierRepo;
import victor.testing.spring.web.dto.ProductDto;
import victor.testing.spring.web.dto.ProductSearchCriteria;
import victor.testing.spring.web.dto.ProductSearchResult;
import victor.testing.tools.HumanReadableTestNames;
import victor.testing.tools.TestcontainersUtils;

import java.time.LocalDate;
import java.util.List;

import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static victor.testing.spring.domain.ProductCategory.HOME;


// WHY to test a controller?
// - Logic ==> @Autowire the controller and call its methods!
// - Contract: url, POST.., DTO structure


// - HTTP Status Code, @RestControllerAdvice ==> does the code matter?
// - @Validated on params of @RestController methods
// - Security: @Secured, @PreAuthorized, mvcMatcher("/admin/**), SecurityContextHolder usage

@DisplayNameGeneration(HumanReadableTestNames.class) // makes test names look nice


/**
 * <li> Connects to a production-like DB in a Docker image
 * <li> Runs the flyway migration scripts against the empty DB
 * <li> Uses WireMock to stub the JSON responses from third party APIs
 * <li> Starts one @Transaction / @Test
 * <li> Fills some 'static' data in the database (Supplier)
 * <li> pre-autorizes the request as ROLE_ADMIN by default
 * <li> Emulates a JSON request against my API and checks the JSON response
 * <li> At the end of each test leaves the DB clean (by auto-rollback of @Transactional)
 */
@SpringBootTest

@AutoConfigureWireMock(port = 0)
@Testcontainers
@Transactional
@ActiveProfiles({"db-migration", "wiremock"})

@WithMockUser(roles = "ADMIN") // current thread is ROLE_ADMIN
@AutoConfigureMockMvc // ❤️ emulates HTTP request without starting a Tomcat => @Transactional works, as the whole test shares 1 single thread
public class ProductControllerTest {
    private final static ObjectMapper jackson = new ObjectMapper().registerModule(new JavaTimeModule());
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private SupplierRepo supplierRepo;
    @Autowired
    private ProductRepo productRepo;
    @Container
    static public PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:11");

    @DynamicPropertySource
    public static void registerPgProperties(DynamicPropertyRegistry registry) {
        TestcontainersUtils.addDatasourceDetails(registry, postgres, true);
    }

    protected Long supplierId;
    protected ProductDto product;

    @BeforeEach
    public void persistReferenceData() {
        supplierId = supplierRepo.save(new Supplier().setActive(true)).getId();
        product = new ProductDto("productName", "safebar", supplierId, HOME);
    }

    @Test
    public void whiteBox() throws Exception {
        createProductRawJson("Tree");

        // (A) white box = direct DB access
        Product returnedProduct = productRepo.findAll().get(0);
        assertThat(returnedProduct.getName()).isEqualTo("Tree");
        assertThat(returnedProduct.getCreateDate()).isToday();
        assertThat(returnedProduct.getCategory()).isEqualTo(product.category);
        assertThat(returnedProduct.getSupplier().getId()).isEqualTo(product.supplierId);
        assertThat(returnedProduct.getBarcode()).isEqualTo(product.barcode);
    }

    @Test
    public void blackBoxFlow() throws Exception {
        createProduct("Tree"); // call#1

        // (B) black box = only API calls; more decoupled
        List<ProductSearchResult> results = searchProduct(criteria().setName("Tree")); // call#2
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Tree");
        Long productId = results.get(0).getId();

        ProductDto returnedProduct = getProduct(productId); // call#3
        assertThat(returnedProduct.getCategory()).isEqualTo(product.category);
        assertThat(returnedProduct.getSupplierId()).isEqualTo(product.supplierId);
        assertThat(returnedProduct.getBarcode()).isEqualTo(product.barcode);
        assertThat(returnedProduct.getCreateDate()).isToday();
    }


    // ==================== test-DSL (helper/framework) ======================

    private static ProductSearchCriteria criteria() {
        return new ProductSearchCriteria();
    }

    // #1 RAW JSON in test
    // - cumbersome
    // + breaks on Dto structure change:
    //    * Prevent accidental changes to my API ==> OpenAPIFreezeTest
    //    * Keep consumer-provider in sync👌 ==> Pact / Spring Cloud Contract Tests
    void createProductRawJson(String name) throws Exception {
        // language=json
//        String createJson = ("{\n" +
//                             "    \"name\": \"%s\",\n" +
//                             "    \"supplierId\": \"%d\",\n" +
//                             "    \"category\" : \"%s\",\n" +
//                             "    \"barcode\": \"safebar\"\n" +
//                             "}\n").formatted(name, supplierId, HOME);

//        mockMvc.perform(post("/product/create")
//                        .content(createJson)
//                        .contentType(APPLICATION_JSON))
//                .andExpect(status().is2xxSuccessful())
//               ;
    }

    // #2 ❤️ new DTO => JSON with jackson + Contract Test/Freeze
    void createProduct(String name) throws Exception {
        product.setName(name)
                .setSupplierId(supplierId)
                .setCategory(HOME)
                .setBarcode("safebar");

        mockMvc.perform(post("/product/create")
                        .content(jackson.writeValueAsString(product))
                        .contentType(APPLICATION_JSON)) // can be set as default
                .andExpect(status().is2xxSuccessful());
    }



    private List<ProductSearchResult> searchProduct(ProductSearchCriteria criteria) throws Exception {
        String responseJson = mockMvc.perform(post("/product/search")
                        .content(jackson.writeValueAsString(criteria))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return List.of(jackson.readValue(responseJson, ProductSearchResult[].class)); // trick to unmarshall a collection<obj>
    }

    private ProductDto getProduct(long productId) throws Exception {
        String responseJson = mockMvc.perform(get("/product/{id}", productId))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();
        return jackson.readValue(responseJson, ProductDto.class);
    }

    // ==================== More stuff you can test with MockMvc ======================

    @Test
    void createProduct_returnsHeader() throws Exception {
        mockMvc.perform(post("/product/create")
                        .content(jackson.writeValueAsString(product))
                        .contentType(APPLICATION_JSON))
                .andExpect(header().string("Location", "http://created-uri"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser // resets the credentials set at the class level
    public void createProductByNonAdmin_NotAuthorized() throws Exception {
        mockMvc.perform(post("/product/create")
                        .content(jackson.writeValueAsString(product))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    public void cannotCreateProductWithNullName() throws Exception {
        product.setName(null); // triggers @Validated on controller method

        mockMvc.perform(post("/product/create")
                        .content(jackson.writeValueAsString(product))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().is4xxClientError()); // see the @RestControllerAdvice
    }

}
