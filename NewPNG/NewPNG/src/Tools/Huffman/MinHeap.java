/**
 * NewPNG
 * Submitted by:
 * Tom Basha 	ID# 311425714
 * Roei Lahav	ID# 315808469
 */

package Tools.Huffman;

public class MinHeap {
    Node[] heap;
    static int size, usedSpace;

    public MinHeap(int size) {
        heap = new Node[size];
        MinHeap.size = size;
        usedSpace = 0;
    }

    public void heapifyRealValues(Node[] possibleValues, int totalBytes) {
        int index = 0;
        for (int i = 0; i < size; i++) {
            while (possibleValues[index] == null) {
                index++;
            }
            possibleValues[index].updateFreq(totalBytes);
            insert(possibleValues[index]);
            index++;
        }
    }

    public void insert(Node n) {
        heap[usedSpace] = n;
        int curr = usedSpace;
        usedSpace++;
        while (heap[curr].getFrequency() < heap[curr / 2].getFrequency()) {
            Node tmp = heap[curr];
            heap[curr] = heap[curr / 2];
            heap[curr / 2] = tmp;
            curr = curr / 2;
        }
    }

    public Node extractMin() {
        Node n = heap[0];
        heap[0] = heap[usedSpace - 1];
        heap[usedSpace - 1] = null;
        usedSpace--;
        heapify(0);
        return n;
    }

    private void heapify(int i) {
        if (i > size)
            return;
        int rootLeft = 1, rootRight = 2, minChild;
        int leftChild = i * 2, rightChild = i * 2 + 1;
        if (i == 0) {
            leftChild = rootLeft;
            rightChild = rootRight;
        }
        if (rightChild < size && heap[rightChild] != null) {
            if (heap[leftChild].getFrequency() < heap[rightChild].getFrequency())
                minChild = leftChild;
            else
                minChild = rightChild;
        } else if (leftChild < size && heap[leftChild] != null)
            minChild = leftChild;
        else
            return;
        if (heap[minChild] != null && heap[minChild].getFrequency() < heap[i].getFrequency()) {
            Node tmp = heap[i];
            heap[i] = heap[minChild];
            heap[minChild] = tmp;
            heapify(minChild);
        }
    }

    public Node getRoot() {
        return heap[0];
    }
}

