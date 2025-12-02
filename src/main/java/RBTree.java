public class RBTree {

    // Konstanta Warna
    private static final boolean RED = true;
    private static final boolean BLACK = false;

    // Struktur Node (Disembunyikan di dalam Tree)
    private class Node {
        String key, value;
        String foreignKey, foreignValue;
        Node left, right, parent;
        boolean color;
        Gimmick gimmick;

        Node(String key, String value, String foreignKey, String foreignValue, Gimmick gimmick) {
            this.key = key;
            this.value = value;
            this.foreignKey = foreignKey;
            this.foreignValue = foreignValue;
            this.color = RED;
            this.gimmick = gimmick;
        }
    }

    // Variabel Root milik pohon ini sendiri
    private Node root;

    public RBTree() {
        this.root = null;
    }

    public SearchResult get(String key){
        Node node = search(key);
        return node != null ?  new SearchResult(node.key, node.value, node.foreignKey, node.foreignValue, node.gimmick) : null;
    }

    // --- PUBLIC METHODS (Yang dipanggil oleh HashMap) ---

    // 1. Mencari Data
    public Node search(String key) {
        Node current = root;
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp == 0) {
//                return new SearchResult(current.value, current.gimmick);
                return current;
            }
            else if (cmp < 0) current = current.left;
            else current = current.right;
        }
        return null;
    }

    // 2. Memasukkan Data
    public void insert(String key, String value, String foreignKey, String foreignValue, Gimmick gimmick) {
        // Jika pohon kosong
        if (root == null) {
            root = new Node(key, value, foreignKey, foreignValue,  gimmick);
            root.color = BLACK;
            return;
        }

        // Insert BST Standar
        Node current = root;
        Node parent = null;
        while (current != null) {
            parent = current;
            int cmp = key.compareTo(current.key);
            if (cmp == 0) {
                current.value = value; // Update jika ada
                return;
            } else if (cmp < 0) current = current.left;
            else current = current.right;
        }

        Node newNode = new Node(key, value, foreignKey, foreignValue, gimmick);
        newNode.parent = parent;
        if (key.compareTo(parent.key) < 0) parent.left = newNode;
        else parent.right = newNode;

        // Penyeimbangan (FixUp)
        insertFixUp(newNode);
    }

    // --- PRIVATE HELPER METHODS (Logika Internal RBT) ---

    private void insertFixUp(Node k) {
        Node u;
        while (k.parent != null && k.parent.color == RED) {
            if (k.parent == k.parent.parent.right) {
                u = k.parent.parent.left;
                if (u != null && u.color == RED) {
                    u.color = BLACK;
                    k.parent.color = BLACK;
                    k.parent.parent.color = RED;
                    k = k.parent.parent;
                } else {
                    if (k == k.parent.left) {
                        k = k.parent;
                        rightRotate(k);
                    }
                    k.parent.color = BLACK;
                    k.parent.parent.color = RED;
                    leftRotate(k.parent.parent);
                }
            } else {
                u = k.parent.parent.right;
                if (u != null && u.color == RED) {
                    u.color = BLACK;
                    k.parent.color = BLACK;
                    k.parent.parent.color = RED;
                    k = k.parent.parent;
                } else {
                    if (k == k.parent.right) {
                        k = k.parent;
                        leftRotate(k);
                    }
                    k.parent.color = BLACK;
                    k.parent.parent.color = RED;
                    rightRotate(k.parent.parent);
                }
            }
            if (k == root) break;
        }
        root.color = BLACK;
    }

    private void leftRotate(Node x) {
        Node y = x.right;
        x.right = y.left;
        if (y.left != null) y.left.parent = x;
        y.parent = x.parent;
        if (x.parent == null) this.root = y; // Update Root Pohon Ini
        else if (x == x.parent.left) x.parent.left = y;
        else x.parent.right = y;
        y.left = x;
        x.parent = y;
    }

    private void rightRotate(Node x) {
        Node y = x.left;
        x.left = y.right;
        if (y.right != null) y.right.parent = x;
        y.parent = x.parent;
        if (x.parent == null) this.root = y; // Update Root Pohon Ini
        else if (x == x.parent.right) x.parent.right = y;
        else x.parent.left = y;
        y.right = x;
        x.parent = y;
    }

    // Helper untuk print (bisa dipanggil HashMap)
    public void printStructure(String prefix) {
        printRecursive(root, prefix, true);
    }

    private void printRecursive(Node node, String indent, boolean last) {
        if (node != null) {
            System.out.print(indent);
            if (last) {
                System.out.print("R----");
                indent += "   ";
            } else {
                System.out.print("L----");
                indent += "|  ";
            }
            String color = node.color == RED ? "RED" : "BLACK";
            System.out.println(node.key + " (" + color + ")");
            printRecursive(node.left, indent, false);
            printRecursive(node.right, indent, true);
        }
    }
}