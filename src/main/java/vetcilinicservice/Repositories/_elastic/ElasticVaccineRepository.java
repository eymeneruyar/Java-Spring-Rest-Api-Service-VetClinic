package vetcilinicservice.Repositories._elastic;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import vetcilinicservice.Documents.ElasticVaccine;

import java.util.Optional;

public interface ElasticVaccineRepository extends ElasticsearchRepository<ElasticVaccine,String> {

    @Query("{\"bool\":{\"must\":[{\"term\":{\"vacid\":\"?0\"}}],\"must_not\":[],\"should\":[]}}")
    Optional<ElasticVaccine> findById(Integer vacid);

}
