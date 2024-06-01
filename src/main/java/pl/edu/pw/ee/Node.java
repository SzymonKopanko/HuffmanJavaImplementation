package pl.edu.pw.ee;

public class Node implements Comparable{

    private String code;
    private char character;
    private final int frequency;
    private Node leftChild;
    private Node rightChild;
    private Node parent;
    private boolean isRoot = false;
    private boolean isLeftChild;

    public Node(char character, int frequency){
        this.frequency = frequency;
        this.character = character;
    }

    public Node(Node leftChild, Node rightChild){
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.frequency = leftChild.frequency + rightChild.frequency;
    }

    public Integer getFrequency(){
        return this.frequency;
    }

    public char getCharacter() {
        return this.character;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof Node)){
            throw new ClassCastException("Zła klasa!");
        }
        Node that = (Node) o;
        return this.getFrequency().compareTo(that.getFrequency());
    }

    @Override
    public String toString() {
        if(this.getLeftChild() == null){
            return "Node{" +
                    "frequency=" + frequency +
                    ", character=" + character +
                    ", code=" + code +
                    '}';
        }
        else {
            return "Node{" +
                    "frequency=" + frequency +
                    '}';
        }

    }

    public Node getLeftChild() {
        return leftChild;
    }

    public Node getRightChild() {
        return rightChild;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Node getParent() {
        return this.parent;
    }

    public boolean isLeftChild() {
        return isLeftChild;
    }

    public void setIsLeftChild(boolean leftChild) {
        isLeftChild = leftChild;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
