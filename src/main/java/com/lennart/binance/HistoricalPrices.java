package com.lennart.binance;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;

import java.util.*;

/**
 * Created by LennartMac on 11/05/2021.
 */
public class HistoricalPrices {

    private BinanceApiRestClient client;
    private Map<String, List<Candlestick>> allCandleSticks = new HashMap<>();

    public HistoricalPrices() {
        this.client = getBinanceApiClient();
    }


    public static void main(String[] args) {
        //new HistoricalPrices().getRecentPrices("BCHBUSD");
        new HistoricalPrices().testMethode();
    }

    private BinanceApiRestClient getBinanceApiClient() {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(
                "fZTmwFWYy72idRzBXhVQL1hCT6PpcpDtjhEtYocPOV2Ig5zQncUwWxAEBbSqbMIw",
                "QF5eoAFBRUvapYsswIqHPnRIIwsR950DC5Amzlp9mFJLSrOBHlESHC5Owpn8COJc");
        return factory.newRestClient();
    }

    public List<Double> getRecentPrices(String ticker) {
        List<Candlestick> candlesticks;

        if(allCandleSticks.get(ticker) != null) {
            candlesticks = allCandleSticks.get(ticker);
        } else {
            candlesticks = client.getCandlestickBars(ticker, CandlestickInterval.ONE_MINUTE);
            allCandleSticks.put(ticker, candlesticks);
        }

        List<Double> prices = new ArrayList<>();

        for(Candlestick stick : candlesticks) {
            double high = Double.valueOf(stick.getHigh());
            double low = Double.valueOf(stick.getLow());
            double average = (high + low) / 2;
            prices.add(average);
        }

        return prices;
    }

    private void testMethode() {
        List<String> allTickers = new Momentum().getAllBusdTradingPairs();

        Map<String, Double> bidAskSpreadPerTicker = new HashMap<>();

        int counter = 0;

        for(String ticker : allTickers) {
            bidAskSpreadPerTicker.put(ticker, getVolumeTimesPriceOfLastMinute(ticker));
            System.out.println(counter++);
        }

        bidAskSpreadPerTicker = sortByValueHighToLow(bidAskSpreadPerTicker);

        bidAskSpreadPerTicker.entrySet().stream().forEach(entry -> {
            System.out.println(entry.getKey() + "  " + entry.getValue());
        });
    }

    public double getBidAskSpreadPercentage(String ticker) {
        OrderBook orderBook = client.getOrderBook(ticker, 10);

        List<OrderBookEntry> asks = orderBook.getAsks();
        List<OrderBookEntry> bids = orderBook.getBids();

        if(asks.isEmpty() || bids.isEmpty()) {
            return 100;
        } else {
            OrderBookEntry firstAskEntry = asks.get(0);
            double askPrice = Double.valueOf(firstAskEntry.getPrice());
            OrderBookEntry firstBidEntry = bids.get(0);
            double bidPrice = Double.valueOf(firstBidEntry.getPrice());
            return askPrice / bidPrice;
        }
    }

    public double getVolumeTimesPriceOfLastMinute(String ticker) {
        List<Candlestick> candlesticksOfTicker = client.getCandlestickBars(ticker, CandlestickInterval.ONE_MINUTE);
        Candlestick mostRecent = candlesticksOfTicker.get(candlesticksOfTicker.size() - 1);
        double volume = Double.valueOf(mostRecent.getVolume());
        double averagePrice = (Double.valueOf(mostRecent.getHigh()) + Double.valueOf(mostRecent.getLow())) / 2;
        return volume * averagePrice;
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
