import javax.swing.*;
import java.awt.*;

/**
 * A Java Swing Stopwatch application with Lap recording.
 * Extends JFrame to create the main window.
 */
public class Stopwatch extends JFrame {

    // =========================
    // TIME TRACKING VARIABLES
    // =========================
    
    // nanoTime() is used for high-precision measurement, unaffected by system clock shifts.
    private long startTime;          
    private long elapsedTime = 0;    // Holds the sum of time from previous "Start-Stop" cycles
    private boolean running = false; // Flag to track if the timer logic is active

    // =========================
    // GUI COMPONENTS
    // =========================

    private JLabel timeLabel;
    private JButton startBtn, stopBtn, resetBtn, lapBtn;

    // =========================
    // LAP LOGGER COMPONENTS
    // =========================

    // DefaultListModel allows us to dynamically add/remove items from the JList
    private DefaultListModel<String> lapModel;
    private JList<String> lapList;

    // javax.swing.Timer fires ActionEvents at specified intervals (10ms here)
    // It runs on the Event Dispatch Thread, making it safe for UI updates.
    private Timer timer;

    private int lapCounter = 1;

    // =========================
    // COLOR THEME (LIGHT PINK)
    // =========================
    // Defining colors as constants makes it easier to maintain a consistent UI theme.
    private final Color BACKGROUND   = new Color(255, 228, 235); 
    private final Color PANEL_PINK   = new Color(255, 235, 240);
    private final Color LIGHT_PINK   = new Color(255, 182, 193);
    private final Color HOT_PINK     = new Color(255, 105, 180);
    private final Color NEON_PINK    = new Color(255, 20, 147);
    private final Color DARK_PINK    = new Color(199, 21, 133);

    // =========================
    // CONSTRUCTOR
    // =========================
    public Stopwatch() {

        // --- Window Configuration ---
        setTitle("Stopwatch");
        setSize(800, 400);
        // Ensures the application exits fully when the window is closed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Centers the window on the user's screen
        setLocationRelativeTo(null);
        // BorderLayout divides the container into North, South, East, West, and Center
        setLayout(new BorderLayout());

        getContentPane().setBackground(BACKGROUND);

        // =========================
        // TIME DISPLAY (CENTER)
        // =========================

        timeLabel = new JLabel("00:00:00", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 60));
        timeLabel.setForeground(NEON_PINK);
        // setOpaque(true) is required for the background color to show on a JLabel
        timeLabel.setOpaque(true);
        timeLabel.setBackground(PANEL_PINK);
        
        // CompoundBorder: Outer line border + Inner empty border for "padding"
        timeLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(HOT_PINK, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        timeLabel.setPreferredSize(new Dimension(400, 120));

        // =========================
        // BUTTONS (SOUTH)
        // =========================

        startBtn = new JButton("Start");
        stopBtn  = new JButton("Stop");
        resetBtn = new JButton("Reset");
        lapBtn   = new JButton("Lap");

        // Apply custom styling to all buttons via a helper method
        styleButton(startBtn);
        styleButton(stopBtn);
        styleButton(resetBtn);
        styleButton(lapBtn);

        // Initial state: User can't stop or lap a timer that hasn't started
        stopBtn.setEnabled(false);
        lapBtn.setEnabled(false);

        // FlowLayout keeps buttons in a row with specific spacing (15px horizontal, 10px vertical)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(BACKGROUND);
        buttonPanel.add(startBtn);
        buttonPanel.add(stopBtn);
        buttonPanel.add(resetBtn);
        buttonPanel.add(lapBtn);

        // Nesting panels: centerPanel holds the clock and the buttons together
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(BACKGROUND);
        centerPanel.add(timeLabel, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        // =========================
        // LAP LOGGER (EAST)
        // =========================

        lapModel = new DefaultListModel<>();
        lapList = new JList<>(lapModel);
        lapList.setBackground(PANEL_PINK);
        lapList.setSelectionBackground(HOT_PINK);
        lapList.setSelectionForeground(Color.WHITE);

        // Custom CellRenderer to control how each lap entry looks in the list
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

        // JScrollPane allows the lap list to be scrollable if many laps are added
        JScrollPane scrollPane = new JScrollPane(lapList);
        scrollPane.setPreferredSize(new Dimension(250, 300));
        scrollPane.getViewport().setBackground(PANEL_PINK);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(HOT_PINK),
                "Lap Records",
                0, 0, new Font("Arial", Font.BOLD, 12), NEON_PINK
        ));

