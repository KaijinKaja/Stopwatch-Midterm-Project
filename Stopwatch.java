import javax.swing.*;
import java.awt.*;

public class Stopwatch extends JFrame {

    // Time variables
    private int milliseconds = 0;
    private int seconds = 0;
    private int minutes = 0;

    // GUI components
    private JLabel timeLabel;
    private JButton startBtn, stopBtn, resetBtn, lapBtn;

    // Lap logger components
    private DefaultListModel<String> lapModel;
    private JList<String> lapList;

    // Swing timer for updating time
    private Timer timer;

    // Tracks whether the stopwatch is running
    private boolean running = false;

    // Constructor that sets up the GUI
    public Stopwatch() {

        setTitle("Stopwatch");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Digital time display
        timeLabel = new JLabel("00:00:00", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 60));
        timeLabel.setForeground(new Color(0, 150, 0));
        timeLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        timeLabel.setPreferredSize(new Dimension(400, 120));

        // Buttons
        startBtn = new JButton("Start");
        stopBtn = new JButton("Stop");
        resetBtn = new JButton("Reset");
        lapBtn = new JButton("Lap");

        Color buttonBlue = new Color(0, 102, 204);
        JButton[] buttons = {startBtn, stopBtn, resetBtn, lapBtn};

        for (JButton btn : buttons) {
            btn.setBackground(buttonBlue);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setFont(new Font("Arial", Font.BOLD, 14));
            btn.setPreferredSize(new Dimension(90, 35));
            btn.setOpaque(true);
            btn.setBorderPainted(false);
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.add(startBtn);
        buttonPanel.add(stopBtn);
        buttonPanel.add(resetBtn);
        buttonPanel.add(lapBtn);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(timeLabel, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Lap logger
        lapModel = new DefaultListModel<>();
        lapList = new JList<>(lapModel);

        lapList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                label.setForeground(new Color(0, 150, 0));
                label.setFont(new Font("Arial", Font.BOLD, 14));
                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(lapList);
        scrollPane.setPreferredSize(new Dimension(250, 300));
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lap"));

        add(centerPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.EAST);

        // Timer updates every 10 milliseconds
        timer = new Timer(10, e -> {
            milliseconds += 10;

            if (milliseconds == 1000) {
                milliseconds = 0;
                seconds++;
            }

            if (seconds == 60) {
                seconds = 0;
                minutes++;
            }

            updateTimeLabel();
        });

        // Start button action
        startBtn.addActionListener(e -> {
            if (!running) {
                timer.start();
                running = true;
            }
        });

        // Stop button action
        stopBtn.addActionListener(e -> {
            timer.stop();
            running = false;
        });

        // Reset button action
        resetBtn.addActionListener(e -> {
            timer.stop();
            running = false;
            milliseconds = seconds = minutes = 0;
            lapModel.clear();
            updateTimeLabel();
        });

        // Lap button action
        lapBtn.addActionListener(e -> {
            if (running) {
                lapModel.addElement("Time: " + formatTime());
            }
        });
    }

    // Updates the stopwatch display
    private void updateTimeLabel() {
        timeLabel.setText(formatTime());
    }

    // Formats time as MM:SS:MS
    private String formatTime() {
        return String.format("%02d:%02d:%02d",
                minutes, seconds, milliseconds / 10);
    }

    // Main method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Stopwatch().setVisible(true);
        });
    }
}