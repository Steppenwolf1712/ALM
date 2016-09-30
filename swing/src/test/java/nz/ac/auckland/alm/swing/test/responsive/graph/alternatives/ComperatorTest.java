package nz.ac.auckland.alm.swing.test.responsive.graph.alternatives;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by Marc Janßen on 30.09.2016.
 */
public class ComperatorTest {

    public static void main(String args[]) {
        ArrayList<Integer> numbers = new ArrayList<Integer>();

        numbers.add(456);
        numbers.add(66);
        numbers.add(76);
        numbers.add(26);
        numbers.add(49);
        numbers.add(73);
        numbers.add(1);
        numbers.add(88);

        System.out.println("Die Liste momentan: ");
        for (int i = 0; i<numbers.size(); i++)
            System.out.print(numbers.get(i)+", ");

        numbers.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });

        System.out.println("Die Liste nach dem sortieren: ");
        for (int i = 0; i<numbers.size(); i++)
            System.out.print(numbers.get(i)+", ");
    }
}