        // Add main panels to the JFrame's BorderLayout
        add(centerPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.EAST);

        // =========================
        // TIMER LOGIC
        // =========================

        // This timer triggers every 10 milliseconds to refresh the UI text
        timer = new Timer(10, e -> updateDisplay());

        // =========================
        // BUTTON ACTIONS (LAMBDAS)
        // =========================

        // START ACTION
        startBtn.addActionListener(e -> {
            if (!running) {
                // Record the exact moment the button was clicked
                startTime = System.nanoTime();
                timer.start();
                running = true;

                // UI Feedback
                startBtn.setBackground(NEON_PINK);
                startBtn.setForeground(Color.WHITE);
                stopBtn.setEnabled(true);
                lapBtn.setEnabled(true);
                startBtn.setEnabled(false);
            }
        });

        // STOP ACTION
        stopBtn.addActionListener(e -> {
            if (running) {
                // Add the time segment just finished to the total elapsed time
                elapsedTime += System.nanoTime() - startTime;
                timer.stop();
                running = false;

                // UI Feedback
                stopBtn.setBackground(DARK_PINK);
                stopBtn.setForeground(Color.WHITE);
                startBtn.setEnabled(true);
                stopBtn.setEnabled(false);
            }
        });

        // RESET ACTION
        resetBtn.addActionListener(e -> {
            timer.stop();
            running = false;
            elapsedTime = 0;
            lapCounter = 1;
            lapModel.clear(); // Empty the lap list

            updateDisplay(); // Reset text to 00:00:00

            // Revert button colors to original state
            styleButton(startBtn);
            styleButton(stopBtn);
            startBtn.setEnabled(true);
            stopBtn.setEnabled(false);
            lapBtn.setEnabled(false);
        });

        // LAP ACTION
        lapBtn.addActionListener(e -> {
            if (running) {
                // Add current time string to the list model
                lapModel.addElement("Lap " + lapCounter++ + " - " + formatTime());
                // Auto-scroll to the bottom of the list
                lapList.ensureIndexIsVisible(lapModel.size() - 1);
            }
        });
    }

    // =========================
    // HELPER METHODS
    // =========================

    /**
     * Standardizes button appearance to avoid repetitive code.
     */
    private void styleButton(JButton btn) {
        btn.setBackground(LIGHT_PINK);
        btn.setForeground(NEON_PINK);
        btn.setFocusPainted(false); // Removes the thin focus square around text
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(90, 35));
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createLineBorder(HOT_PINK, 2));
    }

    /**
     * Calculates the current total time and updates the Label text.
     */
    private void updateDisplay() {
        long totalElapsed;
        if (running) {
            // If running: show (past cycles + current cycle)
            totalElapsed = elapsedTime + (System.nanoTime() - startTime);
        } else {
            // If paused: show only past cycles
            totalElapsed = elapsedTime;
        }
        timeLabel.setText(formatTime(totalElapsed));
    }

    /**
     * Overloaded helper to format the current running time string.
     */
    private String formatTime() {
        return formatTime(elapsedTime + (System.nanoTime() - startTime));
    }

    /**
     * Converts nanoseconds into a readable MM:SS:CC format.
     * CC = Centiseconds (1/100th of a second)
     */
    private String formatTime(long nanoTime) {
        long totalMilliseconds = nanoTime / 1_000_000;

        long minutes = totalMilliseconds / 60000;
        long seconds = (totalMilliseconds / 1000) % 60;
        // Using 10 here gives us centiseconds (0-99)
        long centiseconds = (totalMilliseconds / 10) % 100;

        // %02d ensures two digits with leading zeros (e.g., "05" instead of "5")
        return String.format("%02d:%02d:%02d", minutes, seconds, centiseconds);
    }

    // =========================
    // MAIN ENTRY POINT
    // =========================
    public static void main(String[] args) {
        // Swing is not thread-safe. invokeLater ensures the UI 
        // is created on the Event Dispatch Thread (EDT).
        SwingUtilities.invokeLater(() -> {
            new Stopwatch().setVisible(true);
        });
    }
}
