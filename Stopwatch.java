import javax.swing.*; // javax import for style and customization (Design/JFrame)
import java.awt.*; // java import for time computation and logic (Uses System.nanoTime())

public class StopwatchExplanation extends JFrame {

    // TIME TRACKING VARIABLES
    private long startTime; //Stores the exact time (in nanoseconds) when the stopwatch. Used for started time or resumption.
    private long elapsedTime = 0; //Stores the total accumulated time after starting the stopwatch
    private boolean running = false; // Determines whether the stopwatch is running or active (false means not running and true means is running

    // GUI COMPONENTS
    private JLabel timeLabel; //Stopwatch Template
    private JButton startBtn, stopBtn, resetBtn, lapBtn; //Buttons

    // LAP LOGGER COMPONENTS
    private DefaultListModel<String> lapModel; //dynamically stores lap strings.
    private JList<String> lapList; //displays the recorded lap entries.
    private Timer timer; // Fires an event every 10 milliseconds to update the display.Runs safely on the Event Dispatch Thread (EDT).
    private int lapCounter = 1;

    // COLOR THEME CONSTANTS FOR STOPWATCH DESIGN
    private final Color BACKGROUND   = new Color(255, 228, 235);
    private final Color PANEL_PINK   = new Color(255, 235, 240);
    private final Color LIGHT_PINK   = new Color(255, 182, 193);
    private final Color HOT_PINK     = new Color(255, 105, 180);
    private final Color NEON_PINK    = new Color(255, 20, 147);
    private final Color DARK_PINK    = new Color(199, 21, 133);

    // CONSTRUCTOR
    public StopwatchExplanation() {

        //JFrame configuration: Sets title, size, close behavior, and position
        setTitle("Stopwatch");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND);

        // TIME DISPLAY DESIGN
        timeLabel = new JLabel("00:00:00", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 60));
        timeLabel.setForeground(NEON_PINK);
        timeLabel.setOpaque(true);
        timeLabel.setBackground(PANEL_PINK);
        timeLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(HOT_PINK, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10) // CompoundBorder adds a visible outline and inner spacing (padding)
        ));

        // STOPWATCH BUTTONS
        startBtn = new JButton("Start");
        stopBtn  = new JButton("Stop");
        resetBtn = new JButton("Reset");
        lapBtn   = new JButton("Lap");
        styleButton(startBtn); //Applies uniform styling to all buttons.
        styleButton(stopBtn);
        styleButton(resetBtn);
        styleButton(lapBtn);

        stopBtn.setEnabled(false);// Prevent invalid actions on startup (Unactivated Button)
        lapBtn.setEnabled(false);// Prevent invalid actions on startup (Unactivated Button)

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10)); //Adding the Buttons to the Stopwatch Layout
        buttonPanel.setBackground(BACKGROUND);
        buttonPanel.add(startBtn);
        buttonPanel.add(stopBtn);
        buttonPanel.add(resetBtn);
        buttonPanel.add(lapBtn);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(BACKGROUND);
        centerPanel.add(timeLabel, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        // LAP LOGGER DESIGN
        lapModel = new DefaultListModel<>();
        lapList = new JList<>(lapModel);
        lapList.setBackground(PANEL_PINK);
        lapList.setSelectionBackground(HOT_PINK);
        lapList.setSelectionForeground(Color.WHITE);
        lapList.setCellRenderer(new DefaultListCellRenderer() { // This custom renderer ensures consistent lap text style.
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

        JScrollPane scrollPane = new JScrollPane(lapList); // Lap Logger Components
        scrollPane.setPreferredSize(new Dimension(250, 300));
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(HOT_PINK),
                "Lap Records"
        ));

        add(centerPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.EAST);

        // TIMER LOGIC
        timer = new Timer(10, e -> updateDisplay()); // Timer updates the display every 10 milliseconds

        // 4 STOPWATCH BUTTONS
        startBtn.addActionListener(e -> { // START BUTTON
            if (!running) {
                startTime = System.nanoTime(); // Capture start moment
                timer.start(); // Calls Method
                running = true; // true = stopwatch has started

                startBtn.setBackground(NEON_PINK); //When button is pressed color will change
                startBtn.setForeground(Color.WHITE);

                startBtn.setEnabled(false); // Prevents Start Button from being prssed again
                stopBtn.setEnabled(true); // Allows Stop Button to be activated and used
                lapBtn.setEnabled(true); // Allows Lap Button to be activated and used
            }
        });

        stopBtn.addActionListener(e -> { // STOP BUTTON
            if (running) {
                elapsedTime += System.nanoTime() - startTime; // Store elapsed time so stopwatch so that it can resume correctly later
                timer.stop();
                running = false;

                stopBtn.setBackground(DARK_PINK); // Buttons color will change when pressed
                stopBtn.setForeground(Color.WHITE);

                startBtn.setEnabled(true); // Allows the Start Button to be activated and used
                stopBtn.setEnabled(false); // Prevents Stop Button from being pressed twice
            }
        });

        resetBtn.addActionListener(e -> { // RESET BUTTON
            timer.stop();        // Stop the timer completely
            running = false;     // Mark stopwatch as inactive
            elapsedTime = 0;     // Clear stored elapsed time
            lapCounter = 1;      // Reset lap numbering
            lapModel.clear();    // Remove all lap entries

            updateDisplay(); // Reset display text
            styleButton(startBtn); // Restore Buttons Default colors
            styleButton(stopBtn);

            startBtn.setEnabled(true); // Restore the Buttons to their default state
            stopBtn.setEnabled(false);
            lapBtn.setEnabled(false);
        });

        // LAP BUTTON
        lapBtn.addActionListener(e -> { //Records the current formatted time as a lap entry.
            if (running) {
                lapModel.addElement("Lap " + lapCounter++ + " - " + formatTime());
                lapList.ensureIndexIsVisible(lapModel.size() - 1);
            }
        });
    }
    // HELPER METHODS
    /*
     Applies default styling to buttons.
     COLOR LOGIC:
     LIGHT_PINK = inactive/default state
     NEON_PINK = readable themed text
     HOT_PINK border improves visibility
     */
    private void styleButton(JButton btn) {
        btn.setBackground(LIGHT_PINK);
        btn.setForeground(NEON_PINK);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(90, 35));
        btn.setBorder(BorderFactory.createLineBorder(HOT_PINK, 2));
    }

    /*
     Updates the stopwatch display.
     LOGICAL FLOW:
     Running: previous time + current segment
     Paused: show accumulated time only
     */
    private void updateDisplay() {
        long totalElapsed = running
                ? elapsedTime + (System.nanoTime() - startTime)
                : elapsedTime;

        timeLabel.setText(formatTime(totalElapsed));
    }
    /*
    Returns the current running time
    used for lap recording.
    */
    private String formatTime() {
        return formatTime(elapsedTime + (System.nanoTime() - startTime));
    }

    private String formatTime(long nanoTime) { //Converts nanoseconds into MM:SS:CC format.
        long totalMilliseconds = nanoTime / 1_000_000;
        long minutes = totalMilliseconds / 60000;
        long seconds = (totalMilliseconds / 1000) % 60;
        long centiseconds = (totalMilliseconds / 10) % 100;

        return String.format("%02d:%02d:%02d", minutes, seconds, centiseconds);
    }

    // MAIN METHOD
    public static void main(String[] args) { // Ensures Swing components are created on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            new StopwatchExplanation().setVisible(true);
        });
    }
}
