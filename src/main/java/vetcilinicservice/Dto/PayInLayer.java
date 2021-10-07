package vetcilinicservice.Dto;

import org.elasticsearch.common.unit.Fuzziness;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import vetcilinicservice.Documents.ElasticPayIn;
import vetcilinicservice.Entities.PayIn;
import vetcilinicservice.Entities.Sales;
import vetcilinicservice.Entities.SalesPayment;
import vetcilinicservice.Repositories._elastic.ElasticPayinRepository;
import vetcilinicservice.Repositories._elastic.ElasticPayoutRepository;
import vetcilinicservice.Repositories._jpa.PayinRepository;
import vetcilinicservice.Repositories._jpa.PayoutRepository;
import vetcilinicservice.Repositories._jpa.SalesPaymentRepository;
import vetcilinicservice.Repositories._jpa.SalesRepository;
import vetcilinicservice.Utils.ERest;
import vetcilinicservice.Utils.Util;

import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;

@Service
public class PayInLayer {

    final PayinRepository payinRepo;
    final PayoutRepository payoutRepo;
    final SalesRepository salesRepo;
    final SalesPaymentRepository sapaRepo;
    final ElasticPayinRepository epayinRepo;
    final ElasticPayoutRepository epoutRepo;
    final ElasticsearchOperations operations;
    public PayInLayer(PayinRepository payinRepo, PayoutRepository payoutRepo, SalesRepository salesRepo, SalesPaymentRepository sapaRepo, ElasticPayinRepository epayinRepo, ElasticPayoutRepository epoutRepo, ElasticsearchOperations operations) {
        this.payinRepo = payinRepo;
        this.payoutRepo = payoutRepo;
        this.salesRepo = salesRepo;
        this.sapaRepo = sapaRepo;
        this.epayinRepo = epayinRepo;
        this.epoutRepo = epoutRepo;
        this.operations = operations;
    }

    //PayIn Add
    public Map<ERest,Object> addPayIn(PayIn payIn, BindingResult bindingResult){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        if(!bindingResult.hasErrors()){
            try {
                SalesPayment salesPayment = sapaRepo.findBySales_SaIdEquals(payIn.getSales().getSaId());
                if (salesPayment.getRemainDebt() == 0) {
                    hm.put(ERest.status,false);
                    hm.put(ERest.message,"Borç Kalmamıştır");
                }
                else {
                    //Kalan para ve Borç İşlemleri - Start
                    if (payIn.getPinId() != null){//update
                        PayIn payInEntity = payinRepo.getById(payIn.getPinId());
                        if (salesPayment.getRemainDebt() + payInEntity.getPinAmount() >= payIn.getPinAmount()) {
                            salesPayment.setRemainDebt(salesPayment.getRemainDebt() + payInEntity.getPinAmount() - payIn.getPinAmount());
                        }
                        payInEntity.setPinAmount(payIn.getPinAmount());
                        payinRepo.save(payInEntity);
                        hm.put(ERest.status,true);
                        hm.put(ERest.message,"Kasa giriş işlemi başarılı!");
                        hm.put(ERest.result,payInEntity);
                        //Elasticsearch save
                        ElasticPayIn epayin = new ElasticPayIn();
                        epayin.setPinId(payInEntity.getPinId());
                        epayin.setCuname(payInEntity.getSales().getCustomer().getCuName());
                        epayin.setInvoice(payInEntity.getSales().getSaReceiptNo());
                        epayinRepo.save(epayin);
                    }else {//add
                        //kalan borç ödenen borçtan düşük veya eşitse:
                        if (salesPayment.getRemainDebt() >= payIn.getPinAmount()) {
                            salesPayment.setRemainDebt(salesPayment.getRemainDebt() - payIn.getPinAmount());
                            PayIn p = payinRepo.save(payIn);
                            hm.put(ERest.status,true);
                            hm.put(ERest.message,"Kasa giriş işlemi başarılı!");
                            hm.put(ERest.result,p);
                            //Elasticsearch save
                            ElasticPayIn epayin = new ElasticPayIn();
                            epayin.setPinId(p.getPinId());
                            epayin.setCuname(p.getSales().getCustomer().getCuName());
                            epayin.setInvoice(p.getSales().getSaReceiptNo());
                            epayinRepo.save(epayin);
                        } else {
                            String error = "Ödenen toplam miktar borçtan daha fazla!";
                            hm.put(ERest.status,false);
                            hm.put(ERest.message,error);
                            Util.logger(error,PayIn.class);
                        }
                    }
                    //Kalan para ve Borç İşlemleri - End
                }
            } catch (Exception e) {
                hm.put(ERest.status,false);
                String error = "Kasa giriş işlemi sırasında bir hata oluştu!";
                Util.logger(error, PayIn.class);
                hm.put(ERest.message,error);
            }
        }else {
            hm.put(ERest.status,false);
            hm.put(ERest.errors, Util.errors(bindingResult));
        }
        return hm;
    }

    //All PayIn List
    public Map<ERest,Object> listAll(){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        List<PayIn> payInList = new ArrayList<>();
        try {
            payInList = payinRepo.findAll();
            hm.put(ERest.status,true);
            hm.put(ERest.message,"Kasa giriş listeleme işlemi başarılı!");
            hm.put(ERest.result,payInList);
        } catch (Exception e) {
            String error = "Listeleme sırasında bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,PayIn.class);
        }
        return hm;
    }

