package model;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

public class TimeImageRenderer extends JPanel implements ListCellRenderer<TimeImagePanel> {
    private JLabel timeLabel;
    private JLabel imageLabel;
    private JLabel messageLabel;
    private JLabel nameClientLabel;
    private JPanel temp;

    public TimeImageRenderer() {
        setLayout(new BorderLayout());

        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Times New Roman", Font.ITALIC, 12));
        timeLabel.setBackground(null);
        imageLabel = new JLabel();
        nameClientLabel = new JLabel();
        messageLabel = new JLabel();
        messageLabel.setFont(new Font("Times New Roman", Font.BOLD, 23));
        messageLabel.setHorizontalAlignment(SwingConstants.LEADING);

        temp = new JPanel(new BorderLayout());
        temp.setBackground(null);
        
        add(nameClientLabel, BorderLayout.NORTH);
        add(messageLabel, BorderLayout.CENTER);
        add(temp, BorderLayout.SOUTH);
        temp.add(imageLabel, BorderLayout.CENTER);
        temp.add(timeLabel, BorderLayout.SOUTH);
    }

    public Component getListCellRendererComponent(JList<? extends TimeImagePanel> list, TimeImagePanel value, int index, boolean isSelected, boolean cellHasFocus) {
    	imageLabel.setIcon(value.getImage());
        timeLabel.setText(value.getTime());
        messageLabel.setText(value.getMessage());
        nameClientLabel.setText(value.getNameClient());
        if (isSelected) {
//            setBackground(list.getSelectionBackground());
//            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        return this;
    }
}