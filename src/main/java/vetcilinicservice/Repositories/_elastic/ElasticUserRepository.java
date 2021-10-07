package vetcilinicservice.Repositories._elastic;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import vetcilinicservice.Documents.ElasticUser;

import java.util.Optional;

public interface ElasticUserRepository extends ElasticsearchRepository<ElasticUser,String> {

    @Query("{\"bool\":{\"must\":[{\"term\":{\"uId\":\"?0\"}}],\"must_not\":[],\"should\":[]}}")
    Optional<ElasticUser> findById(Integer uId);

}
