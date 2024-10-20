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


import top.spco.spcobot.wiki.core.Wiki;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

/**
 * 机器人的一个任务单元。
 *
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
public abstract class Task<T extends Task<?>> {
    protected final BiConsumer<T, String> task;
    private final AtomicInteger counter = new AtomicInteger(0);
    private Wiki wiki;

    public Task(BiConsumer<T, String> task) {
        this.task = task;
    }

    @SuppressWarnings("unchecked")
    public void run() {
        targets().forEach(s -> task.accept((T) this, s));
    }

    public final int count() {
        return counter.incrementAndGet();
    }

    public final int getCount() {
        return counter.get();
    }

    public Wiki getWiki() {
        return wiki;
    }

    public void setWiki(Wiki wiki) {
        this.wiki = wiki;
    }

    public abstract List<String> targets();
}