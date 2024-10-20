package top.spco.spcobot.wiki.util.nbt.tag.builtin;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import top.spco.spcobot.wiki.util.nbt.SNBTIO.*;

/**
 * A tag containing a string.
 */
public class StringTag extends Tag {
    private String value;

    /**
     * Creates a tag with the specified name.
     *
     * @param name The name of the tag.
     */
    public StringTag(String name) {
        this(name, "");
    }

    /**
     * Creates a tag with the specified name.
     *
     * @param name  The name of the tag.
     * @param value The value of the tag.
     */
    public StringTag(String name, String value) {
        super(name);
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    /**
     * Sets the value of this tag.
     *
     * @param value New value of this tag.
     */
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void read(DataInput in) throws IOException {
        this.value = in.readUTF();
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(this.value);
    }

    @Override
    public void destringify(StringifiedNBTReader in) throws IOException {
        String s = in.readNextSingleValueString();
        if(s.charAt(0) == '"') {
            value = s.substring(1, s.length() - 1).replaceAll("\\\\\"", "\"");
        } else if(s.charAt(0) == '\'') {
            value = s.substring(1, s.length() - 1).replaceAll("\\\\'", "'");
        } else {
            value = s;
        }
    }

    @Override
    public void stringify(StringifiedNBTWriter out, boolean linebreak, int depth) throws IOException {
        if(value.matches("(?!\\d+)[\\wd]*")) {
            out.append(value);
            return;
        }
        if(value.contains("\"")) {
            if(value.contains("'")) {
                String sb = "\"" + value.replaceAll("\"", "\\\\\"") + "\"";
                out.append(sb);
                return;
            }
            String sb = "'" + value + "'";
            out.append(sb);
            return;
        }
        String sb = "\"" + value + "\"";
        out.append(sb);
    }

    @Override
    public StringTag clone() {
        return new StringTag(this.getName(), this.getValue());
    }
}
