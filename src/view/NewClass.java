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
        double r =  Math.random() * 200;
        //System.out.println("r:"+r);
        r=Math.round(r);
        System.out.println("r:"+r);
        //System.out.println("r round:" +Math.round(r));
        
        //for (int i=1; i<400; i++)
        //{
            x1(r);
            //System.out.println("\n Valor: "+x1(r));
        //}
    }
    
    private static double x1(double x) {
        double tAtual = 1, lambda = 0.5, rtt=200, exp=0, er; 
        
        ExponentialDistribution e = new ExponentialDistribution(x);
        exp = e.cumulativeProbability(x);
        //exp = lambda * Math.pow(Math.E, (-lambda) * x);
        //Math.round(exp);
        //System.out.println("\nexp: "+exp);
        System.out.println("\nEXP: "+exp);
        tAtual = x + (rtt / 2.0) + exp;
        System.out.println("\ntempo: "+tAtual);
        return tAtual;
           
    }
}
