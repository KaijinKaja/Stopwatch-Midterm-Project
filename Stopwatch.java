import javax.swing.*;
import java.awt.*;

public class Stopwatch extends JFrame {

    // =========================
    // TIME TRACKING VARIABLES
    // =========================

    private long startTime;          // Time when stopwatch starts/resumes
    private long elapsedTime = 0;    // Accumulated paused time
    private boolean running = false; // Running state

    // =========================
    // GUI COMPONENTS
    // =========================

    private JLabel timeLabel;
    private JButton startBtn, stopBtn, resetBtn, lapBtn;

    // =========================
    // LAP LOGGER COMPONENTS
    // =========================

    private DefaultListModel<String> lapModel;
    private JList<String> lapList;

    // Timer updates display only
    private Timer timer;

    private int lapCounter = 1;

    // =========================
    // COLOR THEME (LIGHT PINK)
    // =========================

    private final Color BACKGROUND   = new Color(255, 228, 235); // main light pink
    private final Color PANEL_PINK   = new Color(255, 235, 240);
    private final Color LIGHT_PINK   = new Color(255, 182, 193);
    private final Color HOT_PINK     = new Color(255, 105, 180);
    private final Color NEON_PINK    = new Color(255, 20, 147);
    private final Color DARK_PINK    = new Color(199, 21, 133);

    // =========================
    // CONSTRUCTOR
    // =========================
    public Stopwatch() {

        // JFrame setup
        setTitle("Stopwatch");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // IMPORTANT: set content pane color
        getContentPane().setBackground(BACKGROUND);

        // =========================
        // TIME DISPLAY
        // =========================

        timeLabel = new JLabel("00:00:00", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 60));
        timeLabel.setForeground(NEON_PINK);
        timeLabel.setOpaque(true);
        timeLabel.setBackground(PANEL_PINK);
        timeLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(HOT_PINK, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        timeLabel.setPreferredSize(new Dimension(400, 120));

        // =========================
        // BUTTONS
        // =========================

        startBtn = new JButton("Start");
        stopBtn  = new JButton("Stop");
        resetBtn = new JButton("Reset");
        lapBtn   = new JButton("Lap");

        styleButton(startBtn);
        styleButton(stopBtn);
        styleButton(resetBtn);
        styleButton(lapBtn);

        stopBtn.setEnabled(false);
        lapBtn.setEnabled(false);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(BACKGROUND);
        buttonPanel.add(startBtn);
        buttonPanel.add(stopBtn);
        buttonPanel.add(resetBtn);
        buttonPanel.add(lapBtn);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(BACKGROUND);
        centerPanel.add(timeLabel, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        // =========================
        // LAP LOGGER
        // =========================

        lapModel = new DefaultListModel<>();
        lapList = new JList<>(lapModel);
        lapList.setBackground(PANEL_PINK);
        lapList.setSelectionBackground(HOT_PINK);
        lapList.setSelectionForeground(Color.WHITE);

        lapList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                label.setForeground(NEON_PINK);
                label.setFont(new Font("Arial", Font.BOLD, 14));
                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(lapList);
        scrollPane.setPreferredSize(new Dimension(250, 300));
        scrollPane.getViewport().setBackground(PANEL_PINK);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(HOT_PINK),
                "Lap Records",
                0,
                0,
                new Font("Arial", Font.BOLD, 12),
                NEON_PINK
        ));

        add(centerPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.EAST);

        // =========================
        // TIMER
        // =========================

        timer = new Timer(10, e -> updateDisplay());

        // =========================
        // BUTTON ACTIONS
        // =========================

        // START
        startBtn.addActionListener(e -> {
            if (!running) {
                startTime = System.nanoTime();
                timer.start();
                running = true;

                startBtn.setBackground(NEON_PINK);
                startBtn.setForeground(Color.WHITE);

                stopBtn.setEnabled(true);
                lapBtn.setEnabled(true);
                startBtn.setEnabled(false);
            }
        });

        // STOP
        stopBtn.addActionListener(e -> {
            if (running) {
                elapsedTime += System.nanoTime() - startTime;
                timer.stop();
                running = false;

                stopBtn.setBackground(DARK_PINK);
                stopBtn.setForeground(Color.WHITE);

                startBtn.setEnabled(true);
                stopBtn.setEnabled(false);
            }
        });

        // RESET
        resetBtn.addActionListener(e -> {

            timer.stop();
            running = false;

            elapsedTime = 0;
            lapCounter = 1;
            lapModel.clear();

            updateDisplay();

            styleButton(startBtn);
            styleButton(stopBtn);

            startBtn.setEnabled(true);
            stopBtn.setEnabled(false);
            lapBtn.setEnabled(false);
        });

        // LAP
        lapBtn.addActionListener(e -> {
            if (running) {
                lapModel.addElement("Lap " + lapCounter++ + " - " + formatTime());
                lapList.ensureIndexIsVisible(lapModel.size() - 1);
            }
        });
    }

    // =========================
    // BUTTON STYLE METHOD
    // =========================

    private void styleButton(JButton btn) {
        btn.setBackground(LIGHT_PINK);
        btn.setForeground(NEON_PINK);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(90, 35));
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createLineBorder(HOT_PINK, 2));
    }

    // =========================
    // DISPLAY UPDATE
    // =========================

    private void updateDisplay() {

        long totalElapsed;

        if (running) {
            totalElapsed = elapsedTime + (System.nanoTime() - startTime);
        } else {
            totalElapsed = elapsedTime;
        }

        timeLabel.setText(formatTime(totalElapsed));
    }

    // =========================
    // TIME FORMATTING
    // =========================

    private String formatTime() {
        return formatTime(elapsedTime + (System.nanoTime() - startTime));
    }

    private String formatTime(long nanoTime) {

        long totalMilliseconds = nanoTime / 1_000_000;

        long minutes = totalMilliseconds / 60000;
        long seconds = (totalMilliseconds / 1000) % 60;
        long centiseconds = (totalMilliseconds / 10) % 100;

        return String.format("%02d:%02d:%02d",
                minutes, seconds, centiseconds);
    }

    // =========================
    // MAIN METHOD
    // =========================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Stopwatch().setVisible(true);
        });
    }
}
