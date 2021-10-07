package vetcilinicservice.Documents;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "customer")
public class ElasticCustomer {

    @Id
    private String id;

    //@Field(type = FieldType.Integer)
    private Integer cuid;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Text)
    private String surname;

    @Field(type = FieldType.Text)
    private String phone;

    @Field(type = FieldType.Text)
    private String email;

}
