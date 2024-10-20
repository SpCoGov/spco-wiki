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

import top.spco.spcobot.wiki.core.NameSpace;
import top.spco.spcobot.wiki.core.Page;
import top.spco.spcobot.wiki.core.action.parameter.FilterDir;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class ForeachPageMultithreadedTask extends MultithreadedTask<ForeachPageMultithreadedTask> {
    private List<String> targets = null;

    public ForeachPageMultithreadedTask(BiConsumer<ForeachPageMultithreadedTask, String> task, int threadCount, int cooldown) {
        super(task, threadCount, cooldown);
    }

    public ForeachPageMultithreadedTask(BiConsumer<ForeachPageMultithreadedTask, String> task, int threadCount, int cooldown, List<String> targets) {
        super(task, threadCount, cooldown);
        this.targets = targets;
    }

    @Override
    public void run() {
        if (targets == null) {
            ArrayList<String> pages = new ArrayList<>();
            for (Page page : getWiki().allPages("", FilterDir.ALL, NameSpace.allNameSpaces())) {
                pages.add(page.title());
            }
            targets = pages;
        }
        super.run();
    }

    @Override
    public List<String> targets() {
        return targets;
    }
}