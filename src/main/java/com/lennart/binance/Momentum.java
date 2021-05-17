package com.lennart.binance;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by LennartMac on 12/05/2021.
 */
public class Momentum {

    private HistoricalPrices historicalPrices = new HistoricalPrices();

//    public static void main(String[] args) {
//        //new Momentum().getBestPerformer();
//    }

    public List<String> getCoinsToBuy(Map<String, Double> pctChangeForAllPairs, int amountToInclude) {
        List<String> allCoinsSortedByPct = pctChangeForAllPairs.keySet().stream().collect(Collectors.toList());
        List<String> coinsToBuy = new ArrayList<>();

        if(amountToInclude > allCoinsSortedByPct.size()) {
            amountToInclude = allCoinsSortedByPct.size();
        }

        for(int i = 0; i < amountToInclude; i++) {
            //1.005 is goed
            //1.003 ook
            //1.008 ook wel..

            //nieuw: 1.004 -> 190 trades

            if(pctChangeForAllPairs.get(allCoinsSortedByPct.get(i)) > 1.004) {
                coinsToBuy.add(allCoinsSortedByPct.get(i));
            }
        }

        return coinsToBuy;
    }

    public Map<String, Double> getProfitOfBoughtCoins(List<String> boughtCoins, Map<String, Double> pctChangeResult) {
        Map<String, Double> profitOfBoughtCoins = new HashMap<>();

        for(String boughtCoin : boughtCoins) {
            profitOfBoughtCoins.put(boughtCoin, pctChangeResult.get(boughtCoin));
        }

        profitOfBoughtCoins = sortByValueHighToLow(profitOfBoughtCoins);
        return profitOfBoughtCoins;
    }

    public Map<String, Double> getPercentChangeForAllPairs(int startIndex, int deltaIndex, String type) {
        List<String> pairs = getAllBusdTradingPairs();
        Map<String, Double> pctChanges = new HashMap<>();

        for(String pair : pairs) {
            List<Double> buyPrices = historicalPrices.getRecentPrices(pair, "high", startIndex, deltaIndex);
            List<Double> sellPrices = historicalPrices.getRecentPrices(pair, "low", startIndex, deltaIndex);

            double priceAtWhichYouSell = sellPrices.get(startIndex);
            double priceAtWhichYouBuy = buyPrices.get(startIndex - deltaIndex);
            double pctChange = priceAtWhichYouSell / priceAtWhichYouBuy;
            pctChanges.put(pair, pctChange);
        }

        pctChanges = sortByValueHighToLow(pctChanges);
        return pctChanges;
    }

