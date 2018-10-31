package gui;

import javax.swing.JPanel;
import javax.swing.JToolBar;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Component;
import javax.swing.Box;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import javax.swing.JButton;

import net.miginfocom.swing.MigLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Cursor;
import javax.swing.border.LineBorder;

public class Lobby extends JPanel {

	GUI gui;
	
	public Lobby(GUI gui) {
		this.gui = gui;
		
		setBackground(new Color(255, 255, 102));
		setBounds(0, 0, 800, 550);
		setLayout(null);
		
		JButton btnStart = new JButton("Start");
		btnStart.setToolTipText("Nur der Server Admin kann das Spiel Starten!");
		btnStart.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnStart.setActionCommand("");
		btnStart.setEnabled(false);
		btnStart.setHorizontalTextPosition(SwingConstants.CENTER);
		btnStart.setFont(new Font("Calibri", Font.BOLD, 20));
		btnStart.setFocusable(false);
		btnStart.setFocusTraversalKeysEnabled(false);
		btnStart.setFocusPainted(false);
		btnStart.setBorderPainted(false);
		btnStart.setBorder(null);
		btnStart.setBackground(Color.GRAY);
		btnStart.setBounds(432, 464, 300, 50);
		add(btnStart);
		
		JButton btnLeave = new JButton("Verlassen");
		btnLeave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gui.contentPanel.setVisible(false);
				gui.contentPanel.removeAll();
				gui.contentPanel.add(gui.menu);
				gui.contentPanel.setVisible(true);
			}
		});
		btnLeave.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnLeave.setHorizontalTextPosition(SwingConstants.CENTER);
		btnLeave.setFont(new Font("Calibri", Font.BOLD, 20));
		btnLeave.setFocusable(false);
		btnLeave.setFocusTraversalKeysEnabled(false);
		btnLeave.setFocusPainted(false);
		btnLeave.setBorderPainted(false);
		btnLeave.setBorder(null);
		btnLeave.setBackground(Color.GRAY);
		btnLeave.setBounds(66, 464, 300, 50);
		add(btnLeave);
		
		JLabel lblOnu = new JLabel("Lobby");
		lblOnu.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		lblOnu.setBorder(new LineBorder(new Color(0, 0, 0), 3, true));
		lblOnu.setForeground(new Color(204, 0, 0));
		lblOnu.setFont(new Font("Tahoma", Font.BOLD, 40));
		lblOnu.setHorizontalAlignment(SwingConstants.CENTER);
		lblOnu.setBounds(250, 27, 300, 70);
		add(lblOnu);
		
		JLabel lblVon = new JLabel("2 von 8");
		lblVon.setFont(new Font("Calibri", Font.BOLD, 16));
		lblVon.setHorizontalAlignment(SwingConstants.CENTER);
		lblVon.setBounds(123, 135, 186, 24);
		add(lblVon);
		
		LobbyUser lblPlayer1 = new LobbyUser();
		lblPlayer1.setLocation(66, 170);
		add(lblPlayer1);
		
		LobbyUser lobbyUser = new LobbyUser();
		lobbyUser.setBounds(66, 200, 300, 30);
		add(lobbyUser);

	}
}
