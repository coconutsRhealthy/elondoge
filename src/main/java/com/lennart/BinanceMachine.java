package com.lennart;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;

import java.util.List;

import static com.binance.api.client.domain.account.NewOrder.limitBuy;
import static com.binance.api.client.domain.account.NewOrder.limitSell;
import static com.binance.api.client.domain.account.NewOrder.marketBuy;

/**
 * Created by LennartMac on 28/04/2021.
 */
public class BinanceMachine {

    //private void getCurrentAskPriceFor

    private BinanceApiRestClient client;

    public BinanceMachine() {
        this.client = getBinanceApiClient();
    }


    private void testMethod() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(
                "p8d8phgnzjZ6ggBwQwj86VMLJunzZp3pNTUSPR1c6qL9Z1rdnu2cza6oSJkBr4IW",
                "7BkmBAW5sWM7rUPyp19q97LY6Pjk8kug7NSpAjistH7bo0wgRzkV9S4cP35yLPsp");

//        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(
//                "eije",
//                "eije");
        BinanceApiRestClient client = factory.newRestClient();

        OrderBook orderBook = client.getOrderBook("DOGEBTC", 10);
        List<OrderBookEntry> asks = orderBook.getAsks();
        OrderBookEntry firstAskEntry = asks.get(0);
        System.out.println(firstAskEntry.getPrice());

//        Account account = client.getAccount();
//        System.out.println(account.getBalances());
//
//        //client.newOrderTest(limitBuy("DOGEBTC", TimeInForce.GTC, "100", "0.000004"));
//
        NewOrderResponse newOrderResponse = client.newOrder(limitBuy("DOGEBTC", TimeInForce.GTC, "100", "0.00000461"));
        System.out.println(newOrderResponse.getTransactTime());
    }



    private BinanceApiRestClient getBinanceApiClient() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(
                "p8d8phgnzjZ6ggBwQwj86VMLJunzZp3pNTUSPR1c6qL9Z1rdnu2cza6oSJkBr4IW",
                "7BkmBAW5sWM7rUPyp19q97LY6Pjk8kug7NSpAjistH7bo0wgRzkV9S4cP35yLPsp");
        return factory.newRestClient();
    }

    public void placeDogeLimitBuyOrder() {
        client.newOrder(limitBuy("DOGEBTC", TimeInForce.GTC, "100", "0.00000462"));
    }

    public NewOrderResponse placeDogeMarketBuyOrder() {
        NewOrderResponse orderResponse = client.newOrder(marketBuy("DOGEBTC", "100"));
        return orderResponse;
    }

    public void placeDogeLimitSellOrder(String limit) {
        client.newOrder(limitSell("DOGEBTC", TimeInForce.GTC, "100", limit));
    }



//    public static void main(String[] args) {
//        BinanceMachine binanceMachine = new BinanceMachine();
//        BinanceApiRestClient client = binanceMachine.getBinanceApiClient();
//        System.out.println(binanceMachine.getCurrentAskPriceDogeBtc(client));
//    }

    private String getCurrentAskPriceDogeBtc(BinanceApiRestClient client) {
        OrderBook orderBook = client.getOrderBook("DOGEBTC", 10);
        List<OrderBookEntry> asks = orderBook.getAsks();
        OrderBookEntry firstAskEntry = asks.get(0);
        return firstAskEntry.getPrice();
    }



}
