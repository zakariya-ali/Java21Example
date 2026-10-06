package org.example.solid;

import org.example.solid.dip.DependencyInversionDemo;
import org.example.solid.isp.InterfaceSegregationDemo;
import org.example.solid.lsp.LiskovSubstitutionDemo;
import org.example.solid.ocp.OpenClosedDemo;
import org.example.solid.srp.SingleResponsibilityDemo;

/**
 * Runs every SOLID principle example in order.
 */
public class SolidShowcase {

    public static void run() {
        System.out.println();
        System.out.println("=".repeat(70));
        System.out.println(" SOLID Principles");
        System.out.println("=".repeat(70));
        SingleResponsibilityDemo.run();
        OpenClosedDemo.run();
        LiskovSubstitutionDemo.run();
        InterfaceSegregationDemo.run();
        DependencyInversionDemo.run();
    }
}
