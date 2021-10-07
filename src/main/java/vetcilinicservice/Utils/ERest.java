package vetcilinicservice.Utils;

public enum ERest {

    status,
    message,
    result,
    errors,

    payInTotal, //Kasaya Giren Toplam Para
    payOutTotal, //Kasadan Çıkan Toplam Para
    netEarning, //Net Kar

    salesCount, //Toplam Satış Sayısı
    customerCount, //Toplam Müşteri Sayısı
    productCount, //Toplam ürün sayısı
    totalPage // Toplam sayfa sayısı (pagination işlemleri için)

}
