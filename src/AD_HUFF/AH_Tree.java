package AD_HUFF;

import java.util.*;

public class AH_Tree
{
    public static class Node
    {
        private int counter,node_number;
        private char symbol;
        private String code;
        private Node right,left,parent;

        public Node ()
        {
            counter = 0;
            symbol = '*';
            code = "";
            right = null;
            left = null;
            parent = null;
        }
        public Node (char s)
        {
            counter = 0;
            symbol = s;
            code = "";
            right = null;
            left = null;
            parent = null;
        }
        public String get_code(){ return code; }
        public char get_symbol(){ return symbol; }
        public void show()
        {
            System.out.println(code + " " + symbol + " " + counter + " " + node_number + " ");
        }
    }
    public Comparator<Node> sy_comparator = new Comparator<Node>()
    {
        @Override
        public int compare(Node o1, Node o2)
        {
            if(o1.symbol > o2.symbol)
                return 1;
            else if(o1.symbol < o2.symbol)
                return -1;
            else
                return 0;
        }
    };
    ArrayList<Node> symbols = new ArrayList<Node>();
    Node root,NYT;
    public AH_Tree()
    {
        root = new Node();
        NYT = root;
        NYT.node_number = 100;
    }
    private boolean IsParent(Node p,Node n)
    {
        if(p == root)
            return true;
        Node next = n;
        while(next != root)
        {
            if(next == p)
                return true;
            next = next.parent;
        }
        return false;
    }
    private Node swap_by(Node temp)
    {
        Node next;
        Queue<Node> nodes = new LinkedList<>();
        nodes.add(root);
        while(!nodes.isEmpty())
        {
            next = nodes.poll();
            if(next.right != null)
                nodes.add(next.right);
            if(next.left != null)
                nodes.add(next.left);
            if(!IsParent(next,temp))
            {
                if(next == temp)
                    return null;
                else if(next.counter <= temp.counter && next.node_number > temp.node_number)
                    return next;
            }
        }
        return null;
    }
    private int get_level(Node n)
    {
        if(n == root)
            return 0;
        return get_level(n.parent) + 1;
    }
    private void updateCode(Node n,int idx,char c)
    {
        if(n == null)
            return;
        updateCode(n.left,idx,c);
        n.code = n.code.substring(0,idx) + c + n.code.substring(idx + 1);
        updateCode(n.right,idx,c);
    }
    private void swap(Node s,Node sBy)
    {
        int itemp;char ctemp;
        //swap right sub-tree
        Node temp = s  .right;
        s  .right = sBy.right;
        sBy.right = temp;
        if(s.right != null)
            s  .right.parent = s  ;
        if(sBy.right != null)
            sBy.right.parent = sBy;
        //swap left sub-tree
        temp     = s  .left;
        s  .left = sBy.left;
        sBy.left = temp;
        if(s.left != null)
            s  .left.parent = s  ;
        if(sBy.left != null)
            sBy.left.parent = sBy;
        //swap content except node number
        itemp       = s  .counter;ctemp      = s  .symbol;
        s  .counter = sBy.counter;s  .symbol = sBy.symbol;
        sBy.counter = itemp      ;sBy.symbol = ctemp     ;
        //update children codes
        itemp = get_level(s);
        updateCode(s.right,itemp - 1,s.code.charAt(s.code.length() - 1));
        updateCode(s.left,itemp - 1,s.code.charAt(s.code.length() - 1));
        itemp = get_level(sBy);
        updateCode(sBy.right,itemp - 1,sBy.code.charAt(sBy.code.length() - 1));
        updateCode(sBy.left,itemp - 1,sBy.code.charAt(sBy.code.length() - 1));
    }
    private void updateSymbols(Node n)
    {
        if(n.symbol != '*')
        {
            int s_idx = Collections.binarySearch(symbols,n,sy_comparator);
            if(s_idx < 0)
            {
                s_idx = Collections.binarySearch(symbols,new Node('*'),sy_comparator);
            }
            symbols.set(s_idx, n);
            Collections.sort(symbols, sy_comparator);
        }
    }
    public void add(char s)
    {
        int s_idx = Collections.binarySearch(symbols,new Node(s),sy_comparator);
        Node temp,swap_by;
        if(s_idx < 0)
        {
            //split
            NYT.right = new Node(s);
            NYT.left = new Node();
            //right
            temp = NYT.right;
            temp.node_number = NYT.node_number - 1;
            temp.parent = NYT;
            temp.code = temp.parent.code + '1';
            //left
            NYT.left.node_number = NYT.node_number - 2;
            NYT.left.parent = NYT;
            NYT.left.code = NYT.left.parent.code + '0';
            //set counters by 1
            NYT.counter++;
            temp.counter++;

            //set new NYT
            NYT = NYT.left;
            /**add new symbol to symbols list**/
            symbols.add(temp);
            Collections.sort(symbols,sy_comparator);
            //go to old NYT
            temp = temp.parent;
            if(temp == root)
                return;
            else
                //go to parent
                temp = temp.parent;
        }
        else
            //go to symbol
            temp = symbols.get(s_idx);
        while (true)
        {
            swap_by = swap_by(temp);
            if(swap_by != null)
            {
                swap(temp,swap_by);
                temp = swap_by;
            }
            temp.counter++;
            /**update symbols list**/
            if(swap_by != null)
            {
                Collections.sort(symbols, sy_comparator);
                updateSymbols(swap_by);
                updateSymbols(temp);
            }
            if(temp == root)
                return;
            //go to parent
            temp = temp.parent;
        }
    }
    public Node find_code(String s,Node curr,int i)
    {
        if(curr == null)
            return null;
        if(curr.left == null && curr.right == null && curr.code.equals(s))
            return curr;
        if(i == s.length())
            return null;
        else
        {
            if(s.charAt(i) == '0')
                return find_code(s,curr.left,i + 1);
            else
                return find_code(s,curr.right,i + 1);
        }
    }
    public void showBreadth()
    {
        Node next = root;
        Queue<Node> nodes = new LinkedList<>();
        nodes.add(next);
        while(!nodes.isEmpty())
        {
            next = nodes.poll();
            if(next.left != null)
                nodes.add(next.left);
            if(next.right != null)
                nodes.add(next.right);
            next.show();
        }
    }
}

