package com.lennart.binance;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;

import java.util.*;

/**
 * Created by LennartMac on 16/05/2021.
 */
public class BigBackTest {

    private final double BASE_NEEDED_PROFIT = 0.98;
    private final double TAKE_PROFIT = 1.011;
    private final double TAKE_LOSS = 0.95;

    private Map<String, List<Candlestick>> allSticksMap = new HashMap<>();

    public static void main(String[] args) {
        new BigBackTest().testMethod();
    }

    private void testMethodEvaluateMon17May() {
        double bankroll = 125;
        List<String> results = new ArrayList<>();
        List<String> allPairs = new CoinIdentifier().getAllBusdTradingPairs();

        Map<String, Double> amountNeededProfitMap = new HashMap<>();
        Map<String, Double> profitTotalMap = new HashMap<>();

        for(String pair : allPairs) {
            System.out.println(pair);
            List<Candlestick> allSticks = getAllCandleSticksForPair(pair, BinanceClientFactory.getBinanceApiClient());

            for(int i = 1; i < (allSticks.size() - 1); i++) {
                double profit = getProfit(allSticks.get(i - 1), allSticks.get(i));

                if(profit < BASE_NEEDED_PROFIT) {
                    if(amountNeededProfitMap.get(pair) == null) {
                        amountNeededProfitMap.put(pair, 1.0);
                    } else {
                        double oldValue = amountNeededProfitMap.get(pair);
                        double newValue = oldValue + 1;
                        amountNeededProfitMap.put(pair, newValue);
                    }

                    double purchasePrice = determinePurchasePrice(allSticks.get(i + 1));
                    double takeProfitLimit = determineProfitSellPrice(purchasePrice);
                    double takeLossLimit = determineLossSellPrice(purchasePrice);
                    List<Candlestick> remainder = getRemainderOfStickList(allSticks, i + 1);
                    String result = remainderGivesProfitOrLoss(remainder, takeProfitLimit, takeLossLimit);

                    if(result.equals("profit")) {
                        if(profitTotalMap.get(pair) == null) {
                            profitTotalMap.put(pair, 0.0);
                        }

                        double oldValue = profitTotalMap.get(pair);
                        double newValue = oldValue + 1;
                        profitTotalMap.put(pair, newValue);
                    }
                }
            }
        }

        amountNeededProfitMap = sortByValueHighToLow(amountNeededProfitMap);
        profitTotalMap = sortByValueHighToLow(profitTotalMap);

        Map<String, Double> ratioMap = new HashMap<>();

        for(Map.Entry<String, Double> entry : amountNeededProfitMap.entrySet()) {
            if(entry.getValue() >= 5) {
                double amountNeededProfitAmountOfTimes = entry.getValue();
                double profitTotalAmountOfTimes = profitTotalMap.get(entry.getKey());
                double ratio = profitTotalAmountOfTimes / amountNeededProfitAmountOfTimes;
                ratioMap.put(entry.getKey(), ratio);
            }
        }

        ratioMap = sortByValueHighToLow(ratioMap);

        printMap(ratioMap);


        //System.out.println("wacht");
    }

    private void printMap(Map<String, Double> map) {
        for(Map.Entry<String, Double> entry : map.entrySet()) {
            if(entry.getValue() >= 0.8) {
                System.out.println("busdTradingPairs.add(\"" + entry.getKey() + "\");");
            }
        }

        //map.entrySet().stream().forEach(entry -> System.out.println("busdTradingPairs.add(\"" + entry.getKey() + "\");"));
    }

