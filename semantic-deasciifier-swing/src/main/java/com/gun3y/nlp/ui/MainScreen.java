package com.gun3y.nlp.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import com.gun3y.nlp.deasciifier.SemanticDeasciifier;
import com.gun3y.nlp.helper.DeasciifierHelper;

public class MainScreen extends JFrame {

    private static final long serialVersionUID = -3534220386107226774L;

    private JPanel contentPane;

    private SemanticDeasciifier semanticDeasciifier = new SemanticDeasciifier();

    JTextArea txtAsciifiedTurkish = new JTextArea();

    JTextArea txtDeasciifiedTurkish = new JTextArea();

    JTextArea txtTurkishInput = new JTextArea();

    public static DefaultListModel<String> LOG_MODEL = new DefaultListModel<String>();

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
		    MainScreen frame = new MainScreen();
		    frame.setVisible(true);
		}
		catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    private void deasciify() {
	LOG_MODEL.clear();
	txtAsciifiedTurkish.setEditable(false);
	txtAsciifiedTurkish.setText(DeasciifierHelper.asciify(txtTurkishInput.getText()));
	String text = txtAsciifiedTurkish.getText();
	StringBuilder builder = new StringBuilder();
	String[] sentences = text.split("\n");
	for (String str : sentences) {
	    builder.append(semanticDeasciifier.deasciify(str)).append("\n");
	}
	txtDeasciifiedTurkish.setText(builder.toString());
	txtAsciifiedTurkish.setEditable(true);
    }

    /**
     * Create the frame.
     */
    public MainScreen() {
	setTitle("Semantic Deasciifier");
	setResizable(false);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setBounds(100, 100, 779, 692);
	contentPane = new JPanel();
	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	setContentPane(contentPane);
	contentPane.setLayout(null);

	JLabel lblTurkishInput = new JLabel("T\u00FCrk\u00E7e Input:");
	lblTurkishInput.setBounds(10, 11, 76, 14);
	contentPane.add(lblTurkishInput);

	JScrollPane turkishInputScrollPane = new JScrollPane();
	turkishInputScrollPane.setBounds(10, 30, 750, 64);
	contentPane.add(turkishInputScrollPane);
	txtTurkishInput.setFont(new Font("Cambria Math", Font.PLAIN, 15));

	txtTurkishInput.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyTyped(KeyEvent e) {
		if (' ' == e.getKeyChar()) {
		    txtAsciifiedTurkish.setText(DeasciifierHelper.asciify(txtTurkishInput.getText()).trim());
		}
		else if ('\n' == e.getKeyChar()) {
		    deasciify();
		}

	    }
	});
	turkishInputScrollPane.setViewportView(txtTurkishInput);

	JLabel lblAsciifiedInput = new JLabel("Asciify Edilmi\u015F Hali:");
	lblAsciifiedInput.setBounds(10, 100, 138, 14);
	contentPane.add(lblAsciifiedInput);

	JScrollPane asciifiedScrollPane = new JScrollPane();
	asciifiedScrollPane.setBounds(10, 125, 750, 64);
	contentPane.add(asciifiedScrollPane);
	txtAsciifiedTurkish.setFont(new Font("Cambria Math", Font.PLAIN, 15));

	asciifiedScrollPane.setViewportView(txtAsciifiedTurkish);

	JLabel lblDeasciifiedTurkish = new JLabel("Deasciify Edilmi\u015F Hali:");
	lblDeasciifiedTurkish.setBounds(10, 200, 138, 14);
	contentPane.add(lblDeasciifiedTurkish);

	JScrollPane deasciifiedTurkishScrollPane = new JScrollPane();
	deasciifiedTurkishScrollPane.setBounds(10, 225, 750, 64);
	contentPane.add(deasciifiedTurkishScrollPane);
	txtDeasciifiedTurkish.setFont(new Font("Cambria Math", Font.PLAIN, 15));

	deasciifiedTurkishScrollPane.setViewportView(txtDeasciifiedTurkish);

	JButton btnOpenDbPanel = new JButton("DB Panel");
	btnOpenDbPanel.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		MongoPanel frame = new MongoPanel();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    }
	});
	btnOpenDbPanel.setBounds(671, 300, 89, 23);
	contentPane.add(btnOpenDbPanel);

	JButton btnDeasciify = new JButton("\u00C7evir");
	btnDeasciify.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		deasciify();
	    }
	});
	btnDeasciify.setBounds(572, 300, 89, 23);
	contentPane.add(btnDeasciify);

	JScrollPane logScrollPane = new JScrollPane();
	logScrollPane.setBounds(10, 347, 750, 285);
	contentPane.add(logScrollPane);

	JList<String> listLog = new JList<String>();
	listLog.setModel(LOG_MODEL);
	// listLog.setEnabled(false);
	LogListCell logListCell = new LogListCell();
	listLog.setCellRenderer(logListCell);
	logScrollPane.setViewportView(listLog);
    }

    private class LogListCell extends DefaultListCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4844772771743415340L;

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
	    Component listCellRendererComponent = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

	    if (value.toString().contains("Bulunamadı")) {
		setBackground(Color.RED);
	    }
	    if (value.toString().contains("Bulundu")) {
		setBackground(Color.GREEN);
	    }

	    return listCellRendererComponent;
	}
    }
}
