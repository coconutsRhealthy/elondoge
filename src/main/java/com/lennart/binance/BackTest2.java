package com.lennart.binance;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by LennartMac on 14/05/2021.
 */
public class BackTest2 {

    private BinanceApiRestClient client;

    public BackTest2() {
        this.client = getBinanceApiClient();
    }

    public static void main(String[] args) {
        new BackTest2().overallTestMethod();
    }

    private void overallTestMethod() {
        List<Candlestick> allSticksForPair = getAllCandleSticksForPair("BTCBUSD");
        List<Long> epochTimesOfRises = getEpochTimesOfConsecutiveRiseMinutes(allSticksForPair, 5);

        List<Double> totalProfit = new ArrayList<>();

        for(Long time : epochTimesOfRises) {
            double buyPrice = getPriceAtWhichYouCouldBuyCoin(allSticksForPair, time);
            double sellPrice = getPriceAtWhichYouCouldSellCoin(allSticksForPair, time);

            if(buyPrice != -1 && sellPrice != -1) {
                double profit = sellPrice - buyPrice;
                totalProfit.add(profit);
            }
        }

        double profitsum = 0;

        for(Double profit : totalProfit) {
            profitsum = profitsum + profit;
        }

        System.out.println(profitsum);
    }



    private void testje() {
        List<Candlestick> sticks = getAllCandleSticksForPair("DOGEBUSD");
        List<Long> times = getEpochTimesOfConsecutiveRiseMinutes(sticks, 5);
        System.out.println("wacht");
    }


    private List<Candlestick> getAllCandleSticksForPair(String pair) {
        return client.getCandlestickBars(pair, CandlestickInterval.ONE_MINUTE);
    }

