/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

/**
 *
 * @author 2224715
 */
public class NewClass {
    public static void main(String[] args) {
        for (double i = 0; i < 10000; i++) {
            System.out.println(x1(i)); 
        }
    }
    
    private static double x1(double x) {
        double tAtual = 1, rt = 200.0, lambda = 0.0001;
       
            //x = Math.random() * 50;
            tAtual = x + (rt / 2.0) + (lambda * Math.pow(Math.E, (-lambda) * x));
            
           
        return tAtual;
    }
}
