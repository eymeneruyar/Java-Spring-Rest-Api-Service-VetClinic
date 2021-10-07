package vetcilinicservice.Documents;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "payin")
public class ElasticPayIn {

    @Id
    private String id;

    private Integer pinId;

    @Field(type = FieldType.Text)
    private String invoice;

    @Field(type = FieldType.Text)
    private String cuname;

}
