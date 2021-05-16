package com.lennart.binance;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by LennartMac on 16/05/2021.
 */
public class BigBackTest {

    private final double BASE_NEEDED_PROFIT = 0.98;
    private final double TAKE_PROFIT = 1.011;
    private final double TAKE_LOSS = 0.95;

    public static void main(String[] args) {
        new BigBackTest().testMethod();
    }

    private void testMethod() {
        double bankroll = 125;
        List<String> results = new ArrayList<>();
        List<String> allPairs = new CoinIdentifier().getAllBusdTradingPairs();

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
            updatedBankroll = currBankroll + (25 * ((TAKE_PROFIT - 0.005) - 1));
        } else if(result.equals("loss")) {
            updatedBankroll = currBankroll + (25 * (TAKE_LOSS - 1));
        } else {
            updatedBankroll = currBankroll;
        }

        return updatedBankroll;
    }

}
