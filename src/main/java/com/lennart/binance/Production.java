package com.lennart.binance;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.general.ExchangeInfo;
import com.binance.api.client.domain.general.FilterType;
import com.binance.api.client.domain.general.SymbolFilter;
import com.binance.api.client.domain.general.SymbolInfo;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;

import java.awt.*;
import java.awt.event.InputEvent;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.binance.api.client.domain.account.NewOrder.limitSell;
import static com.binance.api.client.domain.account.NewOrder.marketBuy;
import static com.binance.api.client.domain.account.NewOrder.marketSell;

/**
 * Created by LennartMac on 13/05/2021.
 */
public class Production {

    //verkoop al je bestaande posities market

    //doe de analyse welke coins je wil kopen market

    //koop de coins die je wil market voor 25 eur.

    private static final double AMOUNT_TO_INVEST_PER_COIN = 25;
    private static final String BASE_COIN = "BUSD";



    private BinanceApiRestClient client;

    public Production() {
        this.client = getBinanceApiClient();
    }

    public static void main(String[] args) throws Exception {
        new Production().continuousLogic();
    }

    private void continuousLogic() throws Exception {
        int counter = 0;

        for(int i = 0; i < 800; i++) {
            try {
                moveMouseIfNeeded(counter);
                long startTime = new Date().getTime();

                System.out.println();
                System.out.print("****************** Round " + counter++ + " ******************");
                System.out.println();

                System.out.println("--Selling positions--");
                sellAllPositions();
                System.out.println();

                System.out.println("--Identifying coins to buy--");
                List<String> coinsToBuy = getCoinsToBuy();

                if(coinsToBuy.isEmpty()) {
                    System.out.println("No coins to buy");
                }
                coinsToBuy.forEach(coin -> System.out.println("Coin to buy: " + coin));
                System.out.println();

                System.out.println("--Buying new positions--");
                buyNewPositions(coinsToBuy);
                System.out.println();

                System.out.println("--Waiting 48 sec--");
                TimeUnit.SECONDS.sleep(48);
                System.out.println();

                long endTime = new Date().getTime();

                System.out.println();
                System.out.println("*** Round duration: " + (endTime - startTime) + " ***");
                System.out.println();
                System.out.println();
            } catch (Exception e) {
                System.out.println("ERROR EXCEPTION. Time: " + new Date().getTime());
                e.printStackTrace();
            }
        }
    }

    private List<String> getCoinsToBuy() {
        Momentum momentum = new Momentum();
        int startIndex = 499;
        int deltaIndex = 1;
        Map<String, Double> pctChangeBase = momentum.getPercentChangeForAllPairs(startIndex, deltaIndex);
        List<String> coinsToBuy = momentum.getCoinsToBuy(pctChangeBase, 60);
        List<String> coinsToBuy4max = coinsToBuy.stream().limit(4).collect(Collectors.toList());
        coinsToBuy4max = coinsToBuy4max.stream().map(pair -> pair.replace("BUSD", "")).collect(Collectors.toList());
        return coinsToBuy4max;
    }


    private void sellAllPositions() {
        Map<String, Double> currentPositions = getAllCurrentPositions();

        for(Map.Entry<String, Double> entry : currentPositions.entrySet()) {
            String minQtyToTrade = getMinQtyToTrade(entry.getKey(), BASE_COIN);

            if(positionCanBeTraded(minQtyToTrade, entry.getValue(), entry.getKey() + BASE_COIN, true)) {
                String amountToTradeString = getAmountToTradeString2(entry.getValue(), minQtyToTrade);
                placeMarketSellOrder(entry.getKey(), BASE_COIN, amountToTradeString);
            }
        }
    }

    private void buyNewPositions(List<String> coinsToBuy) {
        for(String coinToBuy : coinsToBuy) {
            double currentAsk = getCurrentAskPrice(coinToBuy, BASE_COIN);
            double amountToBuy = AMOUNT_TO_INVEST_PER_COIN / currentAsk;
            String minQtyToTrade = getMinQtyToTrade(coinToBuy, BASE_COIN);

            if(positionCanBeTraded(minQtyToTrade, amountToBuy, coinToBuy + BASE_COIN, false)) {
                String amountToTradeString = getAmountToTradeString2(amountToBuy, minQtyToTrade);
                placeMarketBuyOrder(coinToBuy, BASE_COIN, amountToTradeString);
            }
        }
    }

    private double getCurrentAskPrice(String coinToBuy, String coinToSell) {
        OrderBook orderBook = client.getOrderBook(coinToBuy + coinToSell, 10);
        List<OrderBookEntry> asks = orderBook.getAsks();
        OrderBookEntry firstAskEntry = asks.get(0);
        return Double.valueOf(firstAskEntry.getPrice());
    }

    private Map<String, Double> getAllCurrentPositions() {
        Map<String, Double> currentPositions = new HashMap<>();
        List<AssetBalance> assetBalances = client.getAccount().getBalances();

        for(AssetBalance assetBalance : assetBalances) {
            if(assetBalance.getFree() != null && Double.valueOf(assetBalance.getFree()) > 0) {
                currentPositions.put(assetBalance.getAsset(), Double.valueOf(assetBalance.getFree()));
            }
        }

        return currentPositions;
    }

