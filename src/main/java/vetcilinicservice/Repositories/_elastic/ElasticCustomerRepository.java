package vetcilinicservice.Repositories._elastic;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import vetcilinicservice.Documents.ElasticCustomer;

import java.util.Optional;

public interface ElasticCustomerRepository extends ElasticsearchRepository<ElasticCustomer,String> {

    @Query("{\"bool\":{\"must\":[{\"term\":{\"cuid\":\"?0\"}}],\"must_not\":[],\"should\":[]}}")
    Optional<ElasticCustomer> findById(Integer cuid);

}
