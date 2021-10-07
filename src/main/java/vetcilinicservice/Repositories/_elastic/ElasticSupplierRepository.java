package vetcilinicservice.Repositories._elastic;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import vetcilinicservice.Documents.ElasticSupplier;

import java.util.Optional;

public interface ElasticSupplierRepository extends ElasticsearchRepository<ElasticSupplier,String> {

    @Query("{\"bool\":{\"must\":[{\"term\":{\"supId\":\"?0\"}}],\"must_not\":[],\"should\":[]}}")
    Optional<ElasticSupplier> findById(Integer supId);

}
