package victor.testing.spring.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@ToString
public class Product {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

//    private int pay;

    private ProductCategory category;

    private String barcode;

    @ManyToOne
    private Supplier supplier;

//    @CreatedBy // Spring assigns this at creation time: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#auditing
//    private String createUsername;

    @CreatedDate // Spring assigns this at creation time: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#auditing
    private LocalDate createDate;

    public Product(String name, String barcode, ProductCategory category) {
        this.name = name;
        this.barcode = barcode;
        this.category = category;
    }

    public Product(String name) {
        this.name = name;
    }

    public Product() {}


}
