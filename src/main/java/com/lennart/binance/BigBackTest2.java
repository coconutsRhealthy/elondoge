package com.lennart.binance;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;

import java.util.*;

/**
 * Created by LennartMac on 24/05/2021.
 */
public class BigBackTest2 {

    Map<String, List<Candlestick>> stickMap = new HashMap<>();
    List<String> allTimeStampsAsString = new ArrayList<>();

    public BigBackTest2(CandlestickInterval candlestickInterval) {
        fillStickMap(candlestickInterval);
        fillTimestampMap();
    }


    public static void main(String[] args) {
        new BigBackTest2(CandlestickInterval.TWELVE_HOURLY).method();
    }

    private void method() {
        double bankroll = 100;

        for(int i = 0; i < allTimeStampsAsString.size() - 1; i++) {
            System.out.println(i);
            Map<String, Double> profitsForTimestamp = getProfitsForTimestamp(Long.valueOf(allTimeStampsAsString.get(i)));
            List<String> top5 = getTop5ofSortedMap(profitsForTimestamp);
            Map<String, Double> profitsOfTop5AtFirstFollowingTimestamp = getProfitsOfTop5AtFirstFollowingTimestamp(top5,
                    Long.valueOf(allTimeStampsAsString.get(i + 1)));
            bankroll = updateBankroll(bankroll, profitsOfTop5AtFirstFollowingTimestamp);
        }

        System.out.println("FINAL BR: " + bankroll);
        System.out.println("Buy and hold BTC BR: " + getBankrollIfBuyAndHoldBtc(100, stickMap.get("BTCBUSD")));
    }

    private double updateBankroll(double bankroll, Map<String, Double> profitsTop5Tplus1) {
        for(Map.Entry<String, Double> entry : profitsTop5Tplus1.entrySet()) {
            bankroll = bankroll + (25 * (entry.getValue() - 0.005)) - 25;
        }

        return bankroll;
    }

    private double getBankrollIfBuyAndHoldBtc(double startBankroll, List<Candlestick> btcCandlesticks) {
        double openFirstCandle = Double.valueOf(btcCandlesticks.get(0).getOpen());
        double closeLastCandle = Double.valueOf(btcCandlesticks.get(btcCandlesticks.size() - 1).getClose());
        double roi = closeLastCandle / openFirstCandle;
        double totalBankroll = startBankroll * roi;
        return totalBankroll;
    }

    private Map<String, Double> getProfitsOfTop5AtFirstFollowingTimestamp(List<String> top5, long firstFollowingTimestamp) {
        Map<String, Double> top5profitsTplus1 = new HashMap<>();
        Map<String, Double> profitsForTimeTplus1 = getProfitsForTimestamp(firstFollowingTimestamp);

        for(String top5member : top5) {
            double profitTplus1 = profitsForTimeTplus1.get(top5member);
            top5profitsTplus1.put(top5member, profitTplus1);
        }

        top5profitsTplus1 = sortByValueHighToLow(top5profitsTplus1);
        return top5profitsTplus1;
    }

    private List<String> getTop5ofSortedMap(Map<String, Double> sortedProfitsForTimeT) {
        List<String> top5 = new ArrayList<>();
        int counter = 0;

        for(Map.Entry<String, Double> entry : sortedProfitsForTimeT.entrySet()) {
            counter++;

            if(counter <= 5) {
                top5.add(entry.getKey());
            }
        }

        return top5;
    }

    private Map<String, Double> getProfitsForTimestamp(long timestamp) {
        String timestampString = String.valueOf(timestamp);
        Map<String, Double> profitsForTimestamp = new HashMap<>();

        Map<String, Map<Long, Double>> profitMap = getProfitMap();

        for(Map.Entry<String, Map<Long, Double>> entry : profitMap.entrySet()) {
            if(entry.getValue().get(Long.valueOf(timestampString)) != null) {
                profitsForTimestamp.put(entry.getKey(), entry.getValue().get(Long.valueOf(timestampString)));
            }
        }

        profitsForTimestamp = sortByValueHighToLow(profitsForTimestamp);

        return profitsForTimestamp;
    }

    private void fillStickMap(CandlestickInterval candlestickInterval) {
        List<String> pairs = new CoinIdentifier().getAllBusdTradingPairs();
        BinanceApiRestClient client = BinanceClientFactory.getBinanceApiClient();

        for(String pair : pairs) {
            System.out.println(pair);
            stickMap.put(pair, client.getCandlestickBars(pair, candlestickInterval));
        }
    }

    private void fillTimestampMap() {
        List<Candlestick> btcSticks = stickMap.get("BTCBUSD");

        btcSticks.forEach(stick -> {
            allTimeStampsAsString.add(String.valueOf(stick.getOpenTime()));
        });
    }

    private Map<String, Map<Long, Double>> getProfitMap() {
        Map<String, Map<Long, Double>> profitsMap = new HashMap<>();

        for(Map.Entry<String, List<Candlestick>> entry : stickMap.entrySet()) {
            Map<Long, Double> profitsForCoin = getProfitsForCoin(entry.getValue());
            profitsMap.put(entry.getKey(), profitsForCoin);
        }

        return profitsMap;
    }

    private Map<Long, Double> getProfitsForCoin(List<Candlestick> sticks) {
        Map<Long, Double> profitsForCoin = new HashMap<>();

        profitsForCoin.put((long) 0, (double) 0);

        for(int i = 1; i < sticks.size(); i++) {
            double profit = Double.valueOf(sticks.get(i).getClose()) / Double.valueOf(sticks.get(i - 1).getClose());
            profitsForCoin.put(sticks.get(i).getOpenTime(), profit);
        }

        return profitsForCoin;
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
