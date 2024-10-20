package top.spco.spcobot.wiki.util;

import top.spco.spcobot.wiki.core.Wiki;
import top.spco.spcobot.wiki.core.user.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class WikiUtil {
    public static LinkedHashMap<String, Integer> genGlobalEditCount(Wiki wiki, int maxGroupSize, int threadCount, int minimumEditCount) {
        List<User> users = new ArrayList<>(wiki.allUsers(true));
        println("成功", "成功获取" + users.size() + "条用户记录");
        AtomicInteger errorTimes = new AtomicInteger(0);
        AtomicInteger times = new AtomicInteger(0);
        Map<String, Integer> editsCountMap = new ConcurrentHashMap<>();
        Map<Integer, List<User>> tasks = dispatchUserForEachThread(users, threadCount);
        List<Thread> threads = new ArrayList<>();
        for (var entry : tasks.entrySet()) {
            Thread thread = new Thread(() -> {
                List<List<User>> split = splitList(entry.getValue(), maxGroupSize);
                split.forEach(list -> {
                    try {
                        Thread.sleep(1000);
                        // TODO: needs to use new api
                        var editsCount = new HashMap<String, Integer>();
                        Thread.sleep(1000);
                        editsCount.forEach((s, integer) -> {
                            if (integer >= minimumEditCount) {
                                editsCountMap.put(s, integer);
                            }
                            times.addAndGet(1);
                            System.out.print("\r[进度] " + times.get() + "/" + users.size());
                        });
                    } catch (Exception e) {
                        errorTimes.addAndGet(1);
                        e.printStackTrace();
                    }
                });
            });
            thread.setName("Task-" + entry.getKey());
            threads.add(thread);
        }

        for (var thread : threads) {
            thread.start();
        }

        try {
            for (var thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println();
        editsCountMap.forEach((s, integer) -> {
            if (s.toLowerCase().contains("fuck")) {
                editsCountMap.remove(s);
            }
        });
        var shortedMap = sortByValueDescending(editsCountMap);
        System.out.println("[成功] 共" + shortedMap.size() + "条记录");
        if (errorTimes.get() > 0) {
            println("告知", "操作过程中有" + errorTimes.get() + "次错误，操作失败");
        }
        return shortedMap;
    }

    public static void println(String s, String i) {
    }

    private static void genTestUserNames(List<String> userNames, int count) {
        HashSet<String> set = new HashSet<>();
        for (int i = 0; i < count; i++) {
            set.add(UUID.randomUUID().toString().substring(0, 10));
        }
        userNames.addAll(set);
    }

    public static <T> Map<Integer, List<T>> dispatchUserForEachThread(List<T> oList, final int threadCount) {
        Map<Integer, List<T>> map = new HashMap<>();
        int size = oList.size();
        int sublistSize = (int) Math.ceil((double) size / threadCount);
        for (int i = 0; i < threadCount; i++) {
            int start = i * sublistSize;
            int end = Math.min(start + sublistSize, size);
            if (start < size) {
                map.put(i + 1, oList.subList(start, end));
            } else {
                map.put(i + 1, new ArrayList<>());
            }
        }
        return map;
    }

    private static <T> List<List<T>> splitList(List<T> list, final int max) {
        // 主List，用于存放所有的子List
        List<List<T>> result = new ArrayList<>();
        int size = list.size();

        for (int i = 0; i < size; i += max) {
            // 计算子List的结束位置
            int end = Math.min(size, i + max);
            // 创建子List并添加到主List中
            result.add(new ArrayList<>(list.subList(i, end)));
        }

        return result;
    }

    public static void addDeleteTemplate(Wiki wiki, String page) {
        String pageText = wiki.getPageText(page);
        wiki.edit(page, "{{d}}\n" + pageText, "需要删除的页面");
    }

    public static Map<String, String> getPageInterwiki(Wiki wiki, String page) {
        String pageText = wiki.getPageText(page);
        Map<String, String> interwikis = new HashMap<>();
        if (page.isEmpty()) {
            return interwikis;
        }
        String[] langCode = new String[]{"cs", "de", "el", "en", "es", "fr", "hu", "it", "ja", "ko", "lzh", "nl", "pl", "pt", "ru", "th", "tr", "uk", "zh"};
        for (String code : langCode) {
            String interwiki = StringUtil.extractContent(pageText, "[[" + code + ":", "]]");
            if (!interwiki.isEmpty()) {
                interwikis.put(code, interwiki);
            }
        }
        return interwikis;
    }

    public static Map<String, String> getInterwiki(Wiki zhWiki, Wiki enWiki, String page, boolean isTemplate) {
        String pageText = zhWiki.getPageText(page);
        Map<String, String> interwikis = new HashMap<>();
        if (isTemplate) {
            pageText = StringUtil.extractContent(pageText, "<noinclude>", "</noinclude>");
        }
        if (pageText.isEmpty()) {
            return interwikis;
        }
        String enPage = StringUtil.extractContent(pageText, "[[en:", "]]");
        if (enPage.isEmpty()) {
            if (!enWiki.getPageText(page).isEmpty()) {
                enPage = page;
            } else {
                return interwikis;
            }
        }
        String[] langCode = new String[]{"cs", "de", "el", "en", "es", "fr", "hu", "it", "ja", "ko", "lzh", "nl", "pl", "pt", "ru", "th", "tr", "uk"};
        String enPageText = enWiki.getPageText(enPage);
        if (isTemplate) {
            enPageText = StringUtil.extractContent(enPageText, "<noinclude>", "</noinclude>");
        }
        if (enPageText.isEmpty()) {
            return interwikis;
        }
        for (String code : langCode) {
            String interwiki = StringUtil.extractContent(enPageText, "[[" + code + ":", "]]");
            if (!interwiki.isEmpty()) {
                interwikis.put(code, interwiki);
            }
        }
        for (Map.Entry<String, String> interwiki : interwikis.entrySet()) {
        }
        return interwikis;
    }

    private static LinkedHashMap<String, Integer> sortByValueDescending(Map<String, Integer> map) {
        // 创建一个List<Map.Entry>，将原始HashMap中的entry添加进去
        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());

        // 按值进行降序排序
        list.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        // 创建一个新的LinkedHashMap，按排序后的顺序插入条目
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}
