package vetcilinicservice.Dto;

import org.elasticsearch.common.unit.Fuzziness;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import vetcilinicservice.Documents.ElasticPayIn;
import vetcilinicservice.Documents.ElasticPayOut;
import vetcilinicservice.Entities.*;
import vetcilinicservice.Repositories._elastic.ElasticPayinRepository;
import vetcilinicservice.Repositories._elastic.ElasticPayoutRepository;
import vetcilinicservice.Repositories._jpa.BuyingPaymentRepository;
import vetcilinicservice.Repositories._jpa.BuyingRepository;
import vetcilinicservice.Repositories._jpa.PayinRepository;
import vetcilinicservice.Repositories._jpa.PayoutRepository;
import vetcilinicservice.Utils.ERest;
import vetcilinicservice.Utils.Util;

import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;

@Service
public class PayOutLayer {

    final PayinRepository payinRepo;
    final PayoutRepository payoutRepo;
    final BuyingRepository buyingRepo;
    final BuyingPaymentRepository buyingPaymentRepository;
    final ElasticPayinRepository epayinRepo;
    final ElasticPayoutRepository epoutRepo;
    final ElasticsearchOperations operations;
    public PayOutLayer(PayinRepository payinRepo, PayoutRepository payoutRepo, BuyingRepository buyingRepo, BuyingPaymentRepository buyingPaymentRepository, ElasticPayinRepository epayinRepo, ElasticPayoutRepository epoutRepo, ElasticsearchOperations operations) {
        this.payinRepo = payinRepo;
        this.payoutRepo = payoutRepo;
        this.buyingRepo = buyingRepo;
        this.buyingPaymentRepository = buyingPaymentRepository;
        this.epayinRepo = epayinRepo;
        this.epoutRepo = epoutRepo;
        this.operations = operations;
    }

    //PayOut Add
    public Map<ERest,Object> addPayOut(PayOut payOut, BindingResult bindingResult){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        if(!bindingResult.hasErrors()){
            try {
                BuyingPayment buyingPayment = buyingPaymentRepository.findByBuying_BuyIdEquals(payOut.getBuying().getBuyId());
                if (buyingPayment.getRemainDebt() == 0) {
                    hm.put(ERest.status,false);
                    hm.put(ERest.message,"Borç Kalmamıştır");
                }
                else {
                    //Kalan para ve Borç İşlemleri - Start
                    if (payOut.getPoId() != null){//update
                        PayOut payOutEntity = payoutRepo.getById(payOut.getPoId());
                        if (buyingPayment.getRemainDebt() + payOutEntity.getPoutAmount() >= payOut.getPoutAmount()) {
                            buyingPayment.setRemainDebt(buyingPayment.getRemainDebt() + payOutEntity.getPoutAmount() - payOut.getPoutAmount());
                        }
                        payOutEntity.setPoutAmount(payOut.getPoutAmount());
                        payoutRepo.save(payOutEntity);
                        hm.put(ERest.status,true);
                        hm.put(ERest.message,"Kasa çıkış işlemi başarılı!");
                        hm.put(ERest.result,payOutEntity);
                        //Elasticsearch save
                        ElasticPayOut epayout = new ElasticPayOut();
                        epayout.setPoId(payOutEntity.getPoId());
                        epayout.setSupname(payOutEntity.getBuying().getSupplier().getSupName());
                        epoutRepo.save(epayout);
                    }else {//add
                        //kalan borç ödenen borçtan düşük veya eşitse:
                        if (buyingPayment.getRemainDebt() >= payOut.getPoutAmount()) {
                            buyingPayment.setRemainDebt(buyingPayment.getRemainDebt() - payOut.getPoutAmount());
                            PayOut p = payoutRepo.save(payOut);
                            hm.put(ERest.status,true);
                            hm.put(ERest.message,"Kasa çıkış işlemi başarılı!");
                            hm.put(ERest.result,p);
                            //Elasticsearch save
                            ElasticPayOut epayout = new ElasticPayOut();
                            epayout.setPoId(p.getPoId());
                            epayout.setSupname(p.getBuying().getSupplier().getSupName());
                            epoutRepo.save(epayout);
                        } else {
                            String error = "Ödenen toplam miktar borçtan daha fazla!";
                            hm.put(ERest.status,false);
                            hm.put(ERest.message,error);
                            Util.logger(error,PayOut.class);
                        }
                    }
                    //Kalan para ve Borç İşlemleri - End
                }
            } catch (Exception e) {
                hm.put(ERest.status,false);
                String error = "Kasa çıkış işlemi sırasında bir hata oluştu!";
                Util.logger(error, PayOut.class);
                hm.put(ERest.message,error);
            }
        }else {
            hm.put(ERest.status,false);
            hm.put(ERest.errors, Util.errors(bindingResult));
        }
        return hm;
    }

    //All PayOut List
    public Map<ERest,Object> listAll(){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        List<PayOut> payOutList = new ArrayList<>();
        try {
            payOutList = payoutRepo.findAll();
            hm.put(ERest.status,true);
            hm.put(ERest.message,"Kasa çıkış listeleme işlemi başarılı!");
            hm.put(ERest.result,payOutList);
        } catch (Exception e) {
            String error = "Listeleme sırasında bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,PayOut.class);
        }
        return hm;
    }

