package top.spco.spcobot.wiki.util.nbt.tag.builtin;

import top.spco.spcobot.wiki.util.nbt.SNBTIO.StringifiedNBTReader;
import top.spco.spcobot.wiki.util.nbt.SNBTIO.StringifiedNBTWriter;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * A tag containing an integer.
 */
public class IntTag extends Tag {
    private int value;

    /**
     * Creates a tag with the specified name.
     *
     * @param name The name of the tag.
     */
    public IntTag(String name) {
        this(name, 0);
    }

    /**
     * Creates a tag with the specified name.
     *
     * @param name  The name of the tag.
     * @param value The value of the tag.
     */
    public IntTag(String name, int value) {
        super(name);
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    /**
     * Sets the value of this tag.
     *
     * @param value New value of this tag.
     */
    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public void read(DataInput in) throws IOException {
        this.value = in.readInt();
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(this.value);
    }

    @Override
    public void destringify(StringifiedNBTReader in) throws IOException {
        String s = in.readNextSingleValueString();
        value = Integer.parseInt(s);
    }

    @Override
    public void stringify(StringifiedNBTWriter out, boolean linebreak, int depth) throws IOException {
        out.append(String.valueOf(value));
    }

    @Override
    public IntTag clone() {
        return new IntTag(this.getName(), this.getValue());
    }
}
