/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.io.Serializable;
import util.Serializer;

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
        A a = new A(1,2,3);
        byte[] b = Serializer.serializeObj(a);
        A a2 = (A)Serializer.recoverObj(b);
        System.out.println(b.length);
        System.out.println(a2);
    }
    
    private static double x1(double x) {
        double tAtual = 1, rt = 200.0, lambda = 0.0001;
       
            //x = Math.random() * 50;
            tAtual = x + (rt / 2.0) + (lambda * Math.pow(Math.E, (-lambda) * x));
            
           
        return tAtual;
    }
}