    public void placeMarketBuyOrder(String coinToBuy, String coinToSell, String amount) {
        String tradingPair = coinToBuy + coinToSell;
        System.out.println("Market Buy order: " + coinToBuy + " Amount: " + amount);
        client.newOrder(marketBuy(tradingPair, amount));
    }

    private void placeMarketSellOrder(String coinToSell, String coinToReceive, String amount) {
        String tradingPair = coinToSell + coinToReceive;
        System.out.println("Market Sell " + coinToSell + " Amount: " + amount);
        client.newOrder(marketSell(tradingPair, amount));
    }

    private boolean amountToSellIsAboveMinQty(String coinToSell, String coinToReceive, double amount) {
        boolean amountToSellIsAboveMinQty = false;
        ExchangeInfo info = client.getExchangeInfo();
        List<SymbolInfo> allSymbolInfo = info.getSymbols();

        loop: for(SymbolInfo symbol : allSymbolInfo) {
            if(symbol.getSymbol().equals(coinToSell + coinToReceive)) {
                List<SymbolFilter> filters = symbol.getFilters();

                for(SymbolFilter filter : filters) {
                    if(filter.getFilterType().equals(FilterType.LOT_SIZE)) {
                        double minQty = Double.valueOf(filter.getMinQty());
                        amountToSellIsAboveMinQty = amount >= minQty;
                        break loop;
                    }
                }
            }
        }

        return amountToSellIsAboveMinQty;
    }

    private String getAmountToTradeString(String coin, double amountInitial) {
        ExchangeInfo info = client.getExchangeInfo();
        List<SymbolInfo> allSymbolInfo = info.getSymbols();
        String stepsize = null;

        loop: for(SymbolInfo symbol : allSymbolInfo) {
            if(symbol.getSymbol().equals(coin + BASE_COIN)) {
                List<SymbolFilter> filters = symbol.getFilters();

                for(SymbolFilter filter : filters) {
                    if(filter.getFilterType().equals(FilterType.LOT_SIZE)) {
                        stepsize = filter.getStepSize();
                        break loop;
                    }
                }
            }
        }

        stepsize = stepsize.substring(0, stepsize.indexOf("1") + 1);
        stepsize = stepsize.replace("1", "0");

        String amountAsString = String.valueOf(amountInitial);

        if(amountAsString.length() > stepsize.length()) {
            amountAsString = amountAsString.substring(0, stepsize.length());
        }

        return amountAsString;
    }

    private String getAmountToTradeString2(double amountInitial, String minQtyToTrade) {
        if(Double.valueOf(minQtyToTrade) >= 1) {
            String amountString = String.valueOf(amountInitial);
            amountString = amountString.substring(0, amountString.indexOf("."));
            return amountString;
        }

        minQtyToTrade = minQtyToTrade.substring(0, minQtyToTrade.indexOf("1") + 1);
        minQtyToTrade = minQtyToTrade.replace("1", "0");

        String amountInitialAsString = String.valueOf(amountInitial);

        String amountInitialAfterDecimal = amountInitialAsString.substring(amountInitialAsString.indexOf(".") + 1,
                amountInitialAsString.length());
        String minQtyToTradeAfterDecimal = minQtyToTrade.substring(minQtyToTrade.indexOf(".") + 1, minQtyToTrade.length());

        if(Double.valueOf(amountInitialAfterDecimal) > Double.valueOf(minQtyToTradeAfterDecimal)) {
            int diff = amountInitialAfterDecimal.length() - minQtyToTradeAfterDecimal.length();
            amountInitialAsString = amountInitialAsString.substring(0, amountInitialAsString.length() - diff);
        }

        return amountInitialAsString;
    }

    private boolean positionCanBeTraded(String minQtyToTrade, double amountToTrade, String tradingPair, boolean sell) {
        boolean positionCanBeTraded = true;

        if(minQtyToTrade == null) {
            positionCanBeTraded = false;

            if(sell) {
                System.out.println(tradingPair + " can't be sold because minQtyToTrade == null");
            } else {
                System.out.println(tradingPair + " can't be bought because minQtyToTrade == null");
            }
        } else {
            if(amountToTrade < Double.valueOf(minQtyToTrade)) {
                positionCanBeTraded = false;

                if(sell) {
                    System.out.println(tradingPair + " can't be sold because amountToTrade < minQtyToTrade");
                } else {
                    System.out.println(tradingPair + " can't be bought because amountToTrade < minQtyToTrade");
                }
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

    private BinanceApiRestClient getBinanceApiClient() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(
                "xxx",
                "xxx");
        return factory.newRestClient();
    }

    private void moveMouseIfNeeded(int counter) {
        if(counter % 2 == 0) {
            moveMouseToLocation(7, 100);
            click(7, 100);
            moveMouseToLocation(350, 350);
        }
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
