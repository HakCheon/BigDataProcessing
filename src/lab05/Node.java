package lab05;

class Node {
	private Node left;
	private Node right;
	private String best;
	
	public Node() {
		this.left = null;
		this.right = null;
		this.best = null;
	}

	public void setLeft(Node n) {
		this.left = n;
	}
	
	public void setRight(Node n) {
		this.right = n;
	}
	
	public void setBest(String s) {
		this.best = s;
	}
	
	public Node getLeft() {
		return this.left;
	}
	
	public Node getRight() {
		return this.right;
	}
	
	public String getBest() {
		return this.best;
	}
}
