
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Classe responsable de construire la fenêtre de navigation des parties. 
 * Cette fenêtre est la seconde ouverte (juste après une connexion réussie).
 * Elle affiche une liste de parties. Chaque partie peut être cliquée pour la
 * rejoindre. Un champs de texte peut être rempli, ensuite le bouton "new party"
 * peut être appuyer pour créer une nouvelle partie.
 */
public class GuiPartyBrowser extends JFrame implements Observer {

    /**
     * last update : 07 / 11 / 2015
     */
    private static final long serialVersionUID = 20151107L;

    private final IClient client;
    private GUI gui;

    private final JTable tbParties;
    private final GenericTableModel<Party> mParties;

    private final JTextField tfInputParty;
    private final JButton btNewParty;

    public GuiPartyBrowser(IClient client) {
        this.client = client;
        this.gui = gui;
        mParties = new GenericTableModel<Party>(client.getParties(),
                new String[]{"name"});
        tbParties = new JTable(mParties);
        tfInputParty = new JTextField();
        btNewParty = new JButton("New Party");

        setTitle("RMI BattleArena - Parties Browser");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initialize();
        build();
    }

    private void initialize() {
        addWindowListener(new CloseOperation());
        tbParties.getSelectionModel().addListSelectionListener(
                new JoinPartyOperation());
        btNewParty.addActionListener(new CreatePartyOperation());
    }

    private void build() {
        setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(tbParties);
        tbParties.setFillsViewportHeight(true);
        tbParties.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(tfInputParty, BorderLayout.CENTER);
        panel.add(btNewParty, BorderLayout.EAST);
        add(panel, BorderLayout.SOUTH);
    }

    public void dialog(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    /**
     * Permet d'ouvrir la zone de jeu
     */
    private void openArena() {
        Arena arena = client.getArena();
        if (arena != null) {
           gui = new GUI(client);
            client.setGui(gui);
           // arena.setState(ArenaState.Started);
            arena.addObserver(gui);

            gui.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    super.windowClosing(e);
                    closeArena();
                }
            });

          
            gui.setVisible(true);

            gui.jButton1.setEnabled(true);
              client.newGame();
            this.setVisible(false);

        }
    }

    /**
     * Ferme la zone de jeu
     */
    private void closeArena() {
        this.setVisible(true);
        if (gui != null) {
            gui.setVisible(false);
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof IClient) {
            if (data instanceof ArenaState) {
                switch ((ArenaState) data) {
                    case Interrupted:
                        closeArena();
                        break;
                    default:
                        break;
                }
            }
            if (data instanceof Operation) {
                Operation operation = (Operation) data;
                switch (operation.getAction()) {
                    case Add:
                        if (operation.getData() instanceof Party) {
                            Party party = (Party) operation.getData();
                            mParties.addValue(party);
                        }
                        break;
                    default:
                        break;
                }
            }
            if (data instanceof ClientState) {
                switch ((ClientState) data) {
                    case Disconnected:
                        closeArena();
                        this.setVisible(false);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private class CreatePartyOperation implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String name = tfInputParty.getText();
            if (name.isEmpty()) {
                name = client.getUsername() + "'s Party";
            }
            if (client.createParty(name) && client.joinParty(name)) {
                openArena();
            }
        }
    }

    private class JoinPartyOperation implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            ListSelectionModel model = (ListSelectionModel) e.getSource();
            int row = model.getMinSelectionIndex();

            Party party = mParties.getObjectAt(row);
            if (party == null) {
                return;
            }
            if (client.joinParty(party.getName()) && client.getArena() != null) {
                openArena();
            } else if (client.isConnected()) {
                dialog("Party '" + party.getName() + "' is unjoinable, try another.");
            }
            model.clearSelection();
        }
    }

    private class CloseOperation extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent e) {
            super.windowClosing(e);
            client.disconnect();
        }
    }
}
