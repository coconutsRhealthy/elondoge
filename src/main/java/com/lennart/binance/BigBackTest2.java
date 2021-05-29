package com.lennart.binance;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.StatUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by LennartMac on 24/05/2021.
 */
public class BigBackTest2 {

    Map<String, List<Candlestick>> stickMap = new HashMap<>();
    List<String> allTimeStampsAsString = new ArrayList<>();
    int amountOfTrades = 0;
    int maxCoins = 0;

    public BigBackTest2(CandlestickInterval candlestickInterval) {
        fillStickMap(candlestickInterval);
        fillTimestampMap();
    }


    public static void main(String[] args) {
        new BigBackTest2(CandlestickInterval.TWELVE_HOURLY).method();
    }

    private void method() {
        //for(int maxCoinAmount = 3; maxCoinAmount <= 5; maxCoinAmount++) {
            maxCoins = 5;
            double profitBoundry = 1.04;
            //for(double d = 80; d <= 140; d = d + 10) {
                double bankroll = 100;
                List<Double> allBankrolls = new ArrayList<>();
                allBankrolls.add(bankroll);

                for(int i = 0; i < allTimeStampsAsString.size() - 1; i++) {
                    //System.out.println(i);
                    Map<String, Double> profitsForTimestamp = getProfitsForTimestamp(Long.valueOf(allTimeStampsAsString.get(i)));
                    List<String> top5 = getTop5ofSortedMap(profitsForTimestamp, profitBoundry);
                    //List<String> top5 = get5randomCoinsOfSortedMap(profitsForTimestamp);
                    Map<String, Double> profitsOfTop5AtFirstFollowingTimestamp = getProfitsOfTop5AtFirstFollowingTimestamp(top5,
                            Long.valueOf(allTimeStampsAsString.get(i + 1)));
                    bankroll = updateBankroll(bankroll, top5, profitsOfTop5AtFirstFollowingTimestamp);
                    //System.out.println("" + i + ") " + bankroll);
                    allBankrolls.add(bankroll);
                }

                System.out.println("Max coin amount: " + maxCoins);
                System.out.println("Profit boundry:" + profitBoundry) ;
                System.out.println("Amount of trades: " + amountOfTrades);
                System.out.println("FINAL BR: " + bankroll);
                System.out.println("Standard deviation: " + getStandardDeviation(allBankrolls));
                System.out.println("Buy and hold BTC BR: " + getBankrollIfBuyAndHoldBtc(100, stickMap.get("BTCBUSD")));
                System.out.println();
                Collections.sort(allBankrolls);
                System.out.println("wacht");
            //}
        //}
    }

    private double updateBankroll(double bankroll, List<String> top5, Map<String, Double> profitsTop5Tplus1) {
        Map<String, Double> amountToInvestPerCoin = getAmountToInvestPerCoin(bankroll, top5);

        amountOfTrades = amountOfTrades + amountToInvestPerCoin.size();

        for(Map.Entry<String, Double> entry : profitsTop5Tplus1.entrySet()) {
            if(amountToInvestPerCoin.get(entry.getKey()) != null) {
                bankroll = bankroll + (amountToInvestPerCoin.get(entry.getKey()) * (entry.getValue() - 0.005)) - amountToInvestPerCoin.get(entry.getKey());
            }
        }

        String bankrollString = String.valueOf(bankroll);
        bankrollString = bankrollString.replace(".", ",");
        System.out.println(bankrollString);

        return bankroll;
    }

