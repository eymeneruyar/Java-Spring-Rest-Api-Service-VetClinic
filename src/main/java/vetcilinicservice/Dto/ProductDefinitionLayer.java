package vetcilinicservice.Dto;

import org.elasticsearch.common.unit.Fuzziness;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import vetcilinicservice.Documents.ElasticCustomer;
import vetcilinicservice.Documents.ElasticProduct;
import vetcilinicservice.Entities.Customer;
import vetcilinicservice.Entities.Product;
import vetcilinicservice.Repositories._elastic.ElasticProductRepository;
import vetcilinicservice.Repositories._jpa.ProductRepository;
import vetcilinicservice.Utils.ERest;
import vetcilinicservice.Utils.Util;

import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;

@Service
public class ProductDefinitionLayer {

    final ProductRepository pRepo;
    final ElasticProductRepository epRepo;
    final ElasticsearchOperations operations;
    public ProductDefinitionLayer(ProductRepository pRepo, ElasticProductRepository epRepo, ElasticsearchOperations operations) {
        this.pRepo = pRepo;
        this.epRepo = epRepo;
        this.operations = operations;
    }

    //Product Add
    public Map<ERest,Object> add(Product product, BindingResult bindingResult){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        if(!bindingResult.hasErrors()){
            try {
                Product pro = pRepo.save(product);
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Ürün ekleme işlemi başarılı!");
                hm.put(ERest.result,pro);
                //Elasticsearch save
                ElasticProduct ep = new ElasticProduct();
                ep.setName(pro.getProName());
                ep.setBarcode(pro.getProBarcode());
                ep.setProId(pro.getProId());
                epRepo.save(ep);
            } catch (Exception e) {
                hm.put(ERest.status,false);
                if(e.toString().contains("constraint")){
                    String error = "Bu barkod ("+product.getProBarcode()+") numarası ile daha önce kayıt yapılmış!";
                    Util.logger(error, Product.class);
                    hm.put(ERest.message,error);
                }
            }
        }else {
            hm.put(ERest.status,false);
            hm.put(ERest.errors, Util.errors(bindingResult));
        }
        return hm;
    }

    //All Product List
    public Map<ERest,Object> listAll(){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        List<Product> productList = new ArrayList<>();
        try {
            productList = pRepo.findAll();
            hm.put(ERest.status,true);
            hm.put(ERest.message,"Ürün listeleme işlemi başarılı!");
            hm.put(ERest.result,productList);
        } catch (Exception e) {
            String error = "Listeleme sırasında bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Product.class);
        }
        return hm;
    }

    //Product List with pagination
    public Map<ERest,Object> list(String stPage){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        List<Product> productList = new ArrayList<>();
        try {
            int page = Integer.parseInt(stPage);
            Pageable pageable = PageRequest.of(page-1,Util.pageSize);
            productList = pRepo.findByOrderByProIdAsc(pageable);
            hm.put(ERest.status,true);
            hm.put(ERest.message,(page) + " sayfadaki ürün listeleme işlemi başarılı!");
            hm.put(ERest.result,productList);
        } catch (Exception e) {
            String error = "Listeleme sırasında bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Product.class);
        }
        return hm;
    }

    //Product delete
    public Map<ERest,Object> delete(String stId){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        try {
            int id = Integer.parseInt(stId);
            Optional<Product> optProduct = pRepo.findById(id);
            if(optProduct.isPresent()){
                //Elasticsearch database and MySQL database delete data.
                ElasticProduct ep = epRepo.findById(id).get();
                pRepo.deleteById(id);
                epRepo.deleteById(ep.getId());
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Silme işlemi başarılı!");
                hm.put(ERest.result,optProduct.get());
            }else {
                String error = "Silmek istenen ürün bulunamadı!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                Util.logger(error,Product.class);
            }
        } catch (Exception e) {
            String error = "Silme işlemi sırasında bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Product.class);
        }
        return hm;
    }

    //Product update
    public Map<ERest,Object> update(Product product,BindingResult bindingResult){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        if(product.getProId() != null && !bindingResult.hasErrors()){
            Optional<Product> optProduct = pRepo.findById(product.getProId());
            if(optProduct.isPresent()){
                try {
                    //ElasticSearch and SQL DB Update -Start
                    ElasticProduct ep = epRepo.findById(product.getProId()).get();
                    epRepo.deleteById(ep.getId());
                    Product pro = pRepo.saveAndFlush(product);
                    ElasticProduct epNew = new ElasticProduct();
                    epNew.setName(pro.getProName());
                    epNew.setBarcode(pro.getProBarcode());
                    epNew.setProId(pro.getProId());
                    epRepo.save(epNew);
                    //ElasticSearch and SQL DB Update - End
                    hm.put(ERest.status,true);
                    hm.put(ERest.message,"Güncelleme işlemi başarılı!");
                    hm.put(ERest.result,pro);
                } catch (Exception e) {
                    if(e.toString().contains("constraint")){
                        String error = "Bu barkod ("+product.getProBarcode()+") numarası ile daha önce kayıt yapılmış!";
                        Util.logger(error, Product.class);
                        hm.put(ERest.message,error);
                    }
                }
            }else{
                String error = "Güncelleme işlemi yapılacak ürün bulunamadı!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                hm.put(ERest.result,product);
                Util.logger(error,Product.class);
            }
        }else {
            hm.put(ERest.status,false);
            hm.put(ERest.message,Util.errors(bindingResult));
        }
        return hm;
    }

    //------------------------------------- ElasticSearch - Start -------------------------------------//

    public Map<ERest,Object> elasticSearch(String data){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        final NativeSearchQuery query = new NativeSearchQueryBuilder()
                //Birden fazla aram kriteri eklemek için multiMatchQuery yapısı kullanılır.
                .withQuery(multiMatchQuery(data,"name")
                        .fuzziness(Fuzziness.AUTO))
                .build();
        List<SearchHit<ElasticProduct>> list = operations.search(query,ElasticProduct.class).getSearchHits();
        if(list.size() > 0 ){
            hm.put(ERest.status,true);
            hm.put(ERest.message,"Arama işlemi başarılı!");
            hm.put(ERest.result,list);
        }else{
            hm.put(ERest.status,false);
            hm.put(ERest.message,"Arama kriterlerinize uygun sonuç bulunamadı!");
        }
        return hm;
    }

    public Map<ERest,Object> elasticInsertData(){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        List<Product> productList = pRepo.findAll();
        try {
            if(productList.size() > 0){
                productList.forEach(item -> {
                    //ElasticSearch Save
                    ElasticProduct ep = new ElasticProduct();
                    ep.setName(item.getProName());
                    ep.setBarcode(item.getProBarcode());
                    ep.setProId(item.getProId());
                    epRepo.save(ep);
                });
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Elasticsearch veri ekleme işlemi başarılı!");
                hm.put(ERest.result,epRepo.findAll());
            }else {
                String error = "Sisteme kayıtlı müşteri bulunmamaktadır!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                Util.logger(error,Product.class);
            }
        } catch (Exception e) {
            String error = "Elasticsearch veri tabanına ekleme yapılırken bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,Product.class);
        }
        return hm;
    }

    //------------------------------------- ElasticSearch - End ---------------------------------------//

}
