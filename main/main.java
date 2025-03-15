package main;

import javax.swing.JFrame;

public class main {
    public static void main(String[] args) {
        // Demonstrate dynamic binding before launching the game
        System.out.println("Running OOP principle demonstrations...");
        PolymorphismDemo.runDemo();
        System.out.println("\nLaunching chess game...");

        //Create a JFrame Window
        JFrame window = new JFrame("Simple Chess");

        //Set Close Operation
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Prevent Resizing
        window.setResizable(false);

        //Add main.GamePanel to the window
        GamePanel gp = new GamePanel();
        window.add(gp);

        //Automatically sizes the window
        window.pack();

        //Center the Window
        window.setLocationRelativeTo(null);

        //Show the Window
        window.setVisible(true);

        //Start the Game
        gp.launchGame();
    }
}
