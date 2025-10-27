package com.agrarco.agrovers;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MiniPurchase {
    private int id;
    private long purchaseId;

    private LocalDateTime Tarix;

    private double Ceki;
    private int KiseSayi;
    private double BirKiseninCekisi;
    private int PaletSayi;
    private double BirPaletinSayi;

    public MiniPurchase() {}

    public MiniPurchase(int id, long purchaseId, LocalDateTime Tarix, double Ceki,
                        int KiseSayi, double BirKiseninCekisi, int PaletSayi, double BirPaletinSayi) {
        this.id = id;
        this.purchaseId = purchaseId;
        this.Tarix = Tarix;
        this.Ceki = Ceki;
        this.KiseSayi = KiseSayi;
        this.BirKiseninCekisi = BirKiseninCekisi;
        this.PaletSayi = PaletSayi;
        this.BirPaletinSayi = BirPaletinSayi;
    }
}