    public List<String> getAllBusdTradingPairs() {
        List<String> busdTradingPairs = new ArrayList<>();

        busdTradingPairs.add("1INCHBUSD");
        busdTradingPairs.add("AAVEBUSD");
        busdTradingPairs.add("ACMBUSD");
        busdTradingPairs.add("ADABUSD");
        busdTradingPairs.add("AERGOBUSD");
        busdTradingPairs.add("ALGOBUSD");
        busdTradingPairs.add("ALICEBUSD");
        busdTradingPairs.add("ALPHABUSD");
        busdTradingPairs.add("ANTBUSD");
        busdTradingPairs.add("ATOMBUSD");
        busdTradingPairs.add("AUCTIONBUSD");
        busdTradingPairs.add("AUDBUSD");
        busdTradingPairs.add("AUDIOBUSD");
        busdTradingPairs.add("AUTOBUSD");
        busdTradingPairs.add("AVABUSD");
        busdTradingPairs.add("AVAXBUSD");
        busdTradingPairs.add("AXSBUSD");
        busdTradingPairs.add("BADGERBUSD");
        busdTradingPairs.add("BAKEBUSD");
        busdTradingPairs.add("BALBUSD");
        busdTradingPairs.add("BANDBUSD");
        busdTradingPairs.add("BARBUSD");
        busdTradingPairs.add("BATBUSD");
        busdTradingPairs.add("BCHABUSD");
        busdTradingPairs.add("BCHBUSD");
        busdTradingPairs.add("BELBUSD");
        busdTradingPairs.add("BIFIBUSD");
        busdTradingPairs.add("BNBBUSD");
        //busdTradingPairs.add("BNBUSDC");
        //busdTradingPairs.add("BNBUSDT");
        busdTradingPairs.add("BNTBUSD");
        busdTradingPairs.add("BTCBUSD");
        busdTradingPairs.add("BTCSTBUSD");
        busdTradingPairs.add("BTGBUSD");
        busdTradingPairs.add("BTTBUSD");
        busdTradingPairs.add("BURGERBUSD");
        busdTradingPairs.add("BUSDBIDR");
        busdTradingPairs.add("BUSDBRL");
        busdTradingPairs.add("BUSDBVND");
        busdTradingPairs.add("BUSDDAI");
        busdTradingPairs.add("BUSDRUB");
        //busdTradingPairs.add("BUSDTRY");
        //busdTradingPairs.add("BUSDUSDT");
        busdTradingPairs.add("BUSDVAI");
        busdTradingPairs.add("BUSDZAR");
        busdTradingPairs.add("BZRXBUSD");
        busdTradingPairs.add("CAKEBUSD");
        busdTradingPairs.add("CFXBUSD");
        busdTradingPairs.add("CHZBUSD");
        busdTradingPairs.add("CKBBUSD");
        //busdTradingPairs.add("CKBUSDT");
        busdTradingPairs.add("COMPBUSD");
        busdTradingPairs.add("COVERBUSD");
        busdTradingPairs.add("CREAMBUSD");
        busdTradingPairs.add("CRVBUSD");
        busdTradingPairs.add("CTKBUSD");
        busdTradingPairs.add("CTSIBUSD");
        busdTradingPairs.add("CVPBUSD");
        busdTradingPairs.add("DASHBUSD");
        busdTradingPairs.add("DATABUSD");
        busdTradingPairs.add("DEGOBUSD");
        busdTradingPairs.add("DEXEBUSD");
        busdTradingPairs.add("DFBUSD");
        busdTradingPairs.add("DGBBUSD");
        //busdTradingPairs.add("DGBUSDT");
        busdTradingPairs.add("DIABUSD");
        busdTradingPairs.add("DNTBUSD");
        busdTradingPairs.add("DODOBUSD");
        busdTradingPairs.add("DOGEBUSD");
        busdTradingPairs.add("DOTBUSD");
        busdTradingPairs.add("EGLDBUSD");
        busdTradingPairs.add("ENJBUSD");
        busdTradingPairs.add("EOSBUSD");
        busdTradingPairs.add("EPSBUSD");
        busdTradingPairs.add("ETCBUSD");
        busdTradingPairs.add("ETHBUSD");
        busdTradingPairs.add("EURBUSD");
        busdTradingPairs.add("FILBUSD");
        busdTradingPairs.add("FIOBUSD");
        busdTradingPairs.add("FISBUSD");
        busdTradingPairs.add("FLMBUSD");
        busdTradingPairs.add("FORBUSD");
        busdTradingPairs.add("FORTHBUSD");
        busdTradingPairs.add("FRONTBUSD");
        busdTradingPairs.add("FXSBUSD");
        busdTradingPairs.add("GBPBUSD");
        busdTradingPairs.add("GHSTBUSD");
        busdTradingPairs.add("GRTBUSD");
        busdTradingPairs.add("HARDBUSD");
        busdTradingPairs.add("HBARBUSD");
        busdTradingPairs.add("HEGICBUSD");
        busdTradingPairs.add("HOTBUSD");
        busdTradingPairs.add("ICPBUSD");
        busdTradingPairs.add("ICXBUSD");
        busdTradingPairs.add("IDEXBUSD");
        busdTradingPairs.add("INJBUSD");
        busdTradingPairs.add("IOSTBUSD");
        busdTradingPairs.add("IOTABUSD");
        busdTradingPairs.add("IQBUSD");
        busdTradingPairs.add("JSTBUSD");
        busdTradingPairs.add("JUVBUSD");
        busdTradingPairs.add("KNCBUSD");
        busdTradingPairs.add("KP3RBUSD");
        busdTradingPairs.add("KSMBUSD");
        busdTradingPairs.add("LINABUSD");
        busdTradingPairs.add("LINKBUSD");
        busdTradingPairs.add("LITBUSD");
        busdTradingPairs.add("LRCBUSD");
        busdTradingPairs.add("LTCBUSD");
        busdTradingPairs.add("LUNABUSD");
        busdTradingPairs.add("MANABUSD");
        busdTradingPairs.add("MATICBUSD");
        busdTradingPairs.add("MIRBUSD");
        busdTradingPairs.add("MKRBUSD");
        busdTradingPairs.add("NANOBUSD");
        busdTradingPairs.add("NEARBUSD");
        busdTradingPairs.add("NEOBUSD");
        busdTradingPairs.add("NMRBUSD");
        busdTradingPairs.add("OCEANBUSD");
        busdTradingPairs.add("OMBUSD");
        busdTradingPairs.add("OMGBUSD");
        busdTradingPairs.add("ONEBUSD");
        busdTradingPairs.add("ONTBUSD");
        busdTradingPairs.add("PAXBUSD");
        busdTradingPairs.add("PERPBUSD");
        busdTradingPairs.add("PHABUSD");
        busdTradingPairs.add("PONDBUSD");
        busdTradingPairs.add("PROMBUSD");
        busdTradingPairs.add("PSGBUSD");
        busdTradingPairs.add("QTUMBUSD");
        busdTradingPairs.add("RAMPBUSD");
        busdTradingPairs.add("REEFBUSD");
        busdTradingPairs.add("ROSEBUSD");
        busdTradingPairs.add("RSRBUSD");
        busdTradingPairs.add("RUNEBUSD");
        busdTradingPairs.add("RVNBUSD");
        busdTradingPairs.add("SANDBUSD");
        busdTradingPairs.add("SFPBUSD");
        busdTradingPairs.add("SHIBBUSD");
        //busdTradingPairs.add("SHIBUSDT");
        busdTradingPairs.add("SKLBUSD");
        busdTradingPairs.add("SLPBUSD");
        busdTradingPairs.add("SNXBUSD");
        busdTradingPairs.add("SOLBUSD");
        busdTradingPairs.add("SRMBUSD");
        busdTradingPairs.add("STRAXBUSD");
        busdTradingPairs.add("SUPERBUSD");
        busdTradingPairs.add("SUSHIBUSD");
        busdTradingPairs.add("SWRVBUSD");
        busdTradingPairs.add("SXPBUSD");
        busdTradingPairs.add("SYSBUSD");
        busdTradingPairs.add("TKOBUSD");
        busdTradingPairs.add("TLMBUSD");
        busdTradingPairs.add("TOMOBUSD");
        busdTradingPairs.add("TRBBUSD");
        //busdTradingPairs.add("TRBUSDT");
        busdTradingPairs.add("TRUBUSD");
        busdTradingPairs.add("TRXBUSD");
        busdTradingPairs.add("TUSDBUSD");
        busdTradingPairs.add("TVKBUSD");
        busdTradingPairs.add("TWTBUSD");
        busdTradingPairs.add("UFTBUSD");
        busdTradingPairs.add("UNFIBUSD");
        busdTradingPairs.add("UNIBUSD");
        busdTradingPairs.add("USDCBUSD");
        busdTradingPairs.add("VETBUSD");
        busdTradingPairs.add("VIDTBUSD");
        busdTradingPairs.add("WAVESBUSD");
        busdTradingPairs.add("WINGBUSD");
        busdTradingPairs.add("WRXBUSD");
        busdTradingPairs.add("XLMBUSD");
        busdTradingPairs.add("XMRBUSD");
        busdTradingPairs.add("XRPBUSD");
        busdTradingPairs.add("XTZBUSD");
        busdTradingPairs.add("XVGBUSD");
        busdTradingPairs.add("XVSBUSD");
        busdTradingPairs.add("YFIBUSD");
        busdTradingPairs.add("YFIIBUSD");
        busdTradingPairs.add("ZECBUSD");
        busdTradingPairs.add("ZILBUSD");
        busdTradingPairs.add("ZRXBUSD");

        busdTradingPairs = filterOutBadBidAskSpreadPairs(busdTradingPairs);

        return busdTradingPairs;
    }

    private List<String> filterOutBadBidAskSpreadPairs(List<String> initialPairList) {
        List<String> filteredList = new ArrayList<>();
        Map<String, Double> spreadMap = new BackTest().getBidAskSpreadMap();

        for(String pair : initialPairList) {
            try {
                double spread = spreadMap.get(pair);

                if(spread < 0.001) {
                    filteredList.add(pair);
                }
            } catch (Exception e) {
                System.out.println("EXCEPTION FOR PAIR: " + pair);
            }
        }

        return filteredList;
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
