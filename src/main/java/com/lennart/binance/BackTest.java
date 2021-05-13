package com.lennart.binance;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by LennartMac on 12/05/2021.
 */
public class BackTest {

    Map<String, Double> bidAskSpreadMap = getBidAskSpreadMap();

    public static void main(String[] args) {
        new BackTest().doTheBackTest();
    }

    private void doTheBackTest() {
        double bankroll = 100;
        int intBankroll;

        List<String> coinsToBuy;
        int startIndex = 1;
        int deltaIndex = 1;

        Momentum momentum = new Momentum();
        //initializeBidAskSpreadMap();

        Map<String, Double> pctChangeBase = momentum.getPercentChangeForAllPairs(startIndex, deltaIndex);
        pctChangeBase = correctForBidAskSpread(pctChangeBase);
        pctChangeBase = correctProfitsForTradingCosts(pctChangeBase);

        int amountOfTrades = 0;

        for(int i = startIndex; i < (startIndex + 499); i++) {
            coinsToBuy = momentum.getCoinsToBuy(pctChangeBase, 60);

            //als je al een positie hebt, en hij is voor de volgende ronde weer geidentificeerd, dan moet je hem houden...

//            if(Math.random() > 0.57) {
//                coinsToBuy = getRandomCoins(1);
//            } else {
//                coinsToBuy = new ArrayList<>();
//            }

            Map<String, Double> pctChangeResult = momentum.getPercentChangeForAllPairs(i, deltaIndex);
            pctChangeResult = correctForBidAskSpread(pctChangeResult);
            pctChangeResult = correctProfitsForTradingCosts(pctChangeResult);
            Map<String, Double> profits = momentum.getProfitOfBoughtCoins(coinsToBuy, pctChangeResult);

            for(Map.Entry<String, Double> entry : profits.entrySet()) {
                bankroll = bankroll * entry.getValue();
                intBankroll = (int) bankroll;
                System.out.println(intBankroll);
                amountOfTrades = amountOfTrades + 2;
                //System.out.println("pair: " + entry.getKey());
            }

            pctChangeBase = new HashMap<>();
            pctChangeBase.putAll(pctChangeResult);
            pctChangeBase = sortByValueHighToLow(pctChangeBase);
        }

        System.out.println("Final bankroll: " + bankroll);
        System.out.println("Amount of trades: " + amountOfTrades);
    }

    private List<String> getRandomCoins(int amount) {
        List<String> allCoins = new Momentum().getAllBusdTradingPairs();
        List<String> randomCoins = new ArrayList<>();

        for(int i = 0; i < amount; i++) {
            Random randomizer = new Random();
            String randomCoin = allCoins.get(randomizer.nextInt(allCoins.size()));
            randomCoins.add(randomCoin);
        }

        return randomCoins;
    }

    private Map<String, Double> correctForBidAskSpread(Map<String, Double> uncorrected) {
        Map<String, Double> corrected = new HashMap<>();

        for(Map.Entry<String, Double> entry : uncorrected.entrySet()) {
            double bidAskSpreadPercentage = bidAskSpreadMap.get(entry.getKey());
            double correctedProfit = entry.getValue() - bidAskSpreadPercentage;
            corrected.put(entry.getKey(), correctedProfit);
        }

        corrected = sortByValueHighToLow(corrected);

        return corrected;
    }

    private Map<String, Double> correctProfitsForTradingCosts(Map<String, Double> uncorrected) {
        Map<String, Double> corrected = new HashMap<>();

        for(Map.Entry<String, Double> entry : uncorrected.entrySet()) {
            corrected.put(entry.getKey(), (entry.getValue() - 0.001));
        }

        corrected = sortByValueHighToLow(corrected);

        return corrected;
    }

