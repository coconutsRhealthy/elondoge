package com.lennart;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.account.Trade;

import java.util.List;

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
}
