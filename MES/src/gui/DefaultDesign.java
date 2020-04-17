package gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class DefaultDesign {

	public DefaultDesign() {
		// TODO Auto-generated constructor stub
	}
	public static void buttons(JButton button, String logo) {

		button.setFont(new Font("Calibri", Font.BOLD, 15));
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setBackground(new Color(0xededed));
		button.setForeground(Color.BLACK);
		button.setUI(new StyledButtonUI());
		button.setFocusable(false);
		button.setHorizontalAlignment(SwingConstants.LEFT);
		button.setIconTextGap(25);
		button.setIcon(new ImageIcon(Gui.class.getResource(logo)));

	}
	public static void buttons(JButton button) {
		button.setFont(new Font("Calibri", Font.BOLD, 15));
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setBackground(new Color(0xededed));
		button.setForeground(Color.BLACK);
		button.setUI(new StyledButtonUI());
		button.setFocusable(false);
		button.setHorizontalAlignment(SwingConstants.CENTER);
		button.setHorizontalTextPosition(SwingConstants.CENTER);
	}
	public static void styleLabel(JLabel label) {
		label.setFont(new Font("hP Simplified", Font.BOLD, 14));
		Color colorText = Color.decode("#030349");
		label.setForeground(colorText);
	}

	public static void styleLabelData(JLabel label) {
		label.setFont(new Font("Calibri", Font.BOLD, 16));
		Color colorText = Color.decode("#364f6b");
		label.setForeground(colorText);
	}
}