    public Map<String, Double> getBidAskSpreadMap() {
//        List<String> allTickers = new Momentum().getAllBusdTradingPairs();
//        HistoricalPrices historicalPrices = new HistoricalPrices();
//        int counter = 0;
//
//        for(String ticker : allTickers) {
//            bidAskSpreadMap.put(ticker, historicalPrices.getBidAskSpreadPercentage(ticker));
//            System.out.println("b/a " + counter++);
//        }
//
//        bidAskSpreadMap = sortByValueLowToHigh(bidAskSpreadMap);
//
//        for(Map.Entry<String, Double> entry : bidAskSpreadMap.entrySet()) {
//            double spread = entry.getValue() - 1;
//            DecimalFormat formatter = new DecimalFormat("0.0000000000");
//            String spreadFormatted = formatter.format(spread);
//            System.out.println(entry.getKey() + "   " + spreadFormatted);
//        }
//

        Map<String, Double> bidAskSpreadMap = new HashMap<>();

        bidAskSpreadMap.put("ETHBUSD", 0.0000023277);
        bidAskSpreadMap.put("COMPBUSD", 0.0000118497);
        bidAskSpreadMap.put("AUDBUSD", 0.0000128805);
        bidAskSpreadMap.put("BNBBUSD", 0.0000149788);
        //bidAskSpreadMap.put("BNBUSDT", 0.0000149999);
        bidAskSpreadMap.put("BTCBUSD", 0.0000627187);
        bidAskSpreadMap.put("GBPBUSD", 0.0000709069);
        bidAskSpreadMap.put("ENJBUSD", 0.0000822774);
        bidAskSpreadMap.put("EURBUSD", 0.0000826037);
        bidAskSpreadMap.put("TUSDBUSD", 0.0001000000);
        bidAskSpreadMap.put("USDCBUSD", 0.0001000100);
        bidAskSpreadMap.put("BUSDDAI", 0.0001001201);
        //bidAskSpreadMap.put("BUSDUSDT", 0.0001001502);
        bidAskSpreadMap.put("ADABUSD", 0.0001136945);
        bidAskSpreadMap.put("XVGBUSD", 0.0001609010);
        bidAskSpreadMap.put("CAKEBUSD", 0.0001967010);
        bidAskSpreadMap.put("BUSDBIDR", 0.0002042901);
        bidAskSpreadMap.put("XRPBUSD", 0.0002737101);
        bidAskSpreadMap.put("DOGEBUSD", 0.0002823378);
        //bidAskSpreadMap.put("SHIBUSDT", 0.0003539823);
        bidAskSpreadMap.put("SUSHIBUSD", 0.0004025533);
        bidAskSpreadMap.put("TLMBUSD", 0.0004069176);
        bidAskSpreadMap.put("PAXBUSD", 0.0006000000);
        bidAskSpreadMap.put("ETCBUSD", 0.0006143936);
        bidAskSpreadMap.put("1INCHBUSD", 0.0006263463);
        bidAskSpreadMap.put("LUNABUSD", 0.0006313494);
        bidAskSpreadMap.put("SHIBBUSD", 0.0007064641);
        bidAskSpreadMap.put("FILBUSD", 0.0007135212);
        bidAskSpreadMap.put("HBARBUSD", 0.0007220217);
        bidAskSpreadMap.put("FXSBUSD", 0.0007287302);
        bidAskSpreadMap.put("DOTBUSD", 0.0007291929);
        //bidAskSpreadMap.put("BNBUSDC", 0.0007341264);
        bidAskSpreadMap.put("BUSDBRL", 0.0007584376);
        bidAskSpreadMap.put("REEFBUSD", 0.0008163265);
        bidAskSpreadMap.put("LTCBUSD", 0.0008250605);
        //bidAskSpreadMap.put("CKBUSDT", 0.0009172809);
        bidAskSpreadMap.put("BTTBUSD", 0.0009494563);
        bidAskSpreadMap.put("HEGICBUSD", 0.0009633911);
        bidAskSpreadMap.put("IQBUSD", 0.0009643667);
        bidAskSpreadMap.put("BUSDRUB", 0.0009792217);
        bidAskSpreadMap.put("BCHBUSD", 0.0009803595);
        bidAskSpreadMap.put("VETBUSD", 0.0010176884);
        //bidAskSpreadMap.put("DGBUSDT", 0.0010198135);
        bidAskSpreadMap.put("ICPBUSD", 0.0010269350);
        bidAskSpreadMap.put("NEOBUSD", 0.0010292617);
        bidAskSpreadMap.put("ALGOBUSD", 0.0010527055);
        bidAskSpreadMap.put("EGLDBUSD", 0.0011157601);
        bidAskSpreadMap.put("LINKBUSD", 0.0011410315);
        bidAskSpreadMap.put("SOLBUSD", 0.0011580528);
        bidAskSpreadMap.put("EOSBUSD", 0.0011613598);
        bidAskSpreadMap.put("DASHBUSD", 0.0011860708);
        //bidAskSpreadMap.put("BUSDTRY", 0.0011947431);
        bidAskSpreadMap.put("SNXBUSD", 0.0012083984);
        bidAskSpreadMap.put("ATOMBUSD", 0.0012146056);
        bidAskSpreadMap.put("TRXBUSD", 0.0012147195);
        bidAskSpreadMap.put("DEXEBUSD", 0.0012262089);
        bidAskSpreadMap.put("NANOBUSD", 0.0012262485);
        bidAskSpreadMap.put("BUSDVAI", 0.0012371863);
        bidAskSpreadMap.put("SXPBUSD", 0.0012448571);
        bidAskSpreadMap.put("BUSDBVND", 0.0012451749);
        bidAskSpreadMap.put("XLMBUSD", 0.0012828362);
        bidAskSpreadMap.put("FRONTBUSD", 0.0012835266);
        bidAskSpreadMap.put("QTUMBUSD", 0.0013031438);
        bidAskSpreadMap.put("MIRBUSD", 0.0013367495);
        bidAskSpreadMap.put("BURGERBUSD", 0.0013745704);
        bidAskSpreadMap.put("IOTABUSD", 0.0013975904);
        bidAskSpreadMap.put("TVKBUSD", 0.0014018692);
        bidAskSpreadMap.put("AXSBUSD", 0.0014027485);
        bidAskSpreadMap.put("YFIIBUSD", 0.0014537644);
        bidAskSpreadMap.put("BZRXBUSD", 0.0014577259);
        bidAskSpreadMap.put("MANABUSD", 0.0015010722);
        bidAskSpreadMap.put("SWRVBUSD", 0.0015120968);
        bidAskSpreadMap.put("KP3RBUSD", 0.0015242243);
        bidAskSpreadMap.put("AAVEBUSD", 0.0015258260);
        bidAskSpreadMap.put("XMRBUSD", 0.0015276702);
        //bidAskSpreadMap.put("TRBUSDT", 0.0015337300);
        bidAskSpreadMap.put("CHZBUSD", 0.0015401540);
        bidAskSpreadMap.put("HOTBUSD", 0.0015649452);
        bidAskSpreadMap.put("MATICBUSD", 0.0016244913);
        bidAskSpreadMap.put("VIDTBUSD", 0.0016270986);
        bidAskSpreadMap.put("ZECBUSD", 0.0016334807);
        bidAskSpreadMap.put("UNIBUSD", 0.0016518005);
        bidAskSpreadMap.put("ONTBUSD", 0.0016718317);
        bidAskSpreadMap.put("CRVBUSD", 0.0017533606);
        bidAskSpreadMap.put("TKOBUSD", 0.0018266404);
        bidAskSpreadMap.put("DODOBUSD", 0.0018287108);
        bidAskSpreadMap.put("BATBUSD", 0.0018418920);
        bidAskSpreadMap.put("WAVESBUSD", 0.0018526316);
        bidAskSpreadMap.put("RSRBUSD", 0.0018691589);
        bidAskSpreadMap.put("PERPBUSD", 0.0018768769);
        bidAskSpreadMap.put("YFIBUSD", 0.0018828755);
        bidAskSpreadMap.put("TOMOBUSD", 0.0019130365);
        bidAskSpreadMap.put("SRMBUSD", 0.0019410964);
        bidAskSpreadMap.put("BELBUSD", 0.0019629397);
        bidAskSpreadMap.put("ZRXBUSD", 0.0019927444);
        bidAskSpreadMap.put("BARBUSD", 0.0020257032);
        bidAskSpreadMap.put("ONEBUSD", 0.0020328509);
        bidAskSpreadMap.put("MKRBUSD", 0.0020414690);
        bidAskSpreadMap.put("XVSBUSD", 0.0022354054);
        bidAskSpreadMap.put("ALPHABUSD", 0.0022405449);
        bidAskSpreadMap.put("OCEANBUSD", 0.0022986904);
        bidAskSpreadMap.put("LITBUSD", 0.0023076724);
        bidAskSpreadMap.put("AUDIOBUSD", 0.0023440490);
        bidAskSpreadMap.put("AVAXBUSD", 0.0023959867);
        bidAskSpreadMap.put("SANDBUSD", 0.0024266620);
        bidAskSpreadMap.put("BALBUSD", 0.0024460940);
        bidAskSpreadMap.put("GRTBUSD", 0.0024910047);
        bidAskSpreadMap.put("XTZBUSD", 0.0025253618);
        bidAskSpreadMap.put("ZILBUSD", 0.0025692530);
        bidAskSpreadMap.put("NMRBUSD", 0.0025925347);
        bidAskSpreadMap.put("BANDBUSD", 0.0026161633);
        bidAskSpreadMap.put("NEARBUSD", 0.0026953209);
        bidAskSpreadMap.put("OMGBUSD", 0.0027280254);
        bidAskSpreadMap.put("BCHABUSD", 0.0027392618);
        bidAskSpreadMap.put("LRCBUSD", 0.0027719456);
        bidAskSpreadMap.put("ICXBUSD", 0.0027801983);
        bidAskSpreadMap.put("RVNBUSD", 0.0027979143);
        bidAskSpreadMap.put("UNFIBUSD", 0.0028178086);
        bidAskSpreadMap.put("TRBBUSD", 0.0028394195);
        bidAskSpreadMap.put("IDEXBUSD", 0.0028655710);
        bidAskSpreadMap.put("EPSBUSD", 0.0029732408);
        bidAskSpreadMap.put("BAKEBUSD", 0.0030388617);
        bidAskSpreadMap.put("CTKBUSD", 0.0030638877);
        bidAskSpreadMap.put("ANTBUSD", 0.0030860670);
        bidAskSpreadMap.put("PROMBUSD", 0.0030939796);
        bidAskSpreadMap.put("CREAMBUSD", 0.0031275598);
        bidAskSpreadMap.put("KSMBUSD", 0.0031797150);
        bidAskSpreadMap.put("CTSIBUSD", 0.0032275465);
        bidAskSpreadMap.put("FIOBUSD", 0.0032499188);
        bidAskSpreadMap.put("HARDBUSD", 0.0032668134);
        bidAskSpreadMap.put("LINABUSD", 0.0033112583);
        bidAskSpreadMap.put("ROSEBUSD", 0.0033256414);
        bidAskSpreadMap.put("STRAXBUSD", 0.0033489966);
        bidAskSpreadMap.put("ALICEBUSD", 0.0034660661);
        bidAskSpreadMap.put("JSTBUSD", 0.0035399622);
        bidAskSpreadMap.put("DGBBUSD", 0.0035641548);
        bidAskSpreadMap.put("AVABUSD", 0.0037678975);
        bidAskSpreadMap.put("CVPBUSD", 0.0038250801);
        bidAskSpreadMap.put("SFPBUSD", 0.0039255469);
        bidAskSpreadMap.put("KNCBUSD", 0.0039829303);
        bidAskSpreadMap.put("IOSTBUSD", 0.0041508129);
        bidAskSpreadMap.put("FISBUSD", 0.0042718447);
        bidAskSpreadMap.put("BNTBUSD", 0.0043194436);
        bidAskSpreadMap.put("SYSBUSD", 0.0043800392);
        bidAskSpreadMap.put("UFTBUSD", 0.0044575628);
        bidAskSpreadMap.put("DIABUSD", 0.0045945325);
        bidAskSpreadMap.put("CFXBUSD", 0.0046082949);
        bidAskSpreadMap.put("AERGOBUSD", 0.0047885925);
        bidAskSpreadMap.put("RUNEBUSD", 0.0048485480);
        bidAskSpreadMap.put("FORBUSD", 0.0049024948);
        bidAskSpreadMap.put("WRXBUSD", 0.0049057248);
        bidAskSpreadMap.put("GHSTBUSD", 0.0049359018);
        bidAskSpreadMap.put("PONDBUSD", 0.0050125313);
        bidAskSpreadMap.put("DEGOBUSD", 0.0050636796);
        bidAskSpreadMap.put("TWTBUSD", 0.0050960408);
        bidAskSpreadMap.put("DFBUSD", 0.0051063830);
        bidAskSpreadMap.put("SLPBUSD", 0.0051759834);
        bidAskSpreadMap.put("OMBUSD", 0.0051763183);
        bidAskSpreadMap.put("SKLBUSD", 0.0053054101);
        bidAskSpreadMap.put("TRUBUSD", 0.0056934639);
        bidAskSpreadMap.put("BTCSTBUSD", 0.0058695362);
        bidAskSpreadMap.put("FLMBUSD", 0.0059432319);
        bidAskSpreadMap.put("DATABUSD", 0.0059959606);
        bidAskSpreadMap.put("PHABUSD", 0.0060369318);
        bidAskSpreadMap.put("CKBBUSD", 0.0062146338);
        bidAskSpreadMap.put("INJBUSD", 0.0062694154);
        bidAskSpreadMap.put("AUTOBUSD", 0.0062714067);
        bidAskSpreadMap.put("FORTHBUSD", 0.0063738945);
        bidAskSpreadMap.put("JUVBUSD", 0.0065355248);
        bidAskSpreadMap.put("AUCTIONBUSD", 0.0065706343);
        bidAskSpreadMap.put("BADGERBUSD", 0.0068392077);
        bidAskSpreadMap.put("RAMPBUSD", 0.0072793449);
        bidAskSpreadMap.put("DNTBUSD", 0.0073134079);
        bidAskSpreadMap.put("COVERBUSD", 0.0073913121);
        bidAskSpreadMap.put("BTGBUSD", 0.0081586721);
        bidAskSpreadMap.put("BIFIBUSD", 0.0084205067);
        bidAskSpreadMap.put("ACMBUSD", 0.0091023227);
        bidAskSpreadMap.put("PSGBUSD", 0.0091968548);
        bidAskSpreadMap.put("WINGBUSD", 0.0097334100);
        bidAskSpreadMap.put("SUPERBUSD", 0.0097485890);
        bidAskSpreadMap.put("BUSDZAR", 99.0000000000);

        return bidAskSpreadMap;
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

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValueLowToHigh(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>( map.entrySet() );
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue() ).compareTo( o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

}
