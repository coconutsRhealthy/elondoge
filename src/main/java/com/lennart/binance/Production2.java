package com.lennart.binance;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.account.Trade;
import com.binance.api.client.domain.general.ExchangeInfo;
import com.binance.api.client.domain.general.FilterType;
import com.binance.api.client.domain.general.SymbolFilter;
import com.binance.api.client.domain.general.SymbolInfo;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;

import java.awt.*;
import java.awt.event.InputEvent;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.binance.api.client.domain.account.NewOrder.*;

/**
 * Created by LennartMac on 15/05/2021.
 */
public class Production2 {

    private BinanceApiRestClient client;

    private static final double AMOUNT_TO_INVEST_PER_COIN = 25;
    private static final String BASE_COIN = "BUSD";

    public Production2() {
        client = BinanceClientFactory.getBinanceApiClient();
    }


//    public static void main(String[] args) {
//        new Production2().testOco();
//    }


    private void testMethode() {
        placeLimitSellOrder("DOGE", "BUSD", "20", "0.65");
        placeStopLimitOrder("DOGEBUSD", "25", "0.47", "0.45");


    }


    private void testOco() {
//        NewOrder sell = limitSell(symbol, TimeInForce.GTC, quantity, price);
//        sell.type(OrderType.OCO);
//        sell.stopPrice(stopPrice);
//        sell.stopL(stopLimitPrice);
//        return sell;

//        NewOrder eije = NewOrder.ocoSell("DOGEBUSD", "25", "0.56", "0.48", "0.47");
//        client.newOcoOrder(eije);

        //NewOrder buy = NewOrder.ocoBuy("XRPBTC", "5", "0.00002675", "0.00002800", "0.00002840");
        //client.newOcoOrder()
    }



    private void continuous() {
        CoinIdentifier coinIdentifier = new CoinIdentifier();
        List<String> coinsToBuy = coinIdentifier.getCoinsToBuy(null, 4, 13, 1.004);

        for(String coin : coinsToBuy) {
            if(getCurrentBusdBalance() > 24) {
                tradeWrapper(coin);
            }
        }
    }

//    public static void main(String[] args) {
//        //new Production2().continuous3();
//        new Production2().continuous3();
//    }

    private void continuous3() {
        long timeIsGoodTime = 0;
        List<String> attractiveCoins = Arrays.asList("DOGEBUSD");
        boolean fiveMinuteAttractiveCoinRefreshNeeded = false;
        int evaluateIterationCounter = 0;

        for(int i = 0; i < 100_000_000; i++) {
            try {
                //boolean enoughTimeSincePrev = enoughTimeSincePrev(timeIsGoodTime);

                //if(enoughTimeSincePrev && timeIsGood(13)) {
                    //timeIsGoodTime = new Date().getTime();
                    CoinIdentifier coinIdentifier = new CoinIdentifier();
                    List<String> coinsToBuy = coinIdentifier.getCoinsToBuy(attractiveCoins, 4, 13, 0.994);

                    for(String coin : coinsToBuy) {
                        if(getCurrentBusdBalance() > 24) {
                            tradeWrapper(coin);
                        }
                    }

//                    evaluateIterationCounter++;
//
//                    if(evaluateIterationCounter == 12) {
//                        evaluateIterationCounter = 0;
//                        fiveMinuteAttractiveCoinRefreshNeeded = true;
//                    }
                //}

                //if(!enoughTimeSincePrev) {
                    //TimeUnit.SECONDS.sleep(5);
                //}

//                if(fiveMinuteAttractiveCoinRefreshNeeded) {
//                    System.out.println("Gonna refresh attractive coins. Time: " + new Date().getTime() +
//                            " Old size: " + attractiveCoins.size());
//                    attractiveCoins = new BigBackTest().getAttractiveCoinsDynamically();
//                    System.out.println("New attractive coins size: " + attractiveCoins.size());
//                    System.out.println("New attractive coins list: ");
//
//                    for(int z = 0; z < attractiveCoins.size(); z++) {
//                        System.out.println("" + z + ") " + attractiveCoins.get(z));
//                    }
//
//                    System.out.println();
//                    fiveMinuteAttractiveCoinRefreshNeeded = false;
//                }
            } catch (Exception e) {
                System.out.println("BINANCE EXCEPTION ERROR");
                e.printStackTrace();
            }
        }
    }

