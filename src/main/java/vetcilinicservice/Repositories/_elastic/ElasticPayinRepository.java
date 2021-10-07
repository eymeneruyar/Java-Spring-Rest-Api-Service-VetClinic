package vetcilinicservice.Repositories._elastic;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import vetcilinicservice.Documents.ElasticPayIn;

import java.util.Optional;

public interface ElasticPayinRepository extends ElasticsearchRepository<ElasticPayIn,String> {

    @Query("{\"bool\":{\"must\":[{\"term\":{\"pinId\":\"?0\"}}],\"must_not\":[],\"should\":[]}}")
    Optional<ElasticPayIn> findById(Integer pinId);

}
