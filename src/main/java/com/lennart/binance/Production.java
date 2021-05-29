package com.lennart.binance;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.Trade;
import com.binance.api.client.domain.general.ExchangeInfo;
import com.binance.api.client.domain.general.FilterType;
import com.binance.api.client.domain.general.SymbolFilter;
import com.binance.api.client.domain.general.SymbolInfo;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;

import java.awt.*;
import java.awt.event.InputEvent;
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

    private static final double AMOUNT_TO_INVEST_PER_COIN = 25;
    private static final String BASE_COIN = "BUSD";



    private BinanceApiRestClient client;

    public Production() {
        this.client = BinanceClientFactory.getBinanceApiClient();
    }

    public static void main(String[] args) throws Exception {
        new Production().continuousLogic();
    }

    private void continuousLogic() throws Exception {
        while(true) {
            try {
                if(twelveHourCandleAlmostFinished()) {
                    System.out.println("Time to trade!  Timestamp: " + new Date().getTime());

                    System.out.println("--Selling old positions--");
                    sellAllPositions();

                    List<String> coinsToBuy = getCoinsToBuyTwelveHour();
                    coinsToBuy.forEach(coin -> System.out.println("Coin to buy: " + coin));

                    System.out.println("--Buying new positions--");
                    buyNewPositions(coinsToBuy);

                    TimeUnit.MINUTES.sleep(35);
                } else {
                    TimeUnit.MINUTES.sleep(5);
                }
            } catch (Exception e) {
                System.out.println("ERROR EXCEPTION. Time: " + new Date().getTime());
                e.printStackTrace();
                TimeUnit.MINUTES.sleep(35);
            }
        }
    }

    private boolean twelveHourCandleAlmostFinished() {
        List<Candlestick> btcSticks = client.getCandlestickBars("BTCBUSD", CandlestickInterval.TWELVE_HOURLY);
        Candlestick lastFinishedBtcStick = btcSticks.get(btcSticks.size() - 2);
        long closeTime = lastFinishedBtcStick.getCloseTime();
        long currentTime = new Date().getTime();
        long diffMillies = currentTime - closeTime;
        long diffSeconds = diffMillies / 1000;
        long diffMinutes = diffSeconds / 60;
        int elevenHoursAndFiftyMinutes = 710;

        if(diffMinutes > elevenHoursAndFiftyMinutes) {
            return true;
        }

        return false;
    }

    private double getCurrentBusdBalance() {
        return 0;
    }

    private List<String> getCoinsToBuy() {
        Momentum momentum = new Momentum();
        int startIndex = 499;
        int deltaIndex = 1;
        Map<String, Double> pctChangeBase = momentum.getPercentChangeForAllPairs(startIndex, deltaIndex, null);
        List<String> coinsToBuy = momentum.getCoinsToBuy(pctChangeBase, 60);
        List<String> coinsToBuy4max = coinsToBuy.stream().limit(4).collect(Collectors.toList());
        coinsToBuy4max = coinsToBuy4max.stream().map(pair -> pair.replace("BUSD", "")).collect(Collectors.toList());
        return coinsToBuy4max;
    }

    private List<String> getCoinsToBuyTwelveHour() {
        List<String> pairs = new CoinIdentifier().getAllBusdTradingPairs();
        Map<String, List<Candlestick>> stickMap = new HashMap<>();

        for(String pair : pairs) {
            stickMap.put(pair, client.getCandlestickBars(pair, CandlestickInterval.TWELVE_HOURLY));
        }

        Map<String, Double> profitOfLastCandleStick = new HashMap<>();

        for(Map.Entry<String, List<Candlestick>> entry : stickMap.entrySet()) {
            Candlestick mostRecentStick = entry.getValue().get(entry.getValue().size() - 1);
            Candlestick secondMostRecentStick = entry.getValue().get(entry.getValue().size() - 2);
            double mostRecentClosePrice = Double.valueOf(mostRecentStick.getClose());
            double secondMostRecentClosePrice = Double.valueOf(secondMostRecentStick.getClose());
            double profit = mostRecentClosePrice / secondMostRecentClosePrice;
            profitOfLastCandleStick.put(entry.getKey(), profit);
        }

        profitOfLastCandleStick = sortByValueHighToLow(profitOfLastCandleStick);
        List<String> coinsToBuyTwelveHour = profitOfLastCandleStick.keySet().stream().limit(5).collect(Collectors.toList());
        coinsToBuyTwelveHour = coinsToBuyTwelveHour.stream().map(coin -> coin.replace("BUSD", "")).collect(Collectors.toList());

        return coinsToBuyTwelveHour;
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

    private void placeLimitSellOrderEIJE(String coin, double amount, double limit) {
        String minQtyToTrade = getMinQtyToTrade(coin, BASE_COIN);

        if(positionCanBeTraded(minQtyToTrade, amount, coin + BASE_COIN, true)) {
            String amountToTradeString = getAmountToTradeString2(amount, minQtyToTrade);
            double priceOfLastTrade = getPriceOfLastTrade(coin + BASE_COIN);
            placeLimitSellOrder(coin, BASE_COIN, amountToTradeString, String.valueOf(priceOfLastTrade * 1.004));
        }
    }

    private void buyNewPositions(List<String> coinsToBuy) {
        for(String coinToBuy : coinsToBuy) {
            buyNewPosition(coinToBuy);
        }
    }

    private void buyNewPosition(String coinToBuy) {
        double currentAsk = getCurrentAskPrice(coinToBuy, BASE_COIN);
        double amountToBuy = AMOUNT_TO_INVEST_PER_COIN / currentAsk;
        String minQtyToTrade = getMinQtyToTrade(coinToBuy, BASE_COIN);

        if(positionCanBeTraded(minQtyToTrade, amountToBuy, coinToBuy + BASE_COIN, false)) {
            String amountToTradeString = getAmountToTradeString2(amountToBuy, minQtyToTrade);
            placeMarketBuyOrder(coinToBuy, BASE_COIN, amountToTradeString);
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

    public void placeLimitSellOrder(String coinToSell, String coinToReceive, String amount, String limit) {
        String tradingPair = coinToSell + coinToReceive;
        client.newOrder(limitSell(tradingPair, TimeInForce.GTC, amount, limit));
    }

    private void placeStopLimitOrder(String tradingPair, String amount, String stopPrice, String limit) {
        NewOrder order = new NewOrder(tradingPair, OrderSide.SELL, OrderType.STOP_LOSS_LIMIT, TimeInForce.GTC, amount, limit);
        client.newOrder(order.stopPrice(stopPrice));
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

    public double getPriceOfLastTrade(String pair) {
        double priceToReturn = 0;
        List<Trade> allTrades = client.getMyTrades(pair);

        if(!allTrades.isEmpty()) {
            Trade lastTrade = allTrades.get(allTrades.size() - 1);
            priceToReturn = Double.valueOf(lastTrade.getPrice());
        }

        return priceToReturn;
    }

    private BinanceApiRestClient getBinanceApiClient() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(
                "jeWCbhJx1Smghax75XupLI51BW1IRD4oJm7PeCBrQ7xz89hUQ4UjqVcq8EyOEBD6",
                "yYGe5sFsHqZDhhg79LW3OhjOOZrhxWELO4vMLMfbtNYNU6XcyijQaCARzIUROuK7");
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

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValueHighToLow(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>( map.entrySet() );
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue() ).compareTo( o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
