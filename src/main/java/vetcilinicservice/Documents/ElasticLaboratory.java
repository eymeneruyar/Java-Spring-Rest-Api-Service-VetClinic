package vetcilinicservice.Documents;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "laboratory")
public class ElasticLaboratory {

    @Id
    private String id;

    private Integer labId;

    @Field(type = FieldType.Text)
    private String cuname;

    @Field(type = FieldType.Text)
    private String paname;

    @Field(type = FieldType.Text)
    private String paAirTagNo;

}
