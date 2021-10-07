package vetcilinicservice.Documents;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "storage")
public class ElasticStorage {

    @Id
    private String id;

    private Integer storId;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Text)
    private String no;

    @Field(type = FieldType.Text)
    private String status;

}
