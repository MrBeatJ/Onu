package de.petri.onu.client.gui;

import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import java.awt.Cursor;
import javax.swing.border.LineBorder;

public class Menu extends JPanel {

	GUI gui;
	
	Menu itsMe;
	
	public Menu(GUI gui) {
		this.gui = gui;
		
		itsMe = this;
		
		setBackground(new Color(255, 255, 102));
		setBounds(0, 0, 800, 550);
		
		JButton btnLobby = new JButton("Lobby");
		btnLobby.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnLobby.setHorizontalTextPosition(SwingConstants.CENTER);
		btnLobby.setFont(new Font("Calibri", Font.BOLD, 20));
		btnLobby.setFocusTraversalKeysEnabled(false);
		btnLobby.setFocusPainted(false);
		btnLobby.setBackground(Color.GRAY);
		btnLobby.setBorderPainted(false);
		btnLobby.setBorder(null);
		btnLobby.setFocusable(false);
		btnLobby.setBounds(250, 252, 300, 50);
		btnLobby.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		setLayout(null);
		add(btnLobby);
		
		JButton btnSettings = new JButton("Einstellungen");
		btnSettings.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnSettings.setActionCommand("");
		btnSettings.setEnabled(false);
		btnSettings.setHorizontalTextPosition(SwingConstants.CENTER);
		btnSettings.setFont(new Font("Calibri", Font.BOLD, 20));
		btnSettings.setFocusable(false);
		btnSettings.setFocusTraversalKeysEnabled(false);
		btnSettings.setFocusPainted(false);
		btnSettings.setBorderPainted(false);
		btnSettings.setBorder(null);
		btnSettings.setBackground(Color.GRAY);
		btnSettings.setBounds(250, 342, 300, 50);
		add(btnSettings);
		
		JButton btnQuit = new JButton("Beenden");
		btnQuit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(1);
			}
		});
		btnQuit.setHorizontalTextPosition(SwingConstants.CENTER);
		btnQuit.setFont(new Font("Calibri", Font.BOLD, 20));
		btnQuit.setFocusable(false);
		btnQuit.setFocusTraversalKeysEnabled(false);
		btnQuit.setFocusPainted(false);
		btnQuit.setBorderPainted(false);
		btnQuit.setBorder(null);
		btnQuit.setBackground(Color.GRAY);
		btnQuit.setBounds(250, 432, 300, 50);
		add(btnQuit);
		
		JButton btnJoin = new JButton("Beitreten");
		btnJoin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gui.contentPanel.setVisible(false);
				gui.contentPanel.removeAll();
				gui.contentPanel.add(new Lobby(gui));
				gui.contentPanel.setVisible(true);
			}
		});
		btnJoin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnJoin.setHorizontalTextPosition(SwingConstants.CENTER);
		btnJoin.setFont(new Font("Calibri", Font.BOLD, 20));
		btnJoin.setFocusable(false);
		btnJoin.setFocusTraversalKeysEnabled(false);
		btnJoin.setFocusPainted(false);
		btnJoin.setBorderPainted(false);
		btnJoin.setBorder(null);
		btnJoin.setBackground(Color.GRAY);
		btnJoin.setBounds(250, 162, 300, 50);
		add(btnJoin);
		
		JLabel lblOnu = new JLabel("Onu");
		lblOnu.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		lblOnu.setBorder(new LineBorder(new Color(0, 0, 0), 3, true));
		lblOnu.setForeground(new Color(204, 0, 0));
		lblOnu.setFont(new Font("Tahoma", Font.BOLD, 40));
		lblOnu.setHorizontalAlignment(SwingConstants.CENTER);
		lblOnu.setBounds(250, 27, 300, 70);
		add(lblOnu);

	}
}
