package ipv4v3;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

//基于链表的字符串前缀树
class TrieNode {
    String value;

    public TrieNode downTrieNode;
    public TrieNode leftTrieNode;

    public TrieNode(String value) {
        this.value = value;
    }

    //1.插入
    public void insert(String word) {
        recursionSplit(this, word);
    }

    public static TrieNode childGet(TrieNode childNode, int i) {
        if (childNode == null || childNode.value.length() == 0)
            return null;
        if (childNode.value.charAt(0) == i) {
            return childNode;
        }
        return childGet(childNode.leftTrieNode, i);
    }

    //在链表末尾追加个元素
    public static void childPut(TrieNode childNode, String v) {
        if (childNode.value.length() == 0) {//为 "" 情况
            childNode.value = v;
            childNode.chilPutSpace();
            childNode.leftTrieNode = new TrieNode("");
            return;
        }
        TrieNode lastNode = findLeftLastNode(childNode);

        if (lastNode.leftTrieNode == null) {
            lastNode.leftTrieNode = new TrieNode(v);
        } else if (lastNode.leftTrieNode.value.length() == 0) {
            lastNode.leftTrieNode.value = v;
            lastNode.leftTrieNode.downTrieNode = new TrieNode("");
            lastNode.leftTrieNode.leftTrieNode = new TrieNode("");
        }

    }

    public static TrieNode findLeftLastNode(TrieNode node) {
        if (node.leftTrieNode == null || node.leftTrieNode.value.length() == 0)
            return node;
        else
            return findLeftLastNode(node.leftTrieNode);
    }

    public void chilPutSpace() {
        if (this.downTrieNode == null) {
            this.downTrieNode = new TrieNode("");
            return;
        }
        TrieNode lastNode = findLeftLastNode(this.downTrieNode);
        if (lastNode.value.length() != 0) {
            lastNode.leftTrieNode = new TrieNode("");
        }
    }

    public static void recursionSplit(TrieNode roottrieNode, String word) {
        if (word.length() == 0) {
            return;
        }

        int headIndex = word.charAt(0);

        TrieNode childNode = childGet(roottrieNode.downTrieNode, headIndex);
        if (childNode != null) {
            //1.找相同的前缀
            TrieNode trieNode = childNode;
            String rvalue = trieNode.value;

            for (int i = 0; i < word.length(); i++) {
                if (rvalue.charAt(i) != word.charAt(i)) {
                    trieNode.value = rvalue.substring(0, i);
                    //1.1将切分出的2个不同的后缀下放到子节点
                    if (trieNode.downTrieNode == null)
                        //1.11 直接放入原有节点
                        trieNode.downTrieNode = new TrieNode(rvalue.substring(i));
                    else
                        //1.12 加入中间节点
                        childFill(trieNode, rvalue.substring(i));

                    //新后缀直接放入
                    childPut(trieNode.downTrieNode, word.substring(i));
                    break;
                }

                if (i == word.length() - 1) {
                    if (rvalue.length() == word.length()) {
                        trieNode.chilPutSpace();
                        break;
                    }
                    trieNode.value = word;
                    //将rvalue 后缀下放
                    childFill(trieNode, rvalue.substring(i + 1));
                    trieNode.chilPutSpace();
                    break;
                }

                if (i == rvalue.length() - 1) {
                    //将word 后缀加入
                    trieNode.value = rvalue;
                    if (trieNode.downTrieNode == null) {
                        trieNode.chilPutSpace();
                    }
                    recursionSplit(trieNode, word.substring(i + 1));
                    break;
                }
            }
        } else {
            if (roottrieNode.downTrieNode == null) {
                roottrieNode.downTrieNode = new TrieNode(word);
                roottrieNode.downTrieNode.chilPutSpace();
            } else {
                childPut(roottrieNode.downTrieNode, word); //直接放入
            }

        }
    }

    public static void childFill(TrieNode node, String fillstr) {
        TrieNode downNode = node.downTrieNode;
        TrieNode middleNode = new TrieNode(fillstr);
        node.downTrieNode = middleNode;
        middleNode.downTrieNode = downNode;
    }

    //2.查找
    public List<String> searchList(String prefix) {
        List<String> list = findArr(this, prefix);
        return list;
    }

    public static List<String> concatEle(List<String> list, String prefix) {
        for (int i = 0; i < list.size(); i++) {
            String ele = list.get(i);
            list.set(i, prefix + ele);
        }
        return list;
    }

