package de.petri.onu.client.gui;

import java.awt.Point;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GUI extends JFrame {

	//panel
	private JPanel contentPane;
	public JPanel contentPanel;

	//menus
	Menu menu;
	Lobby lobby;

	//MoseDrag
	private Point initialClick;

	//Settings
	public static String fontName = "Calibri";
	public static int fontType = Font.BOLD;
	public static int fontSizeNorm = 18;
	public static int fontSizeBig = 55;

	public GUI() {
		setUndecorated(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBackground(Color.LIGHT_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JPanel TopBar = new JPanel();
		TopBar.setBounds(0, 0, 800, 50);
		TopBar.addMouseListener(new MouseAdapter() {
	        public void mousePressed(MouseEvent e) {
	            initialClick = e.getPoint();
	            getComponentAt(initialClick);
	        }
	    });

	    TopBar.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
	        public void mouseDragged(MouseEvent e) {

	            // get location of Window
	            int thisX = getLocation().x;
	            int thisY = getLocation().y;

	            // Determine how much the mouse moved since the initial click
	            int xMoved = e.getX() - initialClick.x;
	            int yMoved = e.getY() - initialClick.y;

	            // Move window to this position
	            int X = thisX + xMoved;
	            int Y = thisY + yMoved;
	            setLocation(X, Y);
	        }
	    });
		contentPane.setLayout(null);
		
		
		TopBar.setBackground(Color.DARK_GRAY);
		contentPane.add(TopBar);
		TopBar.setLayout(null);
		
		JButton btnMin = new JButton("-");
		btnMin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setState(ICONIFIED);
			}
		});
		btnMin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				btnMin.setOpaque(true);
				btnMin.setContentAreaFilled(true);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				btnMin.setOpaque(false);
				btnMin.setContentAreaFilled(false);
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}
		});
		btnMin.setBounds(690, 0, 50, 50);
		TopBar.add(btnMin);
		btnMin.setOpaque(false);
		btnMin.setForeground(Color.WHITE);
		btnMin.setFont(new Font("Calibri", Font.BOLD, 55));
		btnMin.setFocusPainted(false);
		btnMin.setContentAreaFilled(false);
		btnMin.setBorderPainted(false);
		btnMin.setBorder(null);
		btnMin.setBackground(Color.GRAY);
		
		JButton btnClose = new JButton("X");
		btnClose.setBounds(750, 0, 50, 50);
		TopBar.add(btnClose);
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnClose.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				btnClose.setOpaque(true);
				btnClose.setContentAreaFilled(true);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				btnClose.setOpaque(false);
				btnClose.setContentAreaFilled(false);
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}
		});
		btnClose.setOpaque(false);
		btnClose.setForeground(Color.WHITE);
		btnClose.setFont(new Font("Arial Black", Font.BOLD, 24));
		btnClose.setFocusPainted(false);
		btnClose.setContentAreaFilled(false);
		btnClose.setBorderPainted(false);
		btnClose.setBorder(null);
		btnClose.setBackground(Color.RED);
		
		contentPanel = new JPanel();
		contentPanel.setBackground(Color.LIGHT_GRAY);
		contentPanel.setBounds(0, 50, 800, 550);
		contentPanel.setLayout(null);
		
		menu = new Menu(this);
		menu.setForeground(new Color(255, 255, 255));
		menu.setBounds(0, 0, 800, 550);
		contentPanel.add(menu);
		contentPane.add(contentPanel);
		
	}
	
}