    //PayOut Invoice List with supplier ID
    public Map<ERest,Object> invoiceList(String stId){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        List<Buying> buyingList = new ArrayList<>();
        try {
            int id = Integer.parseInt(stId);
            buyingList = buyingRepo.findBySupplier_SupIdEquals(id);
            hm.put(ERest.status,true);
            hm.put(ERest.message,"Tedarikçi fatura listeleme işlemi başarılı!");
            hm.put(ERest.result,buyingList);
        } catch (Exception e) {
            String error = "Listeleme sırasında bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error, Buying.class);
        }
        return hm;
    }

    //PayOut delete
    public Map<ERest,Object> delete(String stId){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        try {
            int id = Integer.parseInt(stId);
            Optional<PayOut> optPayOut = payoutRepo.findById(id);
            if(optPayOut.isPresent()){
                //Elasticsearch database and MySQL database delete data.
                ElasticPayOut epayout = epoutRepo.findById(id).get();
                payoutRepo.deleteById(id);
                epoutRepo.deleteById(epayout.getId());
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Silme işlemi başarılı!");
                hm.put(ERest.result,optPayOut.get());
            }else {
                String error = "Silmek istenen kasa çıkış verisi bulunamadı!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                Util.logger(error,PayOut.class);
            }
        } catch (Exception e) {
            String error = "Silme işlemi sırasında bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,PayOut.class);
        }
        return hm;
    }

    //PayOut update
    public Map<ERest,Object> update(PayOut payOut,BindingResult bindingResult){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        BuyingPayment buyingPayment = buyingPaymentRepository.findByBuying_BuyIdEquals(payOut.getBuying().getBuyId());
        if(payOut.getPoId() != null && !bindingResult.hasErrors()){
            Optional<PayOut> optPayOut = payoutRepo.findById(payOut.getPoId());
            if(optPayOut.isPresent()){
                try {
                    PayOut payout = payoutRepo.getById(payOut.getPoId());
                    if (buyingPayment.getRemainDebt() + payout.getPoutAmount() >= payOut.getPoutAmount()) {
                        buyingPayment.setRemainDebt(buyingPayment.getRemainDebt() + payout.getPoutAmount() - payOut.getPoutAmount());
                    }
                    payout.setPoutAmount(payout.getPoutAmount());
                    //ElasticSearch and SQL DB Update -Start
                    ElasticPayOut epayout = epoutRepo.findById(payOut.getPoId()).get();
                    epoutRepo.deleteById(epayout.getId());
                    payout = payoutRepo.saveAndFlush(payOut);
                    ElasticPayOut epayoutNew = new ElasticPayOut();
                    epayoutNew.setPoId(payOut.getPoId());
                    epayoutNew.setSupname(payOut.getBuying().getSupplier().getSupName());
                    epoutRepo.save(epayoutNew);
                    //ElasticSearch and SQL DB Update - End
                    hm.put(ERest.status,true);
                    hm.put(ERest.message,"Güncelleme işlemi başarılı!");
                    hm.put(ERest.result,payout);
                } catch (Exception e) {
                    String error = "Güncelleme işlemi sırasında bir hata oluştu! " + e + " ";
                    hm.put(ERest.status,false);
                    hm.put(ERest.message,error);
                    hm.put(ERest.result,payOut);
                    Util.logger(error,PayOut.class);
                }
            }else{
                String error = "Güncelleme işlemi yapılacak kasa çıkış verisi bulunamadı!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                hm.put(ERest.result,payOut);
                Util.logger(error,PayOut.class);
            }
        }else {
            hm.put(ERest.status,false);
            hm.put(ERest.message,Util.errors(bindingResult));
        }
        return hm;
    }

    //------------------------------------- ElasticSearch PayOut - Start -------------------------------------//

    public Map<ERest,Object> elasticSearchPayOut(String data){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        final NativeSearchQuery query = new NativeSearchQueryBuilder()
                //Birden fazla aram kriteri eklemek için multiMatchQuery yapısı kullanılır.
                .withQuery(multiMatchQuery(data,"supname")
                        .fuzziness(Fuzziness.AUTO))
                .build();
        List<SearchHit<ElasticPayOut>> list = operations.search(query,ElasticPayOut.class).getSearchHits();
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

    public Map<ERest,Object> elasticInsertDataPayOut(){
        Map<ERest,Object> hm = new LinkedHashMap<>();
        List<PayOut> payOutList = payoutRepo.findAll();
        try {
            if(payOutList.size() > 0){
                payOutList.forEach(item -> {
                    //ElasticSearch Save
                    ElasticPayOut epayout = new ElasticPayOut();
                    epayout.setPoId(item.getPoId());
                    epayout.setSupname(item.getBuying().getSupplier().getSupName());
                    epoutRepo.save(epayout);
                });
                hm.put(ERest.status,true);
                hm.put(ERest.message,"Elasticsearch veri ekleme işlemi başarılı!");
                hm.put(ERest.result,epoutRepo.findAll());
            }else {
                String error = "Sisteme kayıtlı ödeme çıkışı bulunmamaktadır!";
                hm.put(ERest.status,false);
                hm.put(ERest.message,error);
                Util.logger(error,PayOut.class);
            }
        } catch (Exception e) {
            String error = "Elasticsearch veri tabanına ekleme yapılırken bir hata oluştu!" + e;
            hm.put(ERest.status,false);
            hm.put(ERest.message,error);
            Util.logger(error,PayOut.class);
        }
        return hm;
    }

    //------------------------------------- ElasticSearch PayOut - End ---------------------------------------//

}
