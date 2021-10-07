package vetcilinicservice.Documents;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "sales")
public class ElasticSales {

    @Id
    private String id;

    private Integer saId;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Text)
    private String invoice;

}
