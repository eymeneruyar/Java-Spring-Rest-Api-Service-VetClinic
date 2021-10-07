package vetcilinicservice.Documents;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "payout")
public class ElasticPayOut {

    @Id
    private String id;

    private Integer poId;

    @Field(type = FieldType.Text)
    private String invoice;

    @Field(type = FieldType.Text)
    private String supname;

}