    private boolean enoughTimeSincePrev(long prevTime) {
        long currTime = new Date().getTime();
        long thirtySeconds = 30_000;

        if(currTime > (prevTime + thirtySeconds)) {
            return true;
        }

        return false;
    }

    private void continuous2() {
        //get all coins

        for(int i = 0; i < 500; i++) {
            //seconds
            if(timeIsGood(13)) {
                System.out.println("Counter: " + i);
                moveMouseIfNeeded();

                CoinIdentifier coinIdentifier = new CoinIdentifier();
                List<String> allCoins = coinIdentifier.getAllBusdTradingPairs();

                for(String coin : allCoins) {
                    if(getCurrentBusdBalance() > 24) {
                        if(coinIdentifier.isCoinToBuy(coin, 1.004, client)) {
                            System.out.println("Coin to buy! " + coin);
                            tradeWrapper(coin);
                        }
                    }
                }
            }
        }
    }

    private boolean timeIsGood(int secondsLimit) {
        CoinIdentifier identifier = new CoinIdentifier();

        List<Candlestick> dummySticks = identifier.getAllCandleSticksForPair("BTCBUSD", client);
        int secondsSince = identifier.getNumberOfSecondsSinceNewestCompletedStickClose(dummySticks.get(dummySticks.size() - 2));

        return secondsSince < secondsLimit;
    }

    private void tradeWrapper(String coinToTrade) {
        coinToTrade = coinToTrade.replace("BUSD", "");

        double currentAsk = getCurrentAskPrice(coinToTrade, BASE_COIN);
        double amountToBuy = AMOUNT_TO_INVEST_PER_COIN / currentAsk;
        String minQtyToTrade = getMinQtyToTrade(coinToTrade, BASE_COIN);

        if(positionCanBeTraded(minQtyToTrade, amountToBuy, coinToTrade + BASE_COIN)) {
            if(noActivePositionInThisCoin(coinToTrade)) {
                String amountToBuyString = getAmountToTradeString(amountToBuy, minQtyToTrade);
                System.out.println("amount to buy: " + amountToBuyString);
                long marketBuyShouldBeAfterThisTime = new Date().getTime();
                placeMarketBuyOrder(coinToTrade, BASE_COIN, amountToBuyString);
                double buyPrice = getPriceOfLastBuyTradeWrapper(coinToTrade + BASE_COIN, marketBuyShouldBeAfterThisTime);
                System.out.println("buyprice via last trade: " + buyPrice);
                double position = getPosition(coinToTrade);
                String amountToSellString = getAmountToTradeString(position, minQtyToTrade);
                System.out.println("amount to sell: " + amountToBuyString);
                String ticksize = getPriceLimitDecimals(coinToTrade, BASE_COIN);
                placeOcoSell(coinToTrade + BASE_COIN, amountToSellString, getSellLimit(buyPrice, ticksize),
                        getStopPrice(buyPrice, ticksize), getStopLimitBelowStopPrice(buyPrice, ticksize));
                System.out.println();
            }
        }
    }

    private boolean noActivePositionInThisCoin(String coin) {
        boolean noActivePositionInCoin = false;

        double numberOfCoinsInPortfolio = getPosition(coin);

        if(numberOfCoinsInPortfolio > 0) {
            double currentAsk = getCurrentAskPrice(coin, BASE_COIN);

            if(numberOfCoinsInPortfolio * currentAsk > 15) {
                System.out.println("Already active position in " + coin + ". No new market buy order");
            } else {
                noActivePositionInCoin = true;
            }
        } else {
            noActivePositionInCoin = true;
        }

        return noActivePositionInCoin;
    }

    private String getSellLimit(double purchasePrice, String ticksize) {
        double sellLimit = purchasePrice * 1.009;
        DecimalFormat formatter = new DecimalFormat(ticksize);
        String sellLimitString = formatter.format(sellLimit);
        System.out.println("Profit sell limit: " + sellLimitString);
        return sellLimitString;
    }

    private String getStopPrice(double purchasePrice, String ticksize) {
        double stopPrice = purchasePrice * 0.97;
        DecimalFormat formatter = new DecimalFormat(ticksize);
        String stopPriceString = formatter.format(stopPrice);
        System.out.println("Stop price: " + stopPriceString);
        return stopPriceString;
    }

    private String getStopLimitBelowStopPrice(double purchasePrice, String ticksize) {
        double stopPriceLimit = purchasePrice * 0.83;
        DecimalFormat formatter = new DecimalFormat(ticksize);
        String stopPriceLimitString = formatter.format(stopPriceLimit);
        System.out.println("Stop price limit: " + stopPriceLimitString);
        return stopPriceLimitString;
    }

