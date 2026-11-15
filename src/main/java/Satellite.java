import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class Satellite {
    public Tree treeFromTraversals(List<Character> preorderInput, List<Character> inorderInput) {
        if (preorderInput.size() != inorderInput.size()) {
            throw new IllegalArgumentException("traversals must have the same length");
        }

        Set<Character> preorderSet = new HashSet<>(preorderInput);
        Set<Character> inorderSet = new HashSet<>(inorderInput);

        if (preorderSet.size() != preorderInput.size() || inorderSet.size() != inorderInput.size()) {
            throw new IllegalArgumentException("traversals must contain unique items");
        }

        if (!preorderSet.equals(inorderSet)) {
            throw new IllegalArgumentException("traversals must have the same elements");
        }

        if (preorderInput.isEmpty()) {
            return new Tree(null);
        }

        Node root = buildTree(preorderInput, inorderInput);
        return new Tree(root);
    }

    private Node buildTree(List<Character> preorder, List<Character> inorder) {
        if (preorder.isEmpty()) {
            return null;
        }

        char rootValue = preorder.get(0);
        Node root = new Node(rootValue);

        int rootIndex = inorder.indexOf(rootValue);

        List<Character> leftInorder = inorder.subList(0, rootIndex);
        List<Character> rightInorder = inorder.subList(rootIndex + 1, inorder.size());

        int leftSize = leftInorder.size();
        List<Character> leftPreorder = preorder.subList(1, 1 + leftSize);
        List<Character> rightPreorder = preorder.subList(1 + leftSize, preorder.size());

        root.left = buildTree(leftPreorder, leftInorder);
        root.right = buildTree(rightPreorder, rightInorder);

        return root;
    }
}
