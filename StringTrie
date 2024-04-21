package ipv4v2;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class TrieNode{
    //[0~9] .
    String value;
    public TrieNode[] children = new TrieNode[13];

    public byte childSize=0;

    public TrieNode(String value) {
        this.value = value;
    }

    //1.插入
    public void insert(String word) {
        recursionSplit(this, word);
    }

    public boolean childExist(int i){
        return children[i] != null;
    }

    public TrieNode childGet(int i){
        return children[i];
    }

    public int childPut(String v){
        int index = v.charAt(0) - '0';
        if (index < 0) {
            index = 10;
        }

        TrieNode newNode = new TrieNode(v);
        children[index] = newNode;
        newNode.chilPutSpace();
        Trie.wordNum.getAndIncrement();
        return  ++childSize;
    }

    public TrieNode childPut(TrieNode node){
        String v = node.value;
        int index = v.charAt(0) - '0';
        if (index < 0) {
            index = 10;
        }

        children[index] = node;
        Trie.wordNum.getAndIncrement();
        return  node;
    }

    public void chilPutSpace() {
        children[children.length - 1] = new TrieNode("");
        ++childSize;
    }

    public static void recursionSplit(TrieNode roottrieNode, String word) {
        if (word.length() == 0) {
            return;
        }
        int headIndex = word.charAt(0) - '0';
        if (headIndex < 0) {
            headIndex = 10;
        }

        if (roottrieNode.childExist(headIndex)) {
            //1.找相同的前缀
            TrieNode trieNode = roottrieNode.childGet(headIndex);
            String rvalue = trieNode.value;

            for (int i = 0; i < word.length(); i++) {
                if (rvalue.charAt(i) != word.charAt(i)) {
                    trieNode.value = rvalue.substring(0, i);
                    //1.1将切分出的2个不同的后缀下放到子节点
                    if (trieNode.childSize == 0)
                        //1.11 直接放入原有节点
                        trieNode.childPut(rvalue.substring(i));
                    else
                        //1.12 加入中间节点
                        fill(trieNode, rvalue.substring(i));

                    //新后缀放入
                    trieNode.childPut(word.substring(i));
                    break;
                }

                if (i == word.length() - 1) {
                    if (rvalue.length() == word.length()) {
                        trieNode.chilPutSpace();
                        break;
                    }
                    trieNode.value = word;
                    //将rvalue 后缀下放
                    fill(trieNode, rvalue.substring(i + 1));
                    trieNode.chilPutSpace();
                    break;
                }

                if (i == rvalue.length() - 1) {
                    //将word 后缀加入
                    trieNode.value = rvalue;
                    recursionSplit(trieNode, word.substring(i + 1) );
                    break;
                }
            }
        } else {
            roottrieNode.childPut(word); //直接放入
        }
    }

    public static void fill(TrieNode node, String fillstr) {
        TrieNode[] nextNodes = node.children;
        node.children = new TrieNode[13];

        byte nextSize = node.childSize;
        node.childSize = 1;

        TrieNode trieNode = new TrieNode(fillstr);
        node.childPut(trieNode);
        trieNode.children= nextNodes;
        trieNode.childSize = nextSize;
    }

    //2.查找
    public List<String> searchList(String prefix) {
        List<String> list = findArr(this, prefix);
        return list;
    }


    public List<String> recursionMerge(TrieNode trieNode) {
        if (trieNode == null) {
            return new ArrayList<>(0);
        }

        ArrayList<String> list = new ArrayList<>();
        String subfix = trieNode.value;

        if (trieNode.childSize == 0) {
            //最底层叶子节点 元素放入
            return Arrays.asList(subfix);
        }
        for (TrieNode node : trieNode.children) {
            List<String> resList = concatEle(recursionMerge(node), subfix);
            list.addAll(resList);
            //if (list.size() >= 100000) {
            //    return list;
            //}
        }

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
        int index = strfix.charAt(0) - '0';
        if (index < 0) {
            index = 10;
        }

        TrieNode trien = node.childGet(index);
        if (trien!=null) {
            TrieNode trieNode = trien;


            if (trieNode.value.equals(strfix) && trieNode.childSize == 0) {
                return Arrays.asList(strfix);
            }

            if (trieNode.value.startsWith(strfix)) {
                //搜索这个节点的所有结果作为列表返回
                if (trieNode.childSize > 0) {
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
        for (TrieNode trieNode : this.children) {
            if (trieNode != null) {
                list.addAll(recursionMerge(trieNode));
                //if (list.size() >= 100000) {
                //    return list;
                //}
            }
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
    static {
        long start = System.currentTimeMillis();
        try {
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

//            Files.lines(Paths.get("dir/ip.txt")).distinct().forEach(list::add);

//            trie.insert("195.233.153.2");
//            trie.insert("195.233.153.233");

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

       // List<String> searchList =list.stream().filter(f->f.startsWith(arg)).collect(Collectors.toList());


        long end = System.nanoTime();

        System.out.println("searchList = " + searchList);
        System.out.println("searchList.size() = " + searchList.size());
        System.out.println("[" + simpleDateFormat.format(System.currentTimeMillis()) + "] 检索用时:" + String.format("%.2f", (end - start)  / 1000) + " ms");
    }


}
