/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.io.Serializable;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import static java.time.temporal.TemporalQueries.zone;
import java.util.Date;
import util.Serializer;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.random.AbstractRandomGenerator;

/**
 *
 * @author 2224715
 */
public class NewClass{
    
    static class A implements Serializable{
        public int x;
        public int y;
        public transient int z;

        public A(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public String toString() {
            return "A{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
        }
        
    }
    
    public static void main(String[] args) {
//        A a = new A(1,2,3);
//        byte[] b = Serializer.serializeObj(a);
//        A a2 = (A)Serializer.recoverObj(b);
//        System.out.println(b.length);
//        System.out.println(a2);
        for (int i=1; i<400; i++)
        {
            System.out.println("\n Valor: "+x1(i));
        }
    }
    
    private static double x1(double x) {
        double tAtual = 1, lambda = 0.5, rtt=200, n2=0, test=0; 
        ExponentialDistribution t = new ExponentialDistribution(rtt);
          // P(T(29) <= -2.656)
        AbstractRandomGenerator n = new AbstractRandomGenerator() {
            @Override
            public void setSeed(long seed) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public double nextDouble() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        test = t.cumulativeProbability(rtt);
        n2=n.nextLong();
       
//        //double upperTail = 1.0 - t.cumulativeProbability(2.75); // P(T(29) >= 2.75)
//
//        tAtual = (test + (rtt / 2.0) + test);
//        tAtual = tAtual * 10;

        //x = Math.random() * 50;
        System.out.println("\ntest: "+test);
        System.out.println("\nn2: "+n2);
        //System.out.println("\nt: "+t);
        
        tAtual = (test + (rtt / 2.0) + (lambda * Math.pow(Math.E, (-lambda) * test)));

        return tAtual;
           
    }
}
