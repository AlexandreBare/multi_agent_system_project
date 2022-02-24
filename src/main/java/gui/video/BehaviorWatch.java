package gui.video;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.google.common.eventbus.Subscribe;

import environment.ActiveItemContainer;
import environment.ActiveItemID;
import util.event.BehaviorChangeEvent;

public class BehaviorWatch extends JFrame {
    final ActiveItemContainer ais;
    final List<ActiveItemID> ids;

    private final JTable jTable1;
    private final JScrollPane jScrollPane1;

    public BehaviorWatch(ActiveItemContainer ais) {
        this.ais = ais;
        Object[][] data = new Object[ais.getNbAgents()][2];
        ids = ais.getAllAgentIDs();

        for (int i = 0; i < ids.size(); i++) {
            data[i][0] = ais.getEnvironment().getAgentWorld().getAgent(ids.get(i)).getName();
        }
        Object[] columns = {
            "Agent Name", "Behavior"};
        jTable1 = new JTable(data, columns);
        jScrollPane1 = new JScrollPane(jTable1);
    }

    @Subscribe
    private void handleBehaviorChangeEvent(BehaviorChangeEvent e) {
        for (int i = 0; i < ids.size(); i++) {
            if (ids.get(i) == e.getAgent()) {
                jTable1.getModel().setValueAt(e.getBehaviorName(), i, 1);
            }
        }
    }

    public void initialize() {
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(20);
        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);
        setSize(new java.awt.Dimension(300, 75 + jTable1.getRowHeight() * jTable1.getRowCount()));

        setTitle("Current Behaviors");
        jScrollPane1.getViewport().add(jTable1);
    }

}
