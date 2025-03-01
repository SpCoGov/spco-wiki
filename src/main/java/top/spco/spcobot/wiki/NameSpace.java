package top.spco.spcobot.wiki;

import top.spco.spcobot.wiki.action.parameter.Expiry;
import top.spco.spcobot.wiki.action.parameter.FilterRedirect;
import top.spco.spcobot.wiki.action.parameter.RevisionType;
import top.spco.spcobot.wiki.action.parameter.Timestamp;
import top.spco.spcobot.wiki.util.CollectionUtil;
import top.spco.spcobot.wiki.util.ParamUtil;

import java.util.HashMap;
import java.util.List;

/**
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
public final class NameSpace {
    private static final HashMap<Integer, NameSpace> mapping = new HashMap<>();
    /**
     * 主空间，是标准命名空间。
     *
     * @since 0.1.0
     */
    public static final NameSpace MAIN = new NameSpace(0);
    /**
     * 主空间附属的讨论空间，是标准命名空间。
     *
     * @since 0.1.0
     */
    public static final NameSpace TALK = new NameSpace(1);
    /**
     * 用户空间，是标准命名空间。每一位用户在“User”命名空间中都有一个对应的页面。只要用户身份被确认，编辑历史、监视列表及最近修改等，就都会链接至此。
     *
     * @since 0.1.0
     */
    public static final NameSpace USER = new NameSpace(2);
    /**
     * 用户空间附属的讨论空间，是标准命名空间。
     *
     * @since 0.1.0
     */
    public static final NameSpace USER_TALK = new NameSpace(3);
    /**
     * <a href="https://minecraft.wiki">Minecraft Wiki</a>的{@link #PROJECT 项目空间}，是标准命名空间。
     *
     * @see #PROJECT
     * @since 0.1.0
     */
    public static final NameSpace MINECRAFT_WIKI = new NameSpace(4);
    /**
     * <a href="https://minecraft.wiki">Minecraft Wiki</a>的{@link #PROJECT 项目空间}附属的讨论空间，是标准命名空间。
     *
     * @see #PROJECT
     * @since 0.1.0
     */
    public static final NameSpace MINECRAFT_WIKI_TALK = new NameSpace(5);
    /**
     * 项目空间，是标准命名空间。这个命名空间通常用来存放与 wiki 运营和开发相关的“元讨论”内容。
     *
     * @since 0.1.0
     */
    public static final NameSpace PROJECT = new NameSpace(4);
    /**
     * 项目空间附属的讨论空间，是标准命名空间。
     *
     * @since 0.1.0
     */
    public static final NameSpace PROJECT_TALK = new NameSpace(5);
    /**
     * 文件空间，是标准命名空间。
     *
     * @since 0.1.0
     */
    public static final NameSpace FILE = new NameSpace(6);
    /**
     * 空间附属的讨论空间，是标准命名空间。
     *
     * @since 0.1.0
     */
    public static final NameSpace FILE_TALK = new NameSpace(7);
    /**
     * MediaWiki空间，是标准命名空间。
     *
     * @since 0.1.0
     */
    public static final NameSpace MEDIAWIKI = new NameSpace(8);
    /**
     * 空间附属的讨论空间，是标准命名空间。
     *
     * @since 0.1.0
     */
    public static final NameSpace MEDIAWIKI_TALK = new NameSpace(9);
    /**
     * 模板空间，是标准命名空间。
     *
     * @since 0.1.0
     */
    public static final NameSpace TEMPLATE = new NameSpace(10);
    /**
     * 空间附属的讨论空间，是标准命名空间。
     *
     * @since 0.1.0
     */
    public static final NameSpace TEMPLATE_TALK = new NameSpace(11);
    /**
     * 帮助空间，是标准命名空间。
     *
     * @since 0.1.0
     */
    public static final NameSpace HELP = new NameSpace(12);
    /**
     * 空间附属的讨论空间，是标准命名空间。
     *
     * @since 0.1.0
     */
    public static final NameSpace HELP_TALK = new NameSpace(13);
    /**
     * 分类空间，是标准命名空间。
     *
     * @since 0.1.0
     */
    public static final NameSpace CATEGORY = new NameSpace(14);
    /**
     * 空间附属的讨论空间，是标准命名空间。
     *
     * @since 0.1.0
     */
    public static final NameSpace CATEGORY_TALK = new NameSpace(15);
    /**
     * @since 0.1.0
     */
    public static final NameSpace PORTAL = new NameSpace(100);
    /**
     * @since 0.1.0
     */
    public static final NameSpace PORTAL_TALK = new NameSpace(101);
    /**
     * @since 0.1.0
     */
    public static final NameSpace WIKI_PROJECT = new NameSpace(102);
    /**
     * @since 0.1.0
     */
    public static final NameSpace WIKI_PROJECT_TALK = new NameSpace(103);
    /**
     * @since 0.1.0
     */
    public static final NameSpace DRAFT = new NameSpace(118);
    /**
     * @since 0.1.0
     */
    public static final NameSpace DRAFT_TALK = new NameSpace(119);
    /**
     * @since 0.1.0
     */
    public static final NameSpace MOS = new NameSpace(126);
    /**
     * @since 0.1.0
     */
    public static final NameSpace MOS_TALK = new NameSpace(127);
    /**
     * @since 0.1.0
     */
    public static final NameSpace PROPERTY = new NameSpace(302);
    /**
     * @since 0.1.0
     */
    public static final NameSpace PROPERTY_TALK = new NameSpace(303);
    /**
     * @since 0.1.0
     */
    public static final NameSpace CONCEPT = new NameSpace(308);
    /**
     * @since 0.1.0
     */
    public static final NameSpace CONCEPT_TALK = new NameSpace(309);
    /**
     * @since 0.1.0
     */
    public static final NameSpace SMW_SCHEMA = new NameSpace(312);
    /**
     * @since 0.1.0
     */
    public static final NameSpace SMW_SCHEMA_TALK = new NameSpace(313);
    /**
     * @since 0.1.0
     */
    public static final NameSpace RULE = new NameSpace(314);
    /**
     * @since 0.1.0
     */
    public static final NameSpace RULE_TALK = new NameSpace(315);
    /**
     * @since 0.1.0
     */
    public static final NameSpace TIMED_TEXT = new NameSpace(710);
    /**
     * @since 0.1.0
     */
    public static final NameSpace TIMED_TEXT_TALK = new NameSpace(711);
    /**
     * @since 0.1.0
     */
    public static final NameSpace MODULE = new NameSpace(828);
    /**
     * @since 0.1.0
     */
    public static final NameSpace MODULE_TALK = new NameSpace(829);
    /**
     * @since 0.1.0
     */
    public static final NameSpace EVENT = new NameSpace(1728);
    /**
     * @since 0.1.0
     */
    public static final NameSpace EVENT_TALK = new NameSpace(1729);
    /**
     * @since 0.1.0
     */
    public static final NameSpace GADGET = new NameSpace(2300);
    /**
     * @since 0.1.0
     */
    public static final NameSpace GADGET_TALK = new NameSpace(2301);
    /**
     * @since 0.1.0
     */
    public static final NameSpace GADGET_DEFINITION = new NameSpace(2302);
    /**
     * @since 0.1.0
     */
    public static final NameSpace GADGET_DEFINITION_TALK = new NameSpace(2303);
    /**
     * @since 0.1.0
     */
    public static final NameSpace TOPIC = new NameSpace(2600);
    /**
     * @since 0.1.0
     */
    public static final NameSpace LEGENDS_TUTORIAL = new NameSpace(9994);
    /**
     * @since 0.1.0
     */
    public static final NameSpace LEGENDS_TUTORIAL_TALK = new NameSpace(9995);
    /**
     * @since 0.1.0
     */
    public static final NameSpace DUNGEONS_TUTORIAL = new NameSpace(9996);
    /**
     * @since 0.1.0
     */
    public static final NameSpace DUNGEONS_TUTORIAL_TALK = new NameSpace(9997);
    /**
     * @since 0.1.0
     */
    public static final NameSpace TUTORIAL = new NameSpace(9998);
    /**
     * @since 0.1.0
     */
    public static final NameSpace TUTORIAL_TALK = new NameSpace(9999);
    /**
     * @since 0.1.0
     */
    public static final NameSpace DUNGEONS = new NameSpace(10000);
    /**
     * @since 0.1.0
     */
    public static final NameSpace DUNGEONS_TALK = new NameSpace(10001);
    /**
     * @since 0.1.0
     */
    public static final NameSpace EARTH = new NameSpace(10002);
    /**
     * @since 0.1.0
     */
    public static final NameSpace EARTH_TALK = new NameSpace(10003);
    /**
     * @since 0.1.0
     */
    public static final NameSpace STORY_MODE = new NameSpace(10004);
    /**
     * @since 0.1.0
     */
    public static final NameSpace STORY_MODE_TALK = new NameSpace(10005);
    /**
     * @since 0.1.0
     */
    public static final NameSpace LEGENDS = new NameSpace(10006);
    /**
     * @since 0.1.0
     */
    public static final NameSpace LEGENDS_TALK = new NameSpace(10007);
    /**
     * 此对象仅在部分Api的参数重表示请求所有命名空间。
     *
     * @see Wiki#allRevisions(String, Timestamp, Timestamp, NameSpace...)
     * @see Wiki#linksHere(String, FilterRedirect, NameSpace...)
     * @see Wiki#recentChanges(Timestamp, Timestamp, RevisionType[], NameSpace...)
     * @see Wiki#block(String, Expiry, String, boolean, boolean, boolean, boolean, boolean, boolean, String[], NameSpace...)
     * @since 0.1.0
     */
    public static final NameSpace ALL = new NameSpace(-1);
    /**
     * 命名空间对应的数字ID。
     */
    public final int value;

    public NameSpace(int value) {
        this.value = value;
        if (value >= 0) {
            mapping.put(value, this);
        }
    }

    public static NameSpace from(int value) {
        if (mapping.containsKey(value)) {
            return mapping.get(value);
        }
        throw new IllegalArgumentException("Unknown namespace value: " + value);
    }

    @Override
    public String toString() {
        if (value == ALL.value) {
            return "*";
        }
        return value + "";
    }

    /**
     * TODO: Remove in 2.0
     *
     * @deprecated
     */
    @Deprecated
    public static NameSpace[] allNameSpaces() {
        return mapping.values().toArray(new NameSpace[0]);
    }

    public static String toApiParam(boolean supportAll, NameSpace... nameSpaces) {
        List<NameSpace> nsList = CollectionUtil.newArrayList(nameSpaces);
        if (supportAll && nsList.contains(ALL)) {
            return "*";
        }
        nsList.remove(ALL);
        return ParamUtil.toListParam(nsList);
    }


}
