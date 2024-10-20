/*
 * Copyright 2024 SpCo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.spco.spcobot.wiki.task;

import org.apache.logging.log4j.Logger;
import top.spco.spcobot.wiki.util.LogUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文本多线程替换任务。
 *
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
public class ReplaceMultithreadedTask extends MultithreadedTask<ReplaceMultithreadedTask> {
    private static final Logger LOGGER = LogUtil.getLogger();
    private final Map<String, String> replacements = new HashMap<>();
    private final AtomicInteger operateCounter = new AtomicInteger(0);
    private final AtomicInteger successCounter = new AtomicInteger(0);
    private final BiFunction<ReplaceMultithreadedTask, String, String> reason;
    private final List<String> targets;

    public void addReplacement(String target, String replacement, boolean isRegex) {
        if (isRegex) {
            replacements.put(target, replacement);
        } else {
            replacements.put(Pattern.quote(target), Matcher.quoteReplacement(replacement));
        }
    }

    public void addReplacement(String target, String replacement) {
        addReplacement(target, replacement, false);
    }

    public ReplaceMultithreadedTask(List<String> targetPages, int threadCount, int cooldown, BiFunction<ReplaceMultithreadedTask, String, String> reason) {
        super((task, page) -> {
            LOGGER.info("{}/{}", task.count(), task.targets().size());
            String pageText;
            pageText = task.getWiki().getPageText(page);
            boolean find = false;
            for (var replacement : task.replacements.entrySet()) {
                Pattern pattern = Pattern.compile(replacement.getKey());
                Matcher matcher = pattern.matcher(pageText);
                if (matcher.find()) {
                    find = true;
                    break;
                }
            }
            if (find) {
                String replaced = pageText;
                for (var replacement : task.replacements.entrySet()) {
                    replaced = replaced.replaceAll(replacement.getKey(), replacement.getValue());
                }
                task.operateCounter.incrementAndGet();
                boolean success = task.getWiki().edit(page, replaced, task.reason.apply(task, page), false, false);
                if (success) {
                    task.successCounter.incrementAndGet();
                } else {
                    LOGGER.warn("替换失败：{}", page);
                }
            }
        }, threadCount, cooldown);
        this.reason = reason;
        this.targets = targetPages;
    }

    public int getOperateCount() {
        return operateCounter.get();
    }

    public int getSuccessCount() {
        return successCounter.get();
    }

    @Override
    public List<String> targets() {
        return targets;
    }
}