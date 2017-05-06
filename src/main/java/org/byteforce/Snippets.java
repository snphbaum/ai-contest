package org.byteforce;

import java.util.concurrent.ConcurrentSkipListSet;


/**
 * @author Philipp Baumgaertel
 */
public class Snippets
{

    public static void main(String[] args)
    {
        ConcurrentSkipListSet<String> test = new ConcurrentSkipListSet<>();

        test.add("Hello");
        test.add("World");
        test.add("Foo");
        test.add("Bar");
        test.add("!");

        for (String s : test) {
            System.out.println(s);
            if(s == "Foo") {
                test.remove(s);
            }
        }
        System.out.println(test);
    }
}
