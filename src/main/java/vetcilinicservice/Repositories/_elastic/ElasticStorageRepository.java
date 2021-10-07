package vetcilinicservice.Repositories._elastic;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import vetcilinicservice.Documents.ElasticStorage;

import java.util.Optional;

public interface ElasticStorageRepository extends ElasticsearchRepository<ElasticStorage,String> {

    @Query("{\"bool\":{\"must\":[{\"term\":{\"storId\":\"?0\"}}],\"must_not\":[],\"should\":[]}}")
    Optional<ElasticStorage> findById(Integer storId);

}
