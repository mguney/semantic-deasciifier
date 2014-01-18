package com.gun3y.nlp.ui;

import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.gun3y.nlp.model.CountModel;
import com.gun3y.nlp.mongo.MongoManager;

public class MongoPanel extends JFrame {

    private static final long serialVersionUID = -4331207540971756076L;
    private JPanel contentPane;
    private JTextField txtQuery;
    private JScrollPane scrollPane;
    private JTable table;

    private MongoManager mongoManager = MongoManager.getInstance();

    private DefaultTableModel tableModel = new DefaultTableModel(new String[] { "Key", "Key", "Key", "Props" }, 0);

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
	try {
	    // Set System L&F
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	}
	catch (UnsupportedLookAndFeelException e) {
	    // handle exception
	}
	catch (ClassNotFoundException e) {
	    // handle exception
	}
	catch (InstantiationException e) {
	    // handle exception
	}
	catch (IllegalAccessException e) {
	    // handle exception
	}
	EventQueue.invokeLater(new Runnable() {
	    public void run() {
		try {
		    MongoPanel frame = new MongoPanel();
		    frame.setVisible(true);
		}
		catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    /**
     * Create the frame.
     */
    public MongoPanel() {
	setResizable(false);
	setTitle("DB Panel");
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setBounds(100, 100, 441, 449);
	contentPane = new JPanel();
	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	setContentPane(contentPane);
	contentPane.setLayout(null);

	JLabel lblQuery = new JLabel("Query:");
	lblQuery.setBounds(10, 11, 46, 14);
	contentPane.add(lblQuery);

	txtQuery = new JTextField();
	txtQuery.setBounds(10, 32, 414, 20);
	txtQuery.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyTyped(KeyEvent arg0) {
		if ('\n' == arg0.getKeyChar()) {
		    txtQuery.setEnabled(false);
		    List<CountModel> models = mongoManager.findCountModelById(txtQuery.getText());
		    tableModel.getDataVector().clear();
		    if (models != null) {
			for (CountModel model : models) {
			    String[] row = new String[] { "", "", "", "" };
			    row[3] = model.getCount() + "";
			    // row[3] = String.format("%.20f",
			    // model.getCount());

			    String id = model.getId();
			    String[] ids = id.split("\\$");
			    for (int i = 0; i < ids.length; i++) {
				row[i] = ids[i];
			    }
			    tableModel.addRow(row);
			}
		    }
		    txtQuery.setEnabled(true);
		}
	    }
	});
	contentPane.add(txtQuery);
	txtQuery.setColumns(10);

	scrollPane = new JScrollPane();
	scrollPane.setBounds(10, 63, 414, 345);
	contentPane.add(scrollPane);

	table = new JTable();
	table.setModel(tableModel);
	scrollPane.setViewportView(table);
    }
}