    private void testMethod() {
        double bankroll = 125;
        List<String> results = new ArrayList<>();
        //List<String> allPairs = new CoinIdentifier().getAllBusdTradingPairs();
        List<String> allPairs = getAttractiveCoins();

        for(String pair : allPairs) {
            System.out.println(pair);
            List<Candlestick> allSticks = getAllCandleSticksForPair(pair, BinanceClientFactory.getBinanceApiClient());

            for(int i = 1; i < (allSticks.size() - 1); i++) {
                double profit = getProfit(allSticks.get(i - 1), allSticks.get(i));

                if(profit < BASE_NEEDED_PROFIT) {
                    double purchasePrice = determinePurchasePrice(allSticks.get(i + 1));
                    double takeProfitLimit = determineProfitSellPrice(purchasePrice);
                    double takeLossLimit = determineLossSellPrice(purchasePrice);
                    List<Candlestick> remainder = getRemainderOfStickList(allSticks, i + 1);
                    String result = remainderGivesProfitOrLoss(remainder, takeProfitLimit, takeLossLimit);
                    results.add(result);
                    bankroll = updateBankroll(result, bankroll);
                }
            }
        }

        Collections.sort(results);
        System.out.println();
        System.out.println();
        System.out.println("Profit amount: " + Collections.frequency(results, "profit"));
        System.out.println("Loss amount: " + Collections.frequency(results, "loss"));
        System.out.println("Undeciced amount: " + Collections.frequency(results, "undecided"));
        System.out.println("Total: " + results.size());
        System.out.println("bankroll: " + bankroll);
    }

    private double getProfit(Candlestick prev, Candlestick curr) {
        return Double.valueOf(curr.getClose()) / Double.valueOf(prev.getClose());
    }

    private double determinePurchasePrice(Candlestick stickAfterCurr) {
        double price = (0.4 * Double.valueOf(stickAfterCurr.getLow())) + (0.6 * Double.valueOf(stickAfterCurr.getHigh()));
        return price;
    }

    private double determineProfitSellPrice(double purchasePrice) {
        return purchasePrice * TAKE_PROFIT;
    }

    private double determineLossSellPrice(double purchasePrice) {
        return  purchasePrice * TAKE_LOSS;
    }

    private List<Candlestick> getRemainderOfStickList(List<Candlestick> allSticks, int indexOfBuyCandle) {
        List<Candlestick> remainder = new ArrayList<>();

        for(int i = 0; i < allSticks.size(); i++) {
            if(i > indexOfBuyCandle) {
                remainder.add(allSticks.get(i));
            }
        }

        return remainder;
    }

    private List<Candlestick> getAllCandleSticksForPair(String pair, BinanceApiRestClient client) {
        //List<Candlestick> sticks =

        return client.getCandlestickBars(pair, CandlestickInterval.FIVE_MINUTES);
    }

    private String remainderGivesProfitOrLoss(List<Candlestick> remainder, double profitLimit, double lossLimit) {
        String result = "undecided";

        for(Candlestick stick : remainder) {
            double high = Double.valueOf(stick.getHigh());
            double low = Double.valueOf(stick.getLow());

            if(high > profitLimit) {
                result = "profit";
                break;
            }

            if(low < lossLimit) {
                result = "loss";
                break;
            }
        }

        return result;
    }

    private double updateBankroll(String result, double currBankroll) {
        double updatedBankroll;

        if(result.equals("profit")) {
            updatedBankroll = currBankroll + (25 * ((TAKE_PROFIT - 0.001) - 1));
        } else if(result.equals("loss")) {
            updatedBankroll = currBankroll + (25 * (TAKE_LOSS - 1));
        } else {
            updatedBankroll = currBankroll;
        }

        return updatedBankroll;
    }

    public List<String> getAttractiveCoins() {
        List<String> busdTradingPairs = new ArrayList<>();

        busdTradingPairs.add("MATICBUSD");
        busdTradingPairs.add("IQBUSD");
        busdTradingPairs.add("NANOBUSD");
        busdTradingPairs.add("BIFIBUSD");
        busdTradingPairs.add("FORTHBUSD");
        busdTradingPairs.add("CREAMBUSD");
        busdTradingPairs.add("BUSDZAR");
        busdTradingPairs.add("EPSBUSD");
        busdTradingPairs.add("WRXBUSD");
        busdTradingPairs.add("PONDBUSD");
        busdTradingPairs.add("FISBUSD");
        busdTradingPairs.add("AVAXBUSD");
        busdTradingPairs.add("AVABUSD");
        busdTradingPairs.add("PSGBUSD");
        busdTradingPairs.add("CFXBUSD");
        busdTradingPairs.add("TLMBUSD");
        busdTradingPairs.add("DIABUSD");
        busdTradingPairs.add("KSMBUSD");
        busdTradingPairs.add("VETBUSD");
        busdTradingPairs.add("SRMBUSD");
        busdTradingPairs.add("HOTBUSD");
        busdTradingPairs.add("IDEXBUSD");
        busdTradingPairs.add("ALICEBUSD");

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
