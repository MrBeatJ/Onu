package gui;

import javax.swing.JPanel;
import java.awt.Rectangle;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;

public class LobbyUser extends JPanel {

	/**
	 * Create the panel.
	 */
	public LobbyUser() {
		setBackground(new Color(255, 153, 102));
		setBounds(new Rectangle(0, 0, 300, 30));
		setLayout(null);
		
		JLabel lblName = new JLabel("Name");
		lblName.setHorizontalAlignment(SwingConstants.LEFT);
		lblName.setBounds(10, 0, 168, 30);
		lblName.setFont(new Font("Calibri", Font.BOLD, 16));
		add(lblName);
		
		JLabel lblPing = new JLabel("100ms");
		lblPing.setHorizontalAlignment(SwingConstants.LEFT);
		lblPing.setFont(new Font("Calibri", Font.BOLD, 16));
		lblPing.setBounds(210, 0, 80, 30);
		add(lblPing);

	}

}
