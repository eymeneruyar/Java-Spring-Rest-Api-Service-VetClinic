package vetcilinicservice.Repositories._elastic;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import vetcilinicservice.Documents.ElasticPayOut;

import java.util.Optional;

public interface ElasticPayoutRepository extends ElasticsearchRepository<ElasticPayOut,String> {

    @Query("{\"bool\":{\"must\":[{\"term\":{\"poId\":\"?0\"}}],\"must_not\":[],\"should\":[]}}")
    Optional<ElasticPayOut> findById(Integer poId);

}
