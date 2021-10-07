package vetcilinicservice.Repositories._elastic;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import vetcilinicservice.Documents.ElasticBuying;

public interface ElasticBuyingRepository extends ElasticsearchRepository<ElasticBuying,String> {

}