    private List<Long> getEpochTimesOfConsecutiveRiseMinutes(List<Candlestick> allCandleSticksForPair, int numberOfConsecutiveRises) {
        List<Long> consecutiveRises = new ArrayList<>();

        for(int i = 0; i < allCandleSticksForPair.size(); i++) {
            try {
                if(numberOfConsecutiveRises == 5) {
                    Candlestick stickMinus5 = allCandleSticksForPair.get(i - 5);
                    Candlestick stickMinus4 = allCandleSticksForPair.get(i - 4);
                    Candlestick stickMinus3 = allCandleSticksForPair.get(i - 3);
                    Candlestick stickMinus2 = allCandleSticksForPair.get(i - 2);
                    Candlestick stickMinus1 = allCandleSticksForPair.get(i - 1);
                    Candlestick stick0 = allCandleSticksForPair.get(i);

                    double stickCloseMinus5 = Double.valueOf(stickMinus5.getClose());
                    double stickCloseMinus4 = Double.valueOf(stickMinus4.getClose());
                    double stickCloseMinus3 = Double.valueOf(stickMinus3.getClose());
                    double stickCloseMinus2 = Double.valueOf(stickMinus2.getClose());
                    double stickCloseMinus1 = Double.valueOf(stickMinus1.getClose());
                    double stickClose0 = Double.valueOf(stick0.getClose());

                    if(stickClose0 < stickCloseMinus1 && stickCloseMinus1 < stickCloseMinus2 && stickCloseMinus2 < stickCloseMinus3
                            && stickCloseMinus3 < stickCloseMinus4 && stickCloseMinus4 < stickCloseMinus5) {
                        consecutiveRises.add(stick0.getCloseTime());
                    }
                } else if(numberOfConsecutiveRises == 4) {
                    Candlestick stickMinus4 = allCandleSticksForPair.get(i - 4);
                    Candlestick stickMinus3 = allCandleSticksForPair.get(i - 3);
                    Candlestick stickMinus2 = allCandleSticksForPair.get(i - 2);
                    Candlestick stickMinus1 = allCandleSticksForPair.get(i - 1);
                    Candlestick stick0 = allCandleSticksForPair.get(i);

                    double stickCloseMinus4 = Double.valueOf(stickMinus4.getClose());
                    double stickCloseMinus3 = Double.valueOf(stickMinus3.getClose());
                    double stickCloseMinus2 = Double.valueOf(stickMinus2.getClose());
                    double stickCloseMinus1 = Double.valueOf(stickMinus1.getClose());
                    double stickClose0 = Double.valueOf(stick0.getClose());

                    if(stickClose0 > stickCloseMinus1 && stickCloseMinus1 > stickCloseMinus2 && stickCloseMinus2 > stickCloseMinus3
                            && stickCloseMinus3 > stickCloseMinus4) {
                        consecutiveRises.add(stick0.getCloseTime());
                    }
                } else if(numberOfConsecutiveRises == 3) {
                    Candlestick stickMinus3 = allCandleSticksForPair.get(i - 3);
                    Candlestick stickMinus2 = allCandleSticksForPair.get(i - 2);
                    Candlestick stickMinus1 = allCandleSticksForPair.get(i - 1);
                    Candlestick stick0 = allCandleSticksForPair.get(i);

                    double stickCloseMinus3 = Double.valueOf(stickMinus3.getClose());
                    double stickCloseMinus2 = Double.valueOf(stickMinus2.getClose());
                    double stickCloseMinus1 = Double.valueOf(stickMinus1.getClose());
                    double stickClose0 = Double.valueOf(stick0.getClose());

                    if(stickClose0 > stickCloseMinus1 && stickCloseMinus1 > stickCloseMinus2 && stickCloseMinus2 > stickCloseMinus3) {
                        consecutiveRises.add(stick0.getCloseTime());
                    }
                } else if(numberOfConsecutiveRises == 2) {
                    Candlestick stickMinus2 = allCandleSticksForPair.get(i - 2);
                    Candlestick stickMinus1 = allCandleSticksForPair.get(i - 1);
                    Candlestick stick0 = allCandleSticksForPair.get(i);

                    double stickCloseMinus2 = Double.valueOf(stickMinus2.getClose());
                    double stickCloseMinus1 = Double.valueOf(stickMinus1.getClose());
                    double stickClose0 = Double.valueOf(stick0.getClose());

                    if(stickClose0 > stickCloseMinus1 && stickCloseMinus1 > stickCloseMinus2) {
                        consecutiveRises.add(stick0.getCloseTime());
                    }
                } else if(numberOfConsecutiveRises == 1) {
                    Candlestick stickMinus1 = allCandleSticksForPair.get(i - 1);
                    Candlestick stick0 = allCandleSticksForPair.get(i);

                    double stickCloseMinus1 = Double.valueOf(stickMinus1.getClose());
                    double stickClose0 = Double.valueOf(stick0.getClose());

                    if(stickClose0 > stickCloseMinus1) {
                        consecutiveRises.add(stick0.getCloseTime());
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                System.out.println("prrt");
            }
        }

        Set<Long> asSet = consecutiveRises.stream().collect(Collectors.toSet());
        List<Long> toReturn = asSet.stream().collect(Collectors.toList());
        Collections.sort(toReturn);
        return toReturn;
    }

    private double getPriceAtWhichYouCouldBuyCoin(List<Candlestick> allSticksForCoin, long epochTimeOfSignal) {
        double buyPrice = -1;

        for(Candlestick stick : allSticksForCoin) {
            if(stick.getOpenTime() > epochTimeOfSignal) {
                buyPrice = Double.valueOf(stick.getHigh());
                break;
            }
        }

        return buyPrice;
    }

    private double getPriceAtWhichYouCouldSellCoin(List<Candlestick> allSticksForCoin, long epochTimeOfSignal) {
        double sellPrice = -1;

        for(Candlestick stick : allSticksForCoin) {
            if(stick.getOpenTime() > epochTimeOfSignal + 60_000) {
                sellPrice = Double.valueOf(stick.getHigh());
                break;
            }
        }

        return sellPrice;
    }

    private BinanceApiRestClient getBinanceApiClient() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(
                "zzz",
                "zzd");
        return factory.newRestClient();
    }
}
