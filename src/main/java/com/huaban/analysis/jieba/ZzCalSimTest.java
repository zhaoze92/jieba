package com.huaban.analysis.jieba;
import com.huaban.analysis.jieba.JiebaSegmenter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZzCalSimTest {
    private static JiebaSegmenter segmenter = new JiebaSegmenter();
    private static volatile HashMap<String, Double> wordWeightMap = new HashMap<>();

    ZzCalSimTest(){
        // 加载词典
        String strPath = "/home/zhaoze/000/model.word.weight.dict";
        try {
            // 读取
            Scanner in=new Scanner(new File(strPath));
            while(in.hasNext()){
                // 读取一行
                String line = in.nextLine();
                String[] part = line.split("\t");
                if (part.length != 2) {
                    continue;
                }
                wordWeightMap.put(part[0], Double.valueOf(part[1]));
            }

            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public double getWordWeight(String word) {
        if (wordWeightMap.containsKey(word)) {
            return wordWeightMap.get(word);
        } else if (isChinese(word)) {
            if (word.length() == 1) {
                return 5.0;
            } else if (word.length() == 2) {
                return 8.0;
            } else {
                return 11.0;
            }
        } else {
            if (word.length() <= 3) {
                return 1.5 * word.length();
            } else {
                return 8.0;
            }
        }
    }

    public boolean isChinese(String word) {
        int count = 0;
        String regEx = "[\\u4e00-\\u9fa5]"; // 普通汉字
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(word);
        while (m.find()) {
            for (int i = 0; i <= m.groupCount(); i++) {
                count++;
            }
        }
        return count == word.length();
    }

    public double calSimByWord(String query, String title) {

        if (title == null) {
            return 0.0;
        }
        HashMap<String, Double> queryDict = new HashMap();
        HashMap<String, Double> titleDict = new HashMap();

        List<SegToken> piece = segmenter.process(query, JiebaSegmenter.SegMode.SEARCH);
        for (SegToken i : piece) {
            double weight = getWordWeight(i.word);
            if (queryDict.containsKey(i.word)) {
                queryDict.put(i.word, queryDict.get(i.word) + weight);
            } else {
                queryDict.put(i.word, weight);
            }
        }

        List<SegToken> piece1 = segmenter.process(title, JiebaSegmenter.SegMode.SEARCH);
        for (SegToken i : piece1) {
            double weight = getWordWeight(i.word);
            if (titleDict.containsKey(i.word)) {
                titleDict.put(i.word, titleDict.get(i.word) + weight);
            } else {
                titleDict.put(i.word, weight);
            }
        }
        return calSim(queryDict, titleDict);
    }

    public double calSim(HashMap<String, Double> queryDict, HashMap<String, Double> titleDict) {
        double dotProduct = 0.0;
        double normQuery = 0.0;
        double normTitle = 0.0;
        for (Map.Entry<String, Double> entry : queryDict.entrySet()) {
            String word = entry.getKey();
            double wordWeight = entry.getValue();
            normQuery += wordWeight * wordWeight;
            if (titleDict.containsKey(word)) {
                double wordWeight2 = titleDict.get(word);
                dotProduct += wordWeight * wordWeight2;
            }
            //LOGGER.debug("query seg, word {}, weight {}.", word, wordWeight);
        }
        for (Map.Entry<String, Double> entry : titleDict.entrySet()) {
            String word = entry.getKey();
            double wordWeight = entry.getValue();
            normTitle += wordWeight * wordWeight;
            //LOGGER.debug("title seg, word {}, weight {}.", word, wordWeight);
        }
        if (normQuery > 0.0 && normTitle > 0.0) {
            //LOGGER.debug("dotProduct {}, normQuery {}, normTitle {}.",
            //             dotProduct, normQuery, normTitle);
            return dotProduct / Math.sqrt(normQuery) / Math.sqrt(normTitle);
        } else {
            return 0.0;
        }
    }




}
