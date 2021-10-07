package vetcilinicservice.Repositories._elastic;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import vetcilinicservice.Documents.ElasticSales;

public interface ElasticSalesRepository extends ElasticsearchRepository<ElasticSales,String> {
}
