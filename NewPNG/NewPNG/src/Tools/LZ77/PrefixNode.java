/**
 * NewPNG
 * Submitted by:
 * Tom Basha 	ID# 311425714
 * Roei Lahav	ID# 315808469
 */

package Tools.LZ77;

public class PrefixNode {
    private final int start;                    // start index
    private final String prefix;                // prefix
    private PrefixNode next = null;             // next prefix node

    public PrefixNode(int start, String prefix) {
        this.start = start;
        this.prefix = prefix;
    }

    public void setNext(PrefixNode next) {
        this.next = next;
    }

    public int getStart() {
        return start;
    }

    public String getPrefix() {
        return prefix;
    }

    public PrefixNode getNext() {
        return next;
    }
}