    private double getCurrentBusdBalance() {
        double currBusdBalance = -1;
        List<AssetBalance> balances = client.getAccount().getBalances();

        for(AssetBalance balance : balances) {
            if(balance.getAsset().equals("BUSD")) {
                currBusdBalance = Double.valueOf(balance.getFree());
            }
        }

        return currBusdBalance;
    }

    public void placeMarketBuyOrder(String coinToBuy, String coinToSell, String amount) {
        String tradingPair = coinToBuy + coinToSell;
        System.out.println("Market Buy order: " + coinToBuy + " Amount: " + amount);
        NewOrderResponse marketBuyOrder = client.newOrder(marketBuy(tradingPair, amount));
        System.out.println("Buy price from order response: " + marketBuyOrder.getPrice());
    }

    public void placeLimitSellOrder(String coinToSell, String coinToReceive, String amount, String limit) {
        String tradingPair = coinToSell + coinToReceive;
        client.newOrder(limitSell(tradingPair, TimeInForce.GTC, amount, limit));
    }

    private void placeStopLimitOrder(String tradingPair, String amount, String stopPrice, String limit) {
        NewOrder order = new NewOrder(tradingPair, OrderSide.SELL, OrderType.STOP_LOSS_LIMIT, TimeInForce.GTC, amount, limit);
        client.newOrder(order.stopPrice(stopPrice));
    }

//    public static void main(String [] args) {
//        new Production2().placeOcoSell("HEGICBUSD", "147.77", "0.2196", "0.1672", "0.1520");
//    }

    private void placeOcoSell(String symbol, String quantity, String price, String stopPrice, String stopLimitPrice) {
//        NewOrder ocoSell = NewOrder.ocoSell(symbol, quantity, price, stopPrice, stopLimitPrice);
//        client.newOcoOrder(ocoSell);
    }

    private double getPriceOfLastBuyTrade(String pair, long shouldBeAfterThisTime) {
        double priceToReturn = 0;
        List<Trade> allTrades = client.getMyTrades(pair);
        allTrades = allTrades.stream().filter(trade -> trade.isBuyer()).collect(Collectors.toList());

        if(!allTrades.isEmpty()) {
            Trade lastTrade = allTrades.get(allTrades.size() - 1);

            if(lastTrade.getTime() > shouldBeAfterThisTime) {
                priceToReturn = Double.valueOf(lastTrade.getPrice());
            } else {
                System.out.println("getPriceOfLastBuyTrade() -> No trades in " + pair + " since " + shouldBeAfterThisTime);
            }
        } else {
            System.out.println("getPriceOfLastBuyTrade() -> No trades at all in " + pair);
        }

        return priceToReturn;
    }

    private double getPriceOfLastBuyTradeWrapper(String pair, long shouldBeAfterThisTime) {
        double priceToReturn = 0;

        for(int i = 0; i < 300; i++) {
            if(priceToReturn != 0) {
                break;
            }

            priceToReturn = getPriceOfLastBuyTrade(pair, shouldBeAfterThisTime);
        }

        return priceToReturn;
    }

    private double getPosition(String coin) {
        double balanceOfCoin = -1;
        List<AssetBalance> balances = client.getAccount().getBalances();

        for(AssetBalance balance : balances) {
            if(balance.getAsset().equals(coin)) {
                balanceOfCoin = Double.valueOf(balance.getFree());
            }
        }

        return balanceOfCoin;
    }

    private double getCurrentAskPrice(String coinToBuy, String coinToSell) {
        OrderBook orderBook = client.getOrderBook(coinToBuy + coinToSell, 10);
        List<OrderBookEntry> asks = orderBook.getAsks();
        OrderBookEntry firstAskEntry = asks.get(0);
        return Double.valueOf(firstAskEntry.getPrice());
    }

