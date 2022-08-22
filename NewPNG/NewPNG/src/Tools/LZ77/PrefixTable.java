/**
 * NewPNG
 * Submitted by:
 * Tom Basha 	ID# 311425714
 * Roei Lahav	ID# 315808469
 */

package Tools.LZ77;

public class PrefixTable {
    private final PrefixNode[] prefixes;
    private final int SIZE = 26;

    // constructor - creates new prefix array.
    public PrefixTable() {
        prefixes = new PrefixNode[SIZE];
    }

    // input: new prefix node
    // inserts the new prefix note to destined location.
    public void insert(PrefixNode pre) {
        int index;
        char[] split = pre.getPrefix().toCharArray();
        index = findIndex(split);           // calculates destined index

        if (prefixes[index] != null)        // index already taken
            pre.setNext(prefixes[index]);   // sets old prefix as new prefix next
        prefixes[index] = pre;              // insert new prefix
    }

    // input: string to look for, buffer start index.
    // output: prefix node.
    // looking for matching prefix.
    public PrefixNode search(String lookFor, int bufferStart) {
        char[] split = lookFor.toCharArray();

        int index = findIndex(split);
        PrefixNode curr = prefixes[index];

        return searchNext(lookFor, curr, bufferStart);
    }

    // input: string to look for, first prefix in destined index, buffer start index.
    // output: prefix node.
    // looking for matching prefix. if not found, returns null.
    public PrefixNode searchNext(String lookFor, PrefixNode curr, int bufferStart) {
        while (curr != null && curr.getStart() >= bufferStart) {
            if (curr.getPrefix().equals(lookFor))           // compare prefixes
                return curr;
            if (curr.getNext() != null && curr.getNext().getStart() < bufferStart)
                curr.setNext(null);                         // set next prefix as null if current next is ouf of buffer bounds
            curr = curr.getNext();                          // get next prefix
        }
        return null;
    }

    // input: prefix in char array.
    // output: destined prefix index.
    // calculates prefix destined index.
    private int findIndex(char[] split) {
        return (split[0] + (split[1] * 2) + (split[2] * 3)) % SIZE;     // gives weight to placement for better spread
    }
}
