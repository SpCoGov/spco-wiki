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
package top.spco.spcobot.wiki.action.parameter;

/**
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
public enum Assert {
    /**
     * 验证用户未登录。
     */
    ANON("anon"),
    /**
     * 验证是否有机器人用户权限。
     */
    BOT("bot"),
    /**
     * 验证用户已登录。
     */
    USER("user");
    private final String value;

    Assert(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}