    public static List<String> findArr(TrieNode node, String strfix) {
        int index = strfix.charAt(0);

        TrieNode childNode = childGet(node.downTrieNode, index);
        if (childNode != null) {
            TrieNode trieNode = childNode;

            if (trieNode.value.equals(strfix) && trieNode.downTrieNode == null) {
                return Arrays.asList(strfix);
            }

            if (trieNode.value.startsWith(strfix)) {
                //搜索这个节点的所有结果作为列表返回
                if (trieNode.downTrieNode != null) {
                    return concatEle(trieNode.find(), trieNode.value);
                } else {
                    return Arrays.asList(trieNode.value);
                }
            }

            if (strfix.startsWith(trieNode.value)) {
                String substring = strfix.substring(trieNode.value.length());
                String prestring = strfix.substring(0, trieNode.value.length());

                //搜索这个节点的5个结果作为列表返回
                return concatEle(findArr(trieNode, substring), prestring);
            }
        }

        return Arrays.asList();
    }

    public List<String> find() {
        ArrayList<String> list = new ArrayList<>();
        ArrayList<TrieNode> listNode = new ArrayList<>();
        leftRecur(this.downTrieNode, listNode);
        for (TrieNode trieNode : listNode) {
            list.addAll(recursionMerge(trieNode));
        }

        return list;
    }

    public static void leftRecur(TrieNode node, List<TrieNode> list) {
        if (node.leftTrieNode != null) {
            leftRecur(node.leftTrieNode, list);
        }

        list.add(node);
    }

    public List<String> recursionMerge(TrieNode trieNode) {

        ArrayList<String> list = new ArrayList<>();
        String subfix = trieNode.value;

        if (trieNode.downTrieNode == null) {
            //最底层叶子节点 元素放入
            return Arrays.asList(subfix);
        }
        ArrayList<TrieNode> trieNodes = new ArrayList<>();
        leftRecur(trieNode.downTrieNode, trieNodes);
        for (TrieNode node : trieNodes) {
            List<String> resList = concatEle(recursionMerge(node), subfix);
            list.addAll(resList);
//            if (list.size() >= 100000) {
//                return list;
//            }
        }

        return list;
    }
}

public class Trie {
    private TrieNode root;

    public Trie() {
        root = new TrieNode("ROOT");
    }

    public void insert(String word) {
        root.insert(word);
    }

    public List<String> search(String word) {
        return root.searchList(word);
    }

    public static Trie trie = new Trie();
    public static AtomicInteger wordNum = new AtomicInteger(0);

    static ArrayList<String> list = new ArrayList<>();

    static void put(TrieNode trieNode, String v) {
        if (trieNode.leftTrieNode == null) {
            trieNode.leftTrieNode = new TrieNode(v);
            return;
        }

        put(trieNode.leftTrieNode, v);
    }

    static {


        long start = System.currentTimeMillis();
        try {
            trie.root.downTrieNode = new TrieNode("105.195.126.72");
            put(trie.root.downTrieNode, "242.196.95.14");
            put(trie.root.downTrieNode, "32.247.217.204");
            put(trie.root.downTrieNode, "46.97.57.69");
            put(trie.root.downTrieNode, "51.63.119.190");
            put(trie.root.downTrieNode, "66.190.92.166");
            put(trie.root.downTrieNode, "7.238.112.35");
            put(trie.root.downTrieNode, "82.54.206.108");
            put(trie.root.downTrieNode, "96.47.241.124");
            put(trie.root.downTrieNode, "0.24.174.50");

            HashMap<Integer, List<String>> map = new HashMap<>();
            IntStream.rangeClosed('0', '9').boxed().forEach(c -> map.put(c, new ArrayList<>()));
            Files.lines(Paths.get("dir/ip.txt")).forEach(line -> {
                Integer c = (int) line.charAt(0);
                map.get(c).add(line);
            });
            map.values().forEach(list -> {
                Thread thread = new Thread(() -> {
                    list.forEach(word -> trie.insert(word.trim().toLowerCase()));
                    System.out.println(Thread.currentThread().getName() + " DONE!");
                });
                thread.start();
            });


            // Files.lines(Paths.get("dir/ip.txt")).forEach(list::add);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        long end = System.currentTimeMillis();
        System.out.println((end - start) / 1000);
    }


    public static void main(String[] args) throws IOException {

        while (true) {
            // 创建Scanner对象，它使用标准输入流System.in
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter query IPV4:");
            test(scanner.nextLine());
            System.out.println();
        }
    }

    static void test(String arg) throws IOException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:S");

        double start = (double) System.nanoTime();
        List<String> searchList = Trie.trie.search(arg);

//         FileUtils.writeLines(new File("dir/3.txt"), searchList);

        // List<String> searchList =list.stream().filter(f->f.startsWith(arg)).collect(Collectors.toList());

        long end = System.nanoTime();

        System.out.println("searchList = " + searchList);
        System.out.println("searchList.size() = " + searchList.size());
        System.out.println("[" + simpleDateFormat.format(System.currentTimeMillis()) + "] 检索用时:" + String.format("%.2f", (end - start) / 1000) + " ws");

        HashSet<String> set = new HashSet<>();
        searchList.forEach(line -> {
            if (set.contains(line)) {
                System.out.println(line);
            } else {
                set.add(line);
            }
        });

    }
}
