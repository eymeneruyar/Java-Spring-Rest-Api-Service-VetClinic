package vetcilinicservice.Repositories._elastic;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import vetcilinicservice.Documents.ElasticProduct;

import java.util.Optional;

public interface ElasticProductRepository extends ElasticsearchRepository<ElasticProduct,String> {

    @Query("{\"bool\":{\"must\":[{\"term\":{\"proId\":\"?0\"}}],\"must_not\":[],\"should\":[]}}")
    Optional<ElasticProduct> findById(Integer proId);

}