    //PayIn Invoice List with customer ID
    public Map<ERest,Object> invoiceList(String stId){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        List<Sales> salesList = new ArrayList<>();
        try {
            int id = Integer.parseInt(stId);
            salesList = salesRepo.findByCustomer_CuIdEquals(id);
            hm.put(ERest.status,true);
            hm.put(ERest.message,"Müşteri fatura listeleme işlemi başarılı!");
            hm.put(ERest.result,salesList);
        } catch (Exception e) {
            String error = "Listeleme sırasında bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error, Sales.class);
        }
        return hm;
    }

    //PayIn delete
    public Map<ERest,Object> delete(String stId){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        try {
            int id = Integer.parseInt(stId);
            Optional<PayIn> optPayIn = payinRepo.findById(id);
            if(optPayIn.isPresent()){
                //Elasticsearch database and MySQL database delete data.
                ElasticPayIn epayin = epayinRepo.findById(id).get();
                payinRepo.deleteById(id);
                epayinRepo.deleteById(epayin.getId());
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Silme işlemi başarılı!");
                hm.put(ERest.result,optPayIn.get());
            }else {
                String error = "Silmek istenen kasa giriş verisi bulunamadı!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                Util.logger(error,PayIn.class);
            }
        } catch (Exception e) {
            String error = "Silme işlemi sırasında bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,PayIn.class);
        }
        return hm;
    }

    //PayIn update
    public Map<ERest,Object> update(PayIn payIn,BindingResult bindingResult){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        SalesPayment salesPayment = sapaRepo.findBySales_SaIdEquals(payIn.getSales().getSaId());
        if(payIn.getPinId() != null && !bindingResult.hasErrors()){
            Optional<PayIn> optPayIn = payinRepo.findById(payIn.getPinId());
            if(optPayIn.isPresent()){
                try {
                    PayIn payin = payinRepo.getById(payIn.getPinId());
                    if (salesPayment.getRemainDebt() + payin.getPinAmount() >= payIn.getPinAmount()) {
                        salesPayment.setRemainDebt(salesPayment.getRemainDebt() + payin.getPinAmount() - payIn.getPinAmount());
                    }
                    payin.setPinAmount(payIn.getPinAmount());
                    //ElasticSearch and SQL DB Update -Start
                    ElasticPayIn epayin = epayinRepo.findById(payIn.getPinId()).get();
                    epayinRepo.deleteById(epayin.getId());
                    payin = payinRepo.saveAndFlush(payIn);
                    ElasticPayIn epayinNew = new ElasticPayIn();
                    epayinNew.setPinId(payin.getPinId());
                    epayinNew.setCuname(payin.getSales().getCustomer().getCuName());
                    epayinNew.setInvoice(payin.getSales().getSaReceiptNo());
                    epayinRepo.save(epayinNew);
                    //ElasticSearch and SQL DB Update - End
                    hm.put(ERest.status,true);
                    hm.put(ERest.message,"Güncelleme işlemi başarılı!");
                    hm.put(ERest.result,payin);
                } catch (Exception e) {
                    String error = "Güncelleme işlemi sırasında bir hata oluştu! " + e + " ";
                    hm.put(ERest.status,false);
                    hm.put(ERest.message,error);
                    hm.put(ERest.result,payIn);
                    Util.logger(error,PayIn.class);
                }
            }else{
                String error = "Güncelleme işlemi yapılacak kasa giriş verisi bulunamadı!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                hm.put(ERest.result,payIn);
                Util.logger(error,PayIn.class);
            }
        }else {
            hm.put(ERest.status,false);
            hm.put(ERest.message,Util.errors(bindingResult));
        }
        return hm;
    }

    //------------------------------------- ElasticSearch PayIn - Start -------------------------------------//

    public Map<ERest,Object> elasticSearchPayIn(String data){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        final NativeSearchQuery query = new NativeSearchQueryBuilder()
                //Birden fazla aram kriteri eklemek için multiMatchQuery yapısı kullanılır.
                .withQuery(multiMatchQuery(data,"cuname")
                        .field("invoice")
                        .fuzziness(Fuzziness.AUTO))
                .build();
        List<SearchHit<ElasticPayIn>> list = operations.search(query,ElasticPayIn.class).getSearchHits();
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

    public Map<ERest,Object> elasticInsertDataPayIn(){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        List<PayIn> payInList = payinRepo.findAll();
        try {
            if(payInList.size() > 0){
                payInList.forEach(item -> {
                    //ElasticSearch Save
                    ElasticPayIn epayin = new ElasticPayIn();
                    epayin.setPinId(item.getPinId());
                    epayin.setCuname(item.getSales().getCustomer().getCuName());
                    epayin.setInvoice(item.getSales().getSaReceiptNo());
                    epayinRepo.save(epayin);
                });
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Elasticsearch veri ekleme işlemi başarılı!");
                hm.put(ERest.result,epayinRepo.findAll());
            }else {
                String error = "Sisteme kayıtlı ödeme girişi bulunmamaktadır!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                Util.logger(error,PayIn.class);
            }
        } catch (Exception e) {
            String error = "Elasticsearch veri tabanına ekleme yapılırken bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,PayIn.class);
        }
        return hm;
    }

    //------------------------------------- ElasticSearch PayIn - End ---------------------------------------//

}
