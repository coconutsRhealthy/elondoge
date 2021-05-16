package com.lennart.binance;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by LennartMac on 15/05/2021.
 */
public class CoinIdentifier {

//    public static void main(String[] args) {
//        //List<String> eije = new CoinIdentifier().getCoinsToBuy(4, 70);
//        //eije.forEach(pair -> System.out.println(pair));
//
//    }

    private void testMethode() {
        //List<String> allPairs = new Momentum().getAllBusdTradingPairs();
        //allPairs.forEach(pair -> System.out.println("busdTradingPairs.add(\"" + pair + "\");"));

        //getCoinsToBuy(4, 20);


//        BinanceClientFactory binanceClientFactory = new BinanceClientFactory();
//        BinanceApiRestClient client = binanceClientFactory.getClient();
//
//        List<Candlestick> allBtcSticks = getAllCandleSticksForPair("BTCBUSD", client);
//        Candlestick newestStick = getNewestCandlestick(allBtcSticks);
//        int secondsSince = getNumberOfSecondsSinceLastStickClose(newestStick);
//        System.out.println(secondsSince);
    }

    public List<String> getCoinsToBuy(int maxNumberOfCoins, int maxSecondsSincsLastObservation, double minimumProfit) {
        List<String> coinsToBuy = new ArrayList<>();
        Map<String, Double> profits = new HashMap<>();
        BinanceApiRestClient client = BinanceClientFactory.getBinanceApiClient();
        List<String> pairs = getAllBusdTradingPairs();

        Collections.shuffle(pairs);

        for(String pair : pairs) {
            List<Candlestick> allSticks = getAllCandleSticksForPair(pair, client);
            Candlestick newestStick = getNewestCompletedCandlestick(allSticks);
            int seconds = getNumberOfSecondsSinceNewestCompletedStickClose(newestStick);

            System.out.println("seconds: " + seconds);

            if(seconds < maxSecondsSincsLastObservation) {
                double profit = getReturn(newestStick, getSecondNewestCompletedCandlestick(allSticks));

                if(profit > minimumProfit) {
                    profits.put(pair, profit);
                }
            }
        }

        profits = sortByValueHighToLow(profits);

        if(profits.size() > maxNumberOfCoins) {
            int counter = 0;

            for(Map.Entry<String, Double> entry : profits.entrySet()) {
                counter++;

                if(counter < 5) {
                    coinsToBuy.add(entry.getKey());
                } else {
                    break;
                }
            }
        } else {
            coinsToBuy = profits.keySet().stream().collect(Collectors.toList());
        }

        return coinsToBuy;
    }

    public boolean isCoinToBuy(String coin, double minimumProfit, BinanceApiRestClient client) {
        List<Candlestick> allSticks = getAllCandleSticksForPair(coin, client);
        Candlestick newestCompletedStick = getNewestCompletedCandlestick(allSticks);

        double profit = getReturn(newestCompletedStick, getSecondNewestCompletedCandlestick(allSticks));

        if(profit > minimumProfit) {
            return true;
        }

        return false;
    }

    public List<Candlestick> getAllCandleSticksForPair(String pair, BinanceApiRestClient client) {
        return client.getCandlestickBars(pair, CandlestickInterval.ONE_MINUTE);
    }

    public static void main(String[] args) {
        new CoinIdentifier().efkesTestje();
    }

    private void efkesTestje() {
        List<Candlestick> allSticks = getAllCandleSticksForPair("BTCBUSD", BinanceClientFactory.getBinanceApiClient());

        Candlestick latest = getNewestCompletedCandlestick(allSticks);

        long currTime = new Date().getTime();
        long closeTime = latest.getCloseTime();
        long diff = currTime - closeTime;
        String open = latest.getOpen();
        String close = latest.getClose();

        System.out.println("currtime: " + currTime);
        System.out.println("closetime: " + closeTime);
        System.out.println("diff: " + diff);
        System.out.println("open: " + open);
        System.out.println("closeprice: " + close);
    }

    private Candlestick getNewestCandleStick(List<Candlestick> allCandlesticks) {
        return allCandlesticks.get(allCandlesticks.size() - 1);
    }

    private Candlestick getNewestCompletedCandlestick(List<Candlestick> allCandlesticks) {
        return allCandlesticks.get(allCandlesticks.size() - 2);
    }

    private Candlestick getSecondNewestCompletedCandlestick(List<Candlestick> allCandlesticks) {
        return allCandlesticks.get(allCandlesticks.size() - 3);
    }

    private double getReturn(Candlestick newest, Candlestick stickToCompareWith) {
        //misschien getClose()...

        double newestHigh = Double.valueOf(newest.getHigh());
        double newestLow = Double.valueOf(newest.getLow());
        double newestAverage = (newestHigh + newestLow) / 2;

        double stickToCompareHigh = Double.valueOf(stickToCompareWith.getHigh());
        double stickToCompareLow = Double.valueOf(stickToCompareWith.getLow());
        double stickToCompareAverage = (stickToCompareHigh + stickToCompareLow) / 2;

        double profit = newestAverage / stickToCompareAverage;

        return profit;

    }

    public int getNumberOfSecondsSinceNewestCompletedStickClose(Candlestick stick) {
        long stickCloseTime = stick.getCloseTime();
        long currentTime = new Date().getTime();
        long diff = currentTime - stickCloseTime;
        int seconds = (int) (diff / 1000);
        return seconds;
    }

    public List<String> getAllBusdTradingPairs() {
        List<String> busdTradingPairs = new ArrayList<>();

        busdTradingPairs.add("1INCHBUSD");
        busdTradingPairs.add("ADABUSD");
        busdTradingPairs.add("AUDBUSD");
        busdTradingPairs.add("BCHBUSD");
        busdTradingPairs.add("BNBBUSD");
        busdTradingPairs.add("BTCBUSD");
        busdTradingPairs.add("BTTBUSD");
        busdTradingPairs.add("BUSDBIDR");
        busdTradingPairs.add("BUSDBRL");
        busdTradingPairs.add("BUSDDAI");
        busdTradingPairs.add("BUSDRUB");
        busdTradingPairs.add("CAKEBUSD");
        busdTradingPairs.add("COMPBUSD");
        busdTradingPairs.add("DOGEBUSD");
        busdTradingPairs.add("DOTBUSD");
        busdTradingPairs.add("ENJBUSD");
        busdTradingPairs.add("ETCBUSD");
        busdTradingPairs.add("ETHBUSD");
        busdTradingPairs.add("EURBUSD");
        busdTradingPairs.add("FILBUSD");
        busdTradingPairs.add("FXSBUSD");
        busdTradingPairs.add("GBPBUSD");
        busdTradingPairs.add("HBARBUSD");
        busdTradingPairs.add("HEGICBUSD");
        busdTradingPairs.add("IQBUSD");
        busdTradingPairs.add("LTCBUSD");
        busdTradingPairs.add("LUNABUSD");
        busdTradingPairs.add("PAXBUSD");
        busdTradingPairs.add("REEFBUSD");
        busdTradingPairs.add("SHIBBUSD");
        busdTradingPairs.add("SUSHIBUSD");
        busdTradingPairs.add("TLMBUSD");
        busdTradingPairs.add("TUSDBUSD");
        busdTradingPairs.add("USDCBUSD");
        busdTradingPairs.add("XRPBUSD");
        busdTradingPairs.add("XVGBUSD");

        return busdTradingPairs;
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
