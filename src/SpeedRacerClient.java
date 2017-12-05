
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author brasc
 */
public class SpeedRacerClient {

    private static String ipAddress;
    private static String name;
    private static String registryURL;
    private static String adversaire;
    private static long id;

    public static void main(String[] args) {

        start();
    }

    private static void display() {

        JTextField nom = new JTextField();
        JTextField ad = new JTextField();
        JPanel panel = new JPanel(new GridLayout(0, 1));

        panel.add(new JLabel("Nom"));
        panel.add(nom);
        panel.add(new JLabel("@IP serveur"));
        panel.add(ad);

        int result = JOptionPane.showConfirmDialog(null, panel, "LOGIN",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {

            ipAddress = ad.getText();
            name = nom.getText();

        } else {
            //panel.setVisible(false);
            System.exit(0);
        }
    }

    public static void start() {

        display();

        registryURL = "rmi://" + ipAddress + ":" + 1099 + "/GameServer";

        try {

            // crée le client pour la connexion et l'authentification
            ClientEngine client = new ClientEngine(name);

            // essaye de se connecter
            // si connexion retourne true, on est connecté
            if (client.connect(registryURL, Constants.SERVER_PORT)) {

                GuiPartyBrowser browser = new GuiPartyBrowser(client);
                client.addObserver(browser);
                browser.setVisible(true);
            } else {
                // sinon affiche de nouveau la fenêtre de connexion

            }
        } catch (RemoteException ex) {

        }

        // donne la tâche à réaliser pour se connecter à la fenêtre de connexion
    }

}
