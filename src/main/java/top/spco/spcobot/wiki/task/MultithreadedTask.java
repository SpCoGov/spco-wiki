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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;

/**
 * 机器人的一个多线程任务单元
 *
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
public abstract class MultithreadedTask<T extends MultithreadedTask<?>> extends Task<T> {
    private static final Logger LOGGER = LogUtil.getLogger();
    private final int threadCount;
    private final int cooldown;
    private final ExecutorService executor;

    public MultithreadedTask(BiConsumer<T, String> task, int threadCount, int cooldown) {
        super(task);
        this.threadCount = threadCount;
        this.cooldown = cooldown;
        this.executor = Executors.newFixedThreadPool(threadCount);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        List<Future<?>> futures = new ArrayList<>();
        for (var entry : assignTargets().entrySet()) {
            Future<?> future = executor.submit(() -> {
                entry.getValue().forEach((s -> {
                    try {
                        cooldown();
                        count();
                        task.accept((T) this, s);
                        cooldown();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        LOGGER.error("线程被中断", e);
                    }
                }));
            });
            futures.add(future);
        }

        // 关闭线程池，并等待所有任务完成
        executor.shutdown();
        try {
            for (Future<?> future : futures) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("任务执行异常", e);
        }
    }

    private void cooldown() throws InterruptedException {
        if (cooldown > 0) {
            Thread.sleep(cooldown);
        }
    }

    private Map<Integer, List<String>> assignTargets() {
        Map<Integer, List<String>> map = new HashMap<>();
        int size = targets().size();
        int sublistSize = (int) Math.ceil((double) size / threadCount);
        for (int i = 0; i < threadCount; i++) {
            int start = i * sublistSize;
            int end = Math.min(start + sublistSize, size);
            if (start < size) {
                map.put(i + 1, targets().subList(start, end));
            } else {
                map.put(i + 1, new ArrayList<>());
            }
        }
        return map;
    }
}