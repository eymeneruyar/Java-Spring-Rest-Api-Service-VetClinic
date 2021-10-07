package vetcilinicservice.Documents;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "buying")
public class ElasticBuying {

    @Id
    private String id;

    private Integer buyId;

    @Field(type = FieldType.Text)
    private String supplierName;

    @Field(type = FieldType.Text)
    private String productName;

}
