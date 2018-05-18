package com.zhaoze.java;

import java.util.Arrays;
import java.util.List;


import org.apache.commons.lang3.StringUtils;

public class BookUtils {

    private static final double BOOK_INTENTION_SCORE_THRESHOLD = 0.6;
    private static final String REGION_KEYWORD = "keyword";
    private static final String REGION_BAIDU_NOVEL = "baidunovel";
    private static final List<String> USELESS_ITEMS = Arrays.asList(
            "第一部", "第二部", "第三部", "第四部", "起点中文网", "笔趣阁", "潇湘书院",
            "免费读", "免费看", "免费", "下载", "txt", "TXT", "最新", "全部", "全集", "全文", "全本",
            "作品集", "章节列表", "章节", "系列", "读本", "书籍", "作品", "在线阅读", "阅读", "故事", "小说", "完结"
    );

    public static String removeUselessItems(String query) {
        for (String uselessItem : USELESS_ITEMS) {
            String noUselessQuery = query.replace(uselessItem, "");
            if (StringUtils.isNotBlank(noUselessQuery)) {
                query = noUselessQuery;
            }
        }
        return query;
    }


    public static boolean isQueryAllUselessItems(String query) {
        for (String uselessItem : USELESS_ITEMS) {
            query = query.replace(uselessItem, "");
            if (StringUtils.isBlank(query)) {
                return true;
            }
        }
        return false;
    }

}
