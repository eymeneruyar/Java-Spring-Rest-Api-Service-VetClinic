package vetcilinicservice.Repositories._elastic;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import vetcilinicservice.Documents.ElasticPatient;

import java.util.Optional;

public interface ElasticPatientRepository extends ElasticsearchRepository<ElasticPatient,String> {

    @Query("{\"bool\":{\"must\":[{\"term\":{\"paId\":\"?0\"}}],\"must_not\":[],\"should\":[]}}")
    Optional<ElasticPatient> findById(Integer paId);

}