    private Map<String, Double> getAmountToInvestPerCoin(double currentBankroll, List<String> top5) {
        if(top5.size() > maxCoins) {
            top5 = top5.stream().limit(maxCoins).collect(Collectors.toList());
        }

        Map<String, Double> amountToInvestPerCoin = new HashMap<>();

        if(currentBankroll < 25) {
            //dead
            //System.out.println("You are dead");
        } else {
            if(currentBankroll < 50) {
                if(top5.isEmpty()) {
                    //nothing
                } else {
                    amountToInvestPerCoin.put(top5.get(0), currentBankroll);
                }
            } else if(currentBankroll < 75) {
                if(top5.isEmpty()) {
                    //nothing
                } else if(top5.size() == 1) {
                    amountToInvestPerCoin.put(top5.get(0), currentBankroll);
                } else {
                    amountToInvestPerCoin.put(top5.get(0), currentBankroll / 2);
                    amountToInvestPerCoin.put(top5.get(1), currentBankroll / 2);
                }
            } else if(currentBankroll < 100) {
                if(top5.isEmpty()) {
                    //nothing
                } else if(top5.size() == 1) {
                    amountToInvestPerCoin.put(top5.get(0), currentBankroll);
                } else if(top5.size() == 2) {
                    amountToInvestPerCoin.put(top5.get(0), currentBankroll / 2);
                    amountToInvestPerCoin.put(top5.get(1), currentBankroll / 2);
                } else {
                    amountToInvestPerCoin.put(top5.get(0), currentBankroll / 3);
                    amountToInvestPerCoin.put(top5.get(1), currentBankroll / 3);
                    amountToInvestPerCoin.put(top5.get(2), currentBankroll / 3);
                }
            } else if(currentBankroll < 125) {
                if(top5.isEmpty()) {
                    //nothing
                } else if(top5.size() == 1) {
                    amountToInvestPerCoin.put(top5.get(0), currentBankroll);
                } else if(top5.size() == 2) {
                    amountToInvestPerCoin.put(top5.get(0), currentBankroll / 2);
                    amountToInvestPerCoin.put(top5.get(1), currentBankroll / 2);
                } else if(top5.size() == 3) {
                    amountToInvestPerCoin.put(top5.get(0), currentBankroll / 3);
                    amountToInvestPerCoin.put(top5.get(1), currentBankroll / 3);
                    amountToInvestPerCoin.put(top5.get(2), currentBankroll / 3);
                } else {
                    amountToInvestPerCoin.put(top5.get(0), currentBankroll / 4);
                    amountToInvestPerCoin.put(top5.get(1), currentBankroll / 4);
                    amountToInvestPerCoin.put(top5.get(2), currentBankroll / 4);
                    amountToInvestPerCoin.put(top5.get(3), currentBankroll / 4);
                }
            } else {
                if(top5.isEmpty()) {
                    //nothing
                } else if(top5.size() == 1) {
                    amountToInvestPerCoin.put(top5.get(0), currentBankroll);
                } else if(top5.size() == 2) {
                    amountToInvestPerCoin.put(top5.get(0), currentBankroll / 2);
                    amountToInvestPerCoin.put(top5.get(1), currentBankroll / 2);
                } else if(top5.size() == 3) {
                    amountToInvestPerCoin.put(top5.get(0), currentBankroll / 3);
                    amountToInvestPerCoin.put(top5.get(1), currentBankroll / 3);
                    amountToInvestPerCoin.put(top5.get(2), currentBankroll / 3);
                } else if(top5.size() == 4) {
                    amountToInvestPerCoin.put(top5.get(0), currentBankroll / 4);
                    amountToInvestPerCoin.put(top5.get(1), currentBankroll / 4);
                    amountToInvestPerCoin.put(top5.get(2), currentBankroll / 4);
                    amountToInvestPerCoin.put(top5.get(3), currentBankroll / 4);
                } else {
                    amountToInvestPerCoin.put(top5.get(0), currentBankroll / 5);
                    amountToInvestPerCoin.put(top5.get(1), currentBankroll / 5);
                    amountToInvestPerCoin.put(top5.get(2), currentBankroll / 5);
                    amountToInvestPerCoin.put(top5.get(3), currentBankroll / 5);
                    amountToInvestPerCoin.put(top5.get(4), currentBankroll / 5);
                }
            }
        }

        return amountToInvestPerCoin;
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

    private List<String> getTop5ofSortedMap(Map<String, Double> sortedProfitsForTimeT, double profitBoundry) {
        List<String> top5 = new ArrayList<>();
        int counter = 0;

        for(Map.Entry<String, Double> entry : sortedProfitsForTimeT.entrySet()) {
            if(entry.getValue() > profitBoundry) {
                counter++;

                if(counter <= 5) {
                    top5.add(entry.getKey());
                }
            }
        }

        return top5;
    }

    private List<String> get5randomCoinsOfSortedMap(Map<String, Double> sortedProfitsForTimeT) {
        if(sortedProfitsForTimeT.size() < 5) {
            return new ArrayList<>();
        } else {
            List<String> allAvailableCoins = sortedProfitsForTimeT.keySet().stream().collect(Collectors.toList());
            Set<String> randomCoins = new HashSet<>();

            while(randomCoins.size() < 5) {
                randomCoins.add(allAvailableCoins.get(new Random().nextInt(allAvailableCoins.size())));
            }

            return randomCoins.stream().collect(Collectors.toList());
        }
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

    private double getStandardDeviation(List<Double> values) {
        Double[] valuesAsArray = values.stream().toArray(Double[]::new);
        double[] valuesAsArrayDoublePrimitive = ArrayUtils.toPrimitive(valuesAsArray);
        double variance = StatUtils.variance(valuesAsArrayDoublePrimitive);
        double standardDeviation = Math.sqrt(variance);
        return standardDeviation;
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
