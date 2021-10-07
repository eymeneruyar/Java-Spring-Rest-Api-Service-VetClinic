package vetcilinicservice.Documents;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "treatment")
public class ElasticTreatment {

    @Id
    private String id;

    private Integer treId;

    @Field(type = FieldType.Text)
    private String cname;

    @Field(type = FieldType.Text)
    private String pname;

    private Long trecode;

    @Field(type = FieldType.Text)
    private String trenote;

}
