package com.lennart;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.account.Trade;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.DoubleAccumulator;

import static com.binance.api.client.domain.account.NewOrder.limitBuy;
import static com.binance.api.client.domain.account.NewOrder.limitSell;
import static com.binance.api.client.domain.account.NewOrder.marketBuy;

/**
 * Created by LennartMac on 28/04/2021.
 */
public class BinanceMachine {

    private BinanceApiRestClient client;

    public BinanceMachine() {
        this.client = getBinanceApiClient();
    }

    private BinanceApiRestClient getBinanceApiClient() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(
                "key",
                "key");
        return factory.newRestClient();
    }

    public NewOrderResponse placeDogeMarketBuyOrder() {
        NewOrderResponse orderResponse = client.newOrder(marketBuy("DOGEBTC", "60000"));
        return orderResponse;
    }

    public void placeDogeLimitSellOrder(String limit) {
        client.newOrder(limitSell("DOGEBTC", TimeInForce.GTC, "60000", limit));
    }

    public void placeDogeLimitBuyOrder() {
        client.newOrder(limitBuy("DOGEBTC", TimeInForce.GTC, "23", "0.00000462"));
    }

    public double getPriceOfLastTrade() {
        double priceToReturn = 0;
        List<Trade> allTrades = client.getMyTrades("DOGEBTC");

        if(!allTrades.isEmpty()) {
            Trade lastTrade = allTrades.get(allTrades.size() - 1);
            System.out.println("ELONDOGE -> last trade number: " + lastTrade.getOrderId());
            priceToReturn = Double.valueOf(lastTrade.getPrice());
        }

        return priceToReturn;
    }

    public static void main(String[] args) {
        new BinanceMachine().fftesten();
    }

    private void fftesten() {
        //List<AssetBalance> eije = client.getAccount().getBalances();
        //System.out.println("wacht");

        //client.getCandlestickBars();
        List<Candlestick> dogeBtcCandleSticks = client.getCandlestickBars("ETHBTC", CandlestickInterval.ONE_MINUTE);
        List<Double> prices = new ArrayList<>();

        for(Candlestick stick : dogeBtcCandleSticks) {
            double high = Double.valueOf(stick.getHigh());
            double low = Double.valueOf(stick.getLow());
            double average = (high + low) / 2;
            prices.add(average);
        }

        prices.stream().forEach(price -> {
            String priceAsString = String.valueOf(price);
            priceAsString = priceAsString.replace(".", ",");
            System.out.println(priceAsString);
        });



        //System.out.println("prt");

        //open:

        //openTime=1620518400000
        //closeTime=1620604799999

        //1620737168

        //Candlestick[openTime=1620712980000, closeTime=1620713039999

        //Candlestick[openTime=1620737220000,closeTime=1620737279999


    }
}
