package vetcilinicservice.Repositories._elastic;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import vetcilinicservice.Documents.ElasticTreatment;

import java.util.Optional;

public interface ElasticTreatmentRepository extends ElasticsearchRepository<ElasticTreatment,String> {

    @Query("{\"bool\":{\"must\":[{\"term\":{\"treId\":\"?0\"}}],\"must_not\":[],\"should\":[]}}")
    Optional<ElasticTreatment> findById(Integer treId);

}
