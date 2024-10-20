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
package top.spco.spcobot.wiki.util;

import top.spco.spcobot.wiki.util.nbt.NBTIO;
import top.spco.spcobot.wiki.util.minecraft.MinecraftAssets;
import top.spco.spcobot.wiki.util.minecraft.world.BlockPos;
import top.spco.spcobot.wiki.util.minecraft.world.BlockState;
import top.spco.spcobot.wiki.util.minecraft.world.Structure;
import top.spco.spcobot.wiki.util.nbt.tag.builtin.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

@SuppressWarnings("unchecked")
public class NBTUtil {
    public static Map<String, Integer> getStructureBlocks(File structureNbtFile) throws IOException {
        ArrayList<String> blockTypes = new ArrayList<>();
        Map<String, Integer> blockCount = new HashMap<>();
        CompoundTag nbtData = NBTIO.readFile(structureNbtFile);
        ArrayList<Tag> palette = (ArrayList<Tag>) nbtData.get("palette").getValue();
        palette.forEach(tag -> blockTypes.add((String) ((LinkedHashMap<String, Tag>) tag.getValue()).get("Name").getValue()));
        ArrayList<CompoundTag> blocks = (ArrayList<CompoundTag>) nbtData.get("blocks").getValue();
        for (CompoundTag block : blocks) {
            String blockType = blockTypes.get(((IntTag) block.get("state")).getValue());
            if (blockType.equals("minecraft:jigsaw")) {
                CompoundTag nbt = block.get("nbt");
                blockType = ((StringTag) nbt.get("final_state")).getValue();
            }
            blockCount.put(blockType, blockCount.getOrDefault(blockType, 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : blockCount.entrySet()) {
            int multiblock = MinecraftAssets.multiblockBlock(entry.getKey());
            if (multiblock > 1) {
                blockCount.put(entry.getKey(), blockCount.get(entry.getKey()) / multiblock);
            }
        }
        Map<String, Integer> cleanedBlockCount = new HashMap<>();
        blockCount.forEach((key, value) -> {
            if (key.contains("[")) {
                String newKey = key.replaceAll("\\[.*?]", "");
                if (newKey.endsWith("]")) {
                    newKey = newKey.replace("]", "");
                }
                cleanedBlockCount.merge(newKey, value, Integer::sum);
            } else {
                cleanedBlockCount.put(key, value);
            }
        });
        return cleanedBlockCount;
    }

    public static Structure getStructure(File structureNbtFile) throws IOException {
        Structure structure = new Structure();
        ArrayList<String> blockTypes = new ArrayList<>();
        CompoundTag nbtData = NBTIO.readFile(structureNbtFile);
        ArrayList<Tag> palette = (ArrayList<Tag>) nbtData.get("palette").getValue();
        palette.forEach(tag -> blockTypes.add((String) ((LinkedHashMap<String, Tag>) tag.getValue()).get("Name").getValue()));
        ArrayList<CompoundTag> blocks = (ArrayList<CompoundTag>) nbtData.get("blocks").getValue();
        for (CompoundTag block : blocks) {
            String blockType = blockTypes.get(((IntTag) block.get("state")).getValue());
            if (blockType.equals("minecraft:jigsaw")) {
                CompoundTag nbt = block.get("nbt");
                blockType = ((StringTag) nbt.get("final_state")).getValue();
            }
            List<Tag> pos = ((ListTag) block.get("pos")).getValue();
            structure.setBlock(new BlockPos(((IntTag) pos.get(0)).getValue(), ((IntTag) pos.get(1)).getValue(), ((IntTag) pos.get(2)).getValue()), new BlockState(blockType));
        }
        return structure;
    }

    public static Map<String, Range> integrate(List<Map<String, Integer>> listOfMaps) {
        // 存储结果
        Map<String, Range> resultMap = new LinkedHashMap<>();
        Set<String> blockTypes = new HashSet<>();

        for (Map<String, Integer> map : listOfMaps) {
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                blockTypes.add(entry.getKey());
                resultMap.computeIfAbsent(entry.getKey(), k -> new Range(entry.getValue(), entry.getValue()));
            }
        }

        for (Map<String, Integer> map : listOfMaps) {
            for (String blockType : blockTypes) {
                resultMap.get(blockType).include(map.getOrDefault(blockType, 0));
            }
        }
        return sortByValueDescending(resultMap);
    }

    public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortByValueDescending(Map<K, V> map) {
        List<Map.Entry<K, V>> entryList = new ArrayList<>(map.entrySet());

        // 根据 value 值进行排序，从大到小
        entryList.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        // 创建一个新的 LinkedHashMap，保持排序后的顺序
        LinkedHashMap<K, V> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : entryList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}