    private String getAmountToTradeString(double amountInitial, String minQtyToTrade) {
        if(Double.valueOf(minQtyToTrade) >= 1) {
            String amountString = String.valueOf(amountInitial);
            amountString = amountString.substring(0, amountString.indexOf("."));
            return amountString;
        }

        minQtyToTrade = minQtyToTrade.substring(0, minQtyToTrade.indexOf("1") + 1);
        minQtyToTrade = minQtyToTrade.replace("1", "0");

        String amountInitialAsString = String.valueOf(amountInitial);

        if(amountInitialAsString.contains("E-")) {
            double tempAmountInitial = amountInitial + 1;
            amountInitialAsString = String.valueOf(tempAmountInitial);
            amountInitialAsString = amountInitialAsString.replaceFirst("1", "0");
        }

        String amountInitialAfterDecimal = amountInitialAsString.substring(amountInitialAsString.indexOf(".") + 1,
                amountInitialAsString.length());
        String minQtyToTradeAfterDecimal = minQtyToTrade.substring(minQtyToTrade.indexOf(".") + 1, minQtyToTrade.length());

        if(Double.valueOf(amountInitialAfterDecimal) > Double.valueOf(minQtyToTradeAfterDecimal)) {
            int diff = amountInitialAfterDecimal.length() - minQtyToTradeAfterDecimal.length();
            amountInitialAsString = amountInitialAsString.substring(0, amountInitialAsString.length() - diff);
        }

        return amountInitialAsString;
    }

    private boolean positionCanBeTraded(String minQtyToTrade, double amountToTrade, String tradingPair) {
        boolean positionCanBeTraded = true;

        if(minQtyToTrade == null) {
            positionCanBeTraded = false;
            System.out.println(tradingPair + " can't be traded because minQtyToTrade == null");
        } else {
            if(amountToTrade < Double.valueOf(minQtyToTrade)) {
                positionCanBeTraded = false;
                System.out.println(tradingPair + " can't be traded because amountToTrade < minQtyToTrade");
            }
        }

        return positionCanBeTraded;
    }



    private String getMinQtyToTrade(String coinToSell, String coinToReceive) {
        String minQtyToTrade = null;
        ExchangeInfo info = client.getExchangeInfo();
        List<SymbolInfo> allSymbolInfo = info.getSymbols();

        loop: for(SymbolInfo symbol : allSymbolInfo) {
            if(symbol.getSymbol().equals(coinToSell + coinToReceive)) {
                List<SymbolFilter> filters = symbol.getFilters();

                for(SymbolFilter filter : filters) {
                    if(filter.getFilterType().equals(FilterType.LOT_SIZE)) {
                        minQtyToTrade = filter.getMinQty();
                        break loop;
                    }
                }
            }
        }

        return minQtyToTrade;
    }

    private String getPriceLimitDecimals(String coinToSell, String coinToReceive) {
        String tickSize = null;
        ExchangeInfo info = client.getExchangeInfo();
        List<SymbolInfo> allSymbolInfo = info.getSymbols();

        loop: for(SymbolInfo symbol : allSymbolInfo) {
            if(symbol.getSymbol().equals(coinToSell + coinToReceive)) {
                List<SymbolFilter> filters = symbol.getFilters();

                for(SymbolFilter filter : filters) {
                    if(filter.getFilterType().equals(FilterType.PRICE_FILTER)) {
                        tickSize = filter.getTickSize();
                        break loop;
                    }
                }
            }
        }

        tickSize = tickSize.substring(0, tickSize.indexOf("1") + 1);
        tickSize = tickSize.replace("1", "0");
        return tickSize;
    }


    /////efkes

//    public static void main(String[] args) {
//        new Production2().placeLimitBuyOrder();
//    }

    private void placeLimitBuyOrder() {
        String tradingPair = "BTCBUSD";
        client.newOrder(limitBuy(tradingPair, TimeInForce.GTC, "0.002", "42500.09"));

    }


    private void placeMarketSell() {
        String tradingPair = "EURBUSD";
        client.newOrder(marketSell("EURBUSD", "20.31"));

    }

    private String getPriceToSell(double priceOfMarketOrder) {
        double priceToSell = priceOfMarketOrder * 1.1;
        DecimalFormat formatter = new DecimalFormat("0.00000000");
        String priceToSellAsString = formatter.format(priceToSell);
        return priceToSellAsString;
    }





    private void moveMouseIfNeeded() {
        moveMouseToLocation(7, 100);
        click(7, 100);
        moveMouseToLocation(350, 350);
    }

    public static void click(int x, int y) {
        try {
            Robot bot = new Robot();
            bot.mouseMove(x, y);
            bot.mousePress(InputEvent.BUTTON1_MASK);
            bot.mouseRelease(InputEvent.BUTTON1_MASK);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public static void moveMouseToLocation(int x, int y) {
        try {
            Robot bot = new Robot();
            bot.mouseMove(x, y);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }
}
