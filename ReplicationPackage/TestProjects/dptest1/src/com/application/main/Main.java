package com.application.main;

import com.application.manager.RegisterManager;

/**
 *
 * @author Andrej Mlyncar <a.mlyncar@gmail.com>
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        RegisterManager manager = new RegisterManager();
        
        manager.createRegistry();
        
        
    }
    
}
