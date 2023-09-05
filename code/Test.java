import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.regex.Pattern;

class TrieNode {
    HashMap<Character, TrieNode> children;
    int count;
    TrieNode fail;
    TrieNode brother;

    public TrieNode() {
        children = new HashMap<>();
        count = 0;
        fail = null;
        brother = null;
    }
}

class Trie {
    TrieNode root;
    private static final Pattern REGEX = Pattern.compile("[^a-zA-Z0-9\u4E00-\u9FA5]"); // 预编译正则表达式

    public Trie() {
        root = new TrieNode();
    }

    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
        }
        node.count++;
    }

    public void buildFailureAndBrotherLinks(boolean buildBrother) {
        Queue<TrieNode> queue = new LinkedList<>();
        for (TrieNode child : root.children.values()) {
            child.fail = root;
            queue.add(child);
        }

        while (!queue.isEmpty()) {
            TrieNode current = queue.poll();
            for (char c : current.children.keySet()) {
                TrieNode child = current.children.get(c);
                queue.add(child);

                TrieNode failNode = current.fail;
                while (failNode != null && !failNode.children.containsKey(c)) {
                    failNode = failNode.fail;
                }

                if (failNode != null) {
                    child.fail = failNode.children.get(c);
                } else {
                    child.fail = root;
                }

                if (buildBrother) {
                    if (child.fail.count > 0) {
                        child.brother = child.fail;
                    } else {
                        child.brother = child.fail.brother;
                    }
                }
            }
        }
    }

    public int query(String text) {
        int count = 0;
        TrieNode current = root;

        for (char c : text.toCharArray()) {
            while (current != null && !current.children.containsKey(c)) {
                current = current.fail;
            }

            if (current == null) {
                current = root;
                continue;
            }

            current = current.children.get(c);
            TrieNode temp = current;
            while (temp != null) {
                count += temp.count;
                temp.count = 0;
                temp = temp.brother;
            }
        }

        return count;
    }

    public String precheck(String text) {
        // 使用正则表达式替换特殊字符为空字符串
        StringBuilder checkedText = new StringBuilder(REGEX.matcher(text).replaceAll(""));

        return checkedText.toString();
    }
}

public class Test {
    public static void solve(Scanner scanner) {
        Trie trie = new Trie();

        int n = scanner.nextInt();
        for (int i = 0; i < n; i++) {
            String s = scanner.next();
            trie.insert(s);
        }

        trie.buildFailureAndBrotherLinks(true);

        String text = scanner.next();

        text = trie.precheck(text);

        int result = trie.query(text);
        System.out.println(result);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int T = scanner.nextInt();
        while (T-- > 0) {
            solve(scanner);
        }
    }
}
