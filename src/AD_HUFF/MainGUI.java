package AD_HUFF;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainGUI
{
    public static char gc;
    public static HashMap<Character,String> short_codes = new HashMap<>();
    public static HashMap<String,Character> codes_short = new HashMap<>();

    public static void show_short_codes()
    {
        for (Map.Entry<Character,String > itr: short_codes.entrySet())
            System.out.println(itr.getKey() + " " + itr.getValue());
    }
    public static void codeSet(int n,int[] arr,int i)
    {
        if(i == n)
        {
            if(gc == '*')
                gc++;
            String bcode = new String();
            for (int c: arr)
                bcode += (char)c;
            short_codes.put(gc,bcode);
            codes_short.put(bcode,gc);
            gc++;
            return;
        }
        arr[i] = (int)'0';
        codeSet(n,arr,i+1);
        arr[i] = (int)'1';
        codeSet(n,arr,i+1);
        /*short_codes.put('A',"00");
        short_codes.put('B',"01");
        short_codes.put('C',"10");
        codes_short.put("00",'A');
        codes_short.put("01",'B');
        codes_short.put("10",'C');*/
    }
    public static String compress(String msg)
    {
        AH_Tree mytree = new AH_Tree();
        String code = "";
        for(int i = 0;i < msg.length();i++)
        {
            int c_idx = Collections.binarySearch(mytree.symbols,new AH_Tree.Node(msg.charAt(i)),mytree.sy_comparator);
            if(c_idx < 0)
            {
                if(!code.equals(""))
                    code += mytree.NYT.get_code();
                code += short_codes.get(msg.charAt(i));
            }
            else
                code += mytree.symbols.get(c_idx).get_code();
            mytree.add(msg.charAt(i));
        }
        return code;
    }
    public static String decompress(String code)
    {
        AH_Tree mytree = new AH_Tree();
        String msg = "",temp = "";
        int code_length = 7;
        boolean is_short = false;
        for(int  i = 0;i < code.length();i++)
        {
            if(msg.equals("") || is_short)
            {
                temp = code.substring(i,i + code_length);
                msg += codes_short.get(temp);
                mytree.add(codes_short.get(temp));
                i += code_length - 1;
                is_short = false;
                temp = "";
            }
            else
            {
                temp += code.charAt(i);
                AH_Tree.Node code_node = mytree.find_code(temp,mytree.root,0);
                if(code_node != null)
                {
                    if(code_node.get_symbol() == '*')
                        is_short = true;
                    else
                    {
                        msg += code_node.get_symbol();
                        mytree.add(code_node.get_symbol());
                        temp = "";
                    }
                }
            }
        }
        return msg;
    }

    private JPanel MNpan;
    private JButton btn;
    private JTextField txt;

    public MainGUI()
    {
        btn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    /**creat short codes dictionary**/
                    gc = 0; int[] bcode = new int[7];
                    codeSet(7,bcode,0);

                    /**creat compression result file & open streams to it**/
                    FileOutputStream out = new FileOutputStream("code.txt");
                    FileInputStream in = new FileInputStream("code.txt");

                    /**compression**/
                    String msg = txt.getText() , res = compress(msg);
                    System.out.println(res);
                    out.write(res.getBytes());
                    out.close();

                    /**read the compression result code from code file**/
                    byte[] bytes =  in.readAllBytes();
                    res = new String(bytes);

                    /**decompression**/
                    res = decompress(res);
                    System.out.println(res);
                    out = new FileOutputStream("dec.txt");
                    out.write(res.getBytes());
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }

            }
        });
    }

    public static void main(String[] args)
    {
        JFrame jf = new JFrame("Main GUI");
        jf.setContentPane(new MainGUI().MNpan);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
    }
}
