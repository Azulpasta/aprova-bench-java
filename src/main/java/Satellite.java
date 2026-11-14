import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class Satellite {
    public Tree treeFromTraversals(List<Character> preorderInput, List<Character> inorderInput) {
        if (preorderInput.size() != inorderInput.size()) {
            throw new IllegalArgumentException("traversals must have the same length");
        }

        Set<Character> preorderSet = new HashSet<>(preorderInput);
        if (preorderSet.size() != preorderInput.size()) {
            throw new IllegalArgumentException("traversals must contain unique items");
        }

        Set<Character> inorderSet = new HashSet<>(inorderInput);
        if (inorderSet.size() != inorderInput.size()) {
            throw new IllegalArgumentException("traversals must contain unique items");
        }

        if (!preorderSet.equals(inorderSet)) {
            throw new IllegalArgumentException("traversals must have the same elements");
        }

        return new Tree(buildNode(preorderInput, inorderInput));
    }

    private Node buildNode(List<Character> preorder, List<Character> inorder) {
        if (preorder.isEmpty()) {
            return null;
        }

        char rootValue = preorder.get(0);
        int splitIndex = inorder.indexOf(rootValue);

        Node left = buildNode(preorder.subList(1, 1 + splitIndex), inorder.subList(0, splitIndex));
        Node right = buildNode(preorder.subList(1 + splitIndex, preorder.size()), inorder.subList(splitIndex + 1, inorder.size()));

        return new Node(rootValue, left, right);
    }
}
