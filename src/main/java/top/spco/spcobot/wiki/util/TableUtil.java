package top.spco.spcobot.wiki.util;

import top.spco.spcobot.wiki.util.minecraft.MinecraftAssets;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;

public class TableUtil {
    public static String minecraftFileTree(File base) {
        File[] files = base.listFiles();
        assert files != null;
        StringBuilder tables = new StringBuilder();
        for (File file : files) {
            tables.append("== ").append(file.getName()).append(" ==").append("\n");
            tables.append(directoryTreeTable(file)).append("\n");
        }
        return tables.toString();
    }

    public static String structureCompositionBlocksTable(File structureOrDirectory, MinecraftAssets assets) throws IOException {
        Set<File> structures = new HashSet<>();
        FileUtil.getAllFile(structures, structureOrDirectory);
        ArrayList<String> blockTypes = new ArrayList<>();
        for (File file : structures) {
            var map = NBTUtil.getStructureBlocks(file);
            blockTypes.addAll(map.keySet());
        }
        blockTypes.remove("minecraft:air");
        StringBuilder sb = new StringBuilder("""
                {| class="wikitable"
                ! 方块
                |-
                | <div style="-moz-column-count: 3; -webkit-column-count: 3; column-count: 3;">""");
        sb.append("\n");
        Collections.sort(blockTypes);
        LinkedHashSet<String> sortedLinkedHashSet = new LinkedHashSet<>(blockTypes);
        sortedLinkedHashSet.remove("minecraft:air");
        sortedLinkedHashSet.remove("minecraft:structure_block");
        sortedLinkedHashSet.remove("minecraft:structure_void");
        for (String blockType : sortedLinkedHashSet) {
            sb.append("* ").append("{{BlockLink|").append(assets.getBlockEnglishName(blockType)).append("}}");
            sb.append("\n");
        }
        sb.append("</div>\n" +
                "|}");
        return sb.toString();
    }

    public static List<String> structureIds(File structureOrDirectory, File root) {
        List<String> ids = new ArrayList<>();
        Set<File> temp = new HashSet<>();
        FileUtil.getAllFile(temp, structureOrDirectory);
        List<File> structures = new ArrayList<>(temp);
        Collections.sort(structures);
        for (File file : structures) {
            String relativePathString = root.toPath().relativize(file.toPath()).normalize().toString().replace("\\", "/").replace(".nbt", "");
            ids.add(relativePathString);
        }
        return ids;
    }

    public static String structureTable(File structureOrDirectory, File root, Function<String, String> description, Function<String, String> image, MinecraftAssets assets) throws IOException {
        Set<File> temp = new HashSet<>();
        FileUtil.getAllFile(temp, structureOrDirectory);
        List<File> structures = new ArrayList<>(temp);
        Collections.sort(structures);
        StringBuilder sb = new StringBuilder("""
                {| class="wikitable collapsible"
                ! scope="col" style="width: 200px" | 结构名称
                ! scope="col" style="width: 200px" | 描述
                ! scope="col" style="width: 200px" | 包含
                ! scope="col" style="width: 200px" | 图片""");
        sb.append("\n");
        for (File structure : structures) {
            String relativePathString = root.toPath().relativize(structure.toPath()).normalize().toString().replace("\\", "/").replace(".nbt", "");
            sb.append("|-").append("\n").append("| <code>").append(relativePathString).append("</code>").append("\n");
            String desc = description.apply(relativePathString);
            sb.append("| ").append(desc).append("\n|\n");
            var tempMap = NBTUtil.getStructureBlocks(structure);
            var map = NBTUtil.sortByValueDescending(tempMap);
            Iterator<Map.Entry<String, Integer>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Integer> entry = iterator.next();
                if (entry.getKey().equals("minecraft:air") || entry.getKey().equals("minecraft:structure_void")) {
                    continue;
                }
                if (!Objects.equals(entry.getKey(), "minecraft:jigsaw")) {
                    sb.append(entry.getValue()).append(" {{BlockLink|").append(assets.getBlockEnglishName(entry.getKey()));
                    sb.append("}}");
                    if (iterator.hasNext()) {
                        sb.append("<br>\n");
                    }
                }
            }
            sb.append("\n| ").append(image.apply(relativePathString)).append("\n");
        }
        sb.append("|}");
        return sb.toString();
    }

    public static String userEditsCountTable(LinkedHashMap<String, Integer> userNameEditCountMap) {
        StringBuilder sb = new StringBuilder("{| class=\"wikitable sortable collapsible\"\n" +
                "|+ 截至 ").append(LocalDateTime.now(ZoneId.of("Asia/Shanghai"))).append("""
                 (UTC+8)
                |-
                ! 排名 !! 用户名 !! 编辑总数""");
        int rank = 0;
        for (Map.Entry<String, Integer> entry : userNameEditCountMap.entrySet()) {
            sb.append("""
                    
                    |-
                    |\s""").append(++rank).append(" || ").append("[[User:").append(entry.getKey()).append("|").append(entry.getKey()).append("]] || ");
            sb.append(entry.getValue());
        }
        sb.append("\n|}");
        return sb.toString();
    }

    public static String patrollersPatrolCountTable(LinkedHashMap<String, Map.Entry<Integer, Integer>> userNamePatrolCountMap) {
        StringBuilder sb = new StringBuilder("{| class=\"wikitable sortable collapsible\"\n" +
                "|+ 截至 ").append(LocalDateTime.now(ZoneId.of("Asia/Shanghai"))).append("""
                 (UTC+8)
                |-
                ! 排名 !! 用户名 !! 巡查总数 !! 近30天巡查次数""");
        int rank = 0;
        for (Map.Entry<String, Map.Entry<Integer, Integer>> entry : userNamePatrolCountMap.entrySet()) {
            sb.append("""
                    
                    |-
                    |\s""").append(++rank).append(" || ").append("[[User:").append(entry.getKey()).append("|").append(entry.getKey()).append("]] || ");
            sb.append(entry.getValue().getKey()).append(" || ");
            sb.append(entry.getValue().getValue());
        }
        sb.append("\n|}");
        return sb.toString();
    }

    public static String directoryTreeTable(File dir) {
        StringBuilder sb = new StringBuilder("{| class=\"collapsible collapsed collapse-button-none\" data-description=\"结构目录\"\n");
        sb.append("! 结构目录\n");
        sb.append("|-\n");
        sb.append("|<div class=\"file-hierarchy treeview\">\n");

        printDirectoryTree(sb, dir, 1);

        sb.append("\n").append("</div>\n").append("|}");
        return sb.toString();
    }

    private static void printDirectoryTree(StringBuilder sb, File dir, int index) {
        printIndexSymbol(sb, index).append(dir.getName()).append("\n");
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            assert files != null;
            Arrays.sort(files, (f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));
            for (File file : files) {
                printDirectoryTree(sb, file, index + 1);
            }
        }
    }

    private static StringBuilder printIndexSymbol(StringBuilder sb, int index) {
        if (index >= 1) {
            sb.append("*".repeat(index)).append(" ");
        }
        return sb;
    }
}
