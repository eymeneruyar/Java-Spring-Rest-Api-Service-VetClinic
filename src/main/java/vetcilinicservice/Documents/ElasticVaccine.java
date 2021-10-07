package vetcilinicservice.Documents;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "vaccine")
public class ElasticVaccine {

    @Id
    private String id;

    private Integer vacid;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Long)
    private Long barcode;

    @Field(type = FieldType.Text)
    private String category;

}
