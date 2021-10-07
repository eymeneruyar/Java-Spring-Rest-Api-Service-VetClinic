package vetcilinicservice.Repositories._elastic;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import vetcilinicservice.Documents.ElasticCustomer;
import vetcilinicservice.Documents.ElasticLaboratory;

import java.util.Optional;

public interface ElasticLaboratoryRepository extends ElasticsearchRepository<ElasticLaboratory,String> {

    @Query("{\"bool\":{\"must\":[{\"term\":{\"labId\":\"?0\"}}],\"must_not\":[],\"should\":[]}}")
    Optional<ElasticLaboratory> findById(Integer labId);

}
