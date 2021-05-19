package com.lennart.binance;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by LennartMac on 16/05/2021.
 */
public class BigBackTest {

    private double baseNeededProfit = 0.98;
    private double takeProfit = 1.011;
    private double takeLoss = 0.95;

    private double tempMachineLearningSucces = -1;
    private double tempMachineLearningTotal = -1;

    //private Map<String, List<Candlestick>> allSticksMap = new HashMap<>();

//    public static void main(String[] args) {
//        new BigBackTest().getAttractiveCoinsDynamically();
//    }

    public List<String> getAttractiveCoinsDynamically() {
        List<String> allPairs = new CoinIdentifier().getAllBusdTradingPairs();

        Map<String, Double> amountNeededProfitMap = new HashMap<>();
        Map<String, Double> profitTotalMap = new HashMap<>();

        for(String pair : allPairs) {
            List<Candlestick> allSticks = getAllCandleSticksForPair(pair, BinanceClientFactory.getBinanceApiClient());

            for(int i = 1; i < (allSticks.size() - 1); i++) {
                double profit = getProfit(allSticks.get(i - 1), allSticks.get(i));

                if(profit < baseNeededProfit) {
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

                    if(profitTotalMap.get(pair) == null) {
                        profitTotalMap.put(pair, 0.0);
                    }

                    if(result.equals("profit")) {
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
        ratioMap = ratioMap.entrySet().stream()
                .filter(entry -> entry.getValue() >= 0.8)
                .collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));

        List<String> attractiveCoins = ratioMap.keySet().stream().collect(Collectors.toList());
        Collections.sort(attractiveCoins);
        return attractiveCoins;
    }

    private void printMap(Map<String, Double> map) {
        for(Map.Entry<String, Double> entry : map.entrySet()) {
            if(entry.getValue() >= 0.8) {
                System.out.println("busdTradingPairs.add(\"" + entry.getKey() + "\");");
            }
        }

        //map.entrySet().stream().forEach(entry -> System.out.println("busdTradingPairs.add(\"" + entry.getKey() + "\");"));
    }

    private double getBankrollResultGivenInput(double bankroll) {
        List<String> results = new ArrayList<>();
        List<String> allPairs = getAttractiveCoinsDynamically();

        for(String pair : allPairs) {
            //System.out.println(pair);
            List<Candlestick> allSticks = getAllCandleSticksForPair(pair, BinanceClientFactory.getBinanceApiClient());

            for(int i = 1; i < (allSticks.size() - 1); i++) {
                double profit = getProfit(allSticks.get(i - 1), allSticks.get(i));

                if(profit < baseNeededProfit) {
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

        tempMachineLearningSucces = (double) Collections.frequency(results, "profit");
        tempMachineLearningTotal = results.size();

        System.out.println();
        System.out.println();
        System.out.println("Profit amount: " + Collections.frequency(results, "profit"));
        System.out.println("Loss amount: " + Collections.frequency(results, "loss"));
        System.out.println("Undeciced amount: " + Collections.frequency(results, "undecided"));
        System.out.println("Total: " + results.size());
        System.out.println("bankroll: " + bankroll);

        return bankroll;
    }

    private void machineLearning() {
        Map<List<Double>, Double> comboProfitMap = new HashMap<>();

        List<Double> baseNeededProfitList = getBaseNeededProfitList();
        List<Double> takeProfitList = getTakeProfitList();
        List<Double> takeLossList = getTakeLossList();

        int counter = 0;

        for(Double a : baseNeededProfitList) {
            for(Double b : takeProfitList) {
                for(Double c : takeLossList) {
                    System.out.println(counter++);

                    baseNeededProfit = a;
                    takeProfit = b;
                    takeLoss = c;

                    double profit = getBankrollResultGivenInput(100);
                    comboProfitMap.put(Arrays.asList(baseNeededProfit,  takeProfit, takeLoss, tempMachineLearningSucces,
                            tempMachineLearningTotal), profit);
                }
            }
        }

        comboProfitMap = sortByValueHighToLow(comboProfitMap);

        System.out.println("wacht");
    }

    private List<Double> getBaseNeededProfitList() {
        List<Double> baseNeededProfitList = new ArrayList<>();

        for(double d = 0.97; d < 1; d = d + 0.01) {
            baseNeededProfitList.add(d);
        }

        return baseNeededProfitList;
    }

    private List<Double> getTakeProfitList() {
        List<Double> takeProfitList = new ArrayList<>();

        for(double d = 1.01; d <= 1.04; d = d + 0.01) {
            takeProfitList.add(d);
        }

        return takeProfitList;
    }

    private List<Double> getTakeLossList() {
        List<Double> takeLossList = new ArrayList<>();

        for(double d = 0.90; d < 1.00; d = d + 0.01) {
            takeLossList.add(d);
        }

        return takeLossList;
    }

    private double getProfit(Candlestick prev, Candlestick curr) {
        return Double.valueOf(curr.getClose()) / Double.valueOf(prev.getClose());
    }

    private double determinePurchasePrice(Candlestick stickAfterCurr) {
        double price = (0.4 * Double.valueOf(stickAfterCurr.getLow())) + (0.6 * Double.valueOf(stickAfterCurr.getHigh()));
        return price;
    }

    private double determineProfitSellPrice(double purchasePrice) {
        return purchasePrice * takeProfit;
    }

    private double determineLossSellPrice(double purchasePrice) {
        return  purchasePrice * takeLoss;
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
        List<Candlestick> allStickForPair;

//        if(allSticksMap.get(pair) == null) {
            allStickForPair = client.getCandlestickBars(pair, CandlestickInterval.FIVE_MINUTES);
//            allSticksMap.put(pair, allStickForPair);
//        } else {
//            allStickForPair = allSticksMap.get(pair);
//        }

        return allStickForPair;
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
            updatedBankroll = currBankroll + (25 * ((takeProfit - 0.002) - 1));
            //updatedBankroll = currBankroll + 0.21;
        } else if(result.equals("loss")) {
            updatedBankroll = currBankroll + (25 * (takeLoss - 1.00555));
            //updatedBankroll = currBankroll - 1.4;
        } else {
            updatedBankroll = currBankroll + (25 * (takeLoss - 1.00555));
            //updatedBankroll = currBankroll - 1.4;
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
