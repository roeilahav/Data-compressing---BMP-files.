/**
 * NewPNG
 * Submitted by:
 * Tom Basha 	ID# 311425714
 * Roei Lahav	ID# 315808469
 */

package Tools.Huffman;

public class Node {
    static int lengthSum = 0;
    static int longestHuffmanLength = 0;
    private int value, cnt, altCnt, padding = 0;
    private final String connectedValues;
    private String huffmanCode = "";
    private double frequency;
    private Node left = null, right = null;

    public Node(int value) {
        this.value = value;
        connectedValues = "" + value;
        this.cnt = 0;
    }

    public Node(Node n1, Node n2) {
        this.value = -1;
        this.right = n1;
        this.left = n2;
        frequency = n1.getFrequency() + n2.getFrequency();
        cnt = n1.getCnt() + n2.getCnt();
        connectedValues = "(" + n1.getConnectedValues() + " + " + n2.getConnectedValues() + ")";
    }

    public Node(int value, int cnt) {
        this.value = value;
        connectedValues = "" + value;
        this.cnt = cnt;
    }

    public int getValue() {
        return value;
    }

    public int getCnt() {
        return cnt;
    }

    public double getFrequency() {
        return frequency;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public void incCnt() {
        cnt++;
    }

    public String getConnectedValues() {
        return connectedValues;
    }

    public int getPadding() {
        return padding;
    }

    @Override
    public String toString() {
        return "Value: " + connectedValues +
                "\nCount: " + cnt +
                "\nFrequency: " + frequency +
                "\nHuffman Code: " + huffmanCode + "\n";
    }

    public void updateFreq(int totalBytes) {
        frequency = ((double) cnt) / totalBytes;
    }

    public void setHuffmanCode(String s) {
        this.huffmanCode = s;
        if (s.length() > longestHuffmanLength)
            longestHuffmanLength = s.length();
        if (left != null)
            left.setHuffmanCode(s + "0");
        if (right != null)
            right.setHuffmanCode(s + "1");
    }

    public String getCode() {
        return huffmanCode;
    }

    public int getLongestHoffmanLength() {
        return longestHuffmanLength;
    }

    public void setPadding() {
        lengthSum = 0;
        setPadding(this);
        padding = 8 - lengthSum % 8;
    }

    public void setPadding(Node n) {
        if (n == null)
            return;
        setPadding(n.left);
        if (n.getValue() != -1)
            lengthSum += n.huffmanCode.length() * n.getCnt();
        setPadding(n.right);
    }
}
