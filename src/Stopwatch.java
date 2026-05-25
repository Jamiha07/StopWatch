import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Stopwatch extends JFrame implements ActionListener {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(Stopwatch::new);
    }

    // UI Navigation & Views
    private final JLabel timeDisplay = new JLabel("00:00:00.00");
    private final AnalogClockPanel analogPanel = new AnalogClockPanel();
    private final JPanel displayCardPanel = new JPanel(new CardLayout());
    private boolean isAnalogMode = false;

    // Control Buttons (Main Actions)
    private final JButton startPauseBtn = new ModernButton("START");
    private final JButton lapBtn = new ModernButton("LAP");
    private final JButton resetBtn = new ModernButton("RESET");

    // Feature Buttons (Utility Layer)
    private final JButton viewToggleBtn = new ModernButton("ANALOG VIEW");
    private final JButton themeBtn = new ModernButton("THEME: EMERALD");
    private final JButton clearLapsBtn = new ModernButton("CLEAR LAPS");
    private final JButton saveLapsBtn = new ModernButton("SAVE LAPS");
    private final JButton closeBtn = new ModernButton("CLOSE");

    private final JTextArea lapArea = new JTextArea(10, 22);
    private final JCheckBox alwaysOnTopCb = new JCheckBox("Always on Top");
    private final JScrollPane scrollPane;
    private final JPanel topPanel, topLeft, topRight, centerPanel, featurePanel, btnPanel;

    // Logic Variables
    private final Timer timer;
    private int elapsedTime = 0;
    private boolean isRunning = false;
    private int lapCount = 0;
    private int currentTheme = 0; // 0 = Emerald, 1 = Dark, 2 = Light

    public Stopwatch() {

        this.setTitle("Pro Stopwatch 2026");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500, 660);
        this.setLayout(new BorderLayout(0, 0));


        topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(10, 15, 5, 15));

        topLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        topLeft.setOpaque(false);
        topRight.setOpaque(false);

        alwaysOnTopCb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        alwaysOnTopCb.addActionListener(e -> this.setAlwaysOnTop(alwaysOnTopCb.isSelected()));
        topLeft.add(alwaysOnTopCb);

        // Style Window Exit Action Route Button
        closeBtn.setPreferredSize(new Dimension(75, 28));
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        closeBtn.setBackground(new Color(231, 76, 60));
        closeBtn.setForeground(Color.WHITE);
        topRight.add(closeBtn);

        topPanel.add(topLeft, BorderLayout.WEST);
        topPanel.add(topRight, BorderLayout.EAST);
        this.add(topPanel, BorderLayout.NORTH);

        // Center Panel
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(new EmptyBorder(10, 25, 10, 25));

        // Setup Main Digital Text Configuration
        timeDisplay.setFont(new Font("Monospaced", Font.BOLD, 56));
        timeDisplay.setAlignmentX(Component.CENTER_ALIGNMENT);
        timeDisplay.setHorizontalAlignment(SwingConstants.CENTER);
        timeDisplay.setPreferredSize(new Dimension(400, 160));

        displayCardPanel.add(timeDisplay, "DIGITAL");
        displayCardPanel.add(analogPanel, "ANALOG");
        displayCardPanel.setOpaque(false);

        featurePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        featurePanel.setOpaque(false);

        styleMiniButton(viewToggleBtn);
        styleMiniButton(themeBtn);
        styleMiniButton(clearLapsBtn);
        styleMiniButton(saveLapsBtn);

        featurePanel.add(viewToggleBtn);
        featurePanel.add(themeBtn);
        featurePanel.add(clearLapsBtn);
        featurePanel.add(saveLapsBtn);

        // Lap Area Configuration
        lapArea.setEditable(false);
        lapArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        lapArea.setBorder(new EmptyBorder(8, 8, 8, 8));

        scrollPane = new JScrollPane(lapArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 116, 139), 1), "Recorded Laps", 0, 0, new Font("Segoe UI", Font.BOLD, 12), Color.WHITE));

        centerPanel.add(displayCardPanel);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(featurePanel);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(scrollPane);
        this.add(centerPanel, BorderLayout.CENTER);

        //  Bottom Panel (Main Action Dashboard Control Area) ---
        btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        btnPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        styleMainButton(startPauseBtn, new Color(46, 204, 113));
        styleMainButton(lapBtn, new Color(155, 89, 182));
        styleMainButton(resetBtn, new Color(149, 165, 166)); // Clean metallic grey when inactive

        btnPanel.add(startPauseBtn);
        btnPanel.add(lapBtn);
        btnPanel.add(resetBtn);
        this.add(btnPanel, BorderLayout.SOUTH);

        //  Attach Action Event Target Listeners
        startPauseBtn.addActionListener(this);
        lapBtn.addActionListener(this);
        resetBtn.addActionListener(this);
        viewToggleBtn.addActionListener(this);
        themeBtn.addActionListener(this);
        clearLapsBtn.addActionListener(this);
        saveLapsBtn.addActionListener(this);
        closeBtn.addActionListener(this);

        //Internal Engine Clock Loop Execution Logic
        timer = new Timer(10, e -> {
            elapsedTime += 10;
            updateDisplay();
            if (isAnalogMode) {
                analogPanel.repaint();
            }
        });

        applyTheme();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void styleMainButton(JButton btn, Color color) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(115, 42));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
    }

    private void styleMiniButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setPreferredSize(new Dimension(105, 32));
        btn.setForeground(Color.WHITE);
    }

    private void updateDisplay() {
        int h = (elapsedTime / 3600000);
        int m = (elapsedTime / 60000) % 60;
        int s = (elapsedTime / 1000) % 60;
        int ms = (elapsedTime / 10) % 100;
        timeDisplay.setText(String.format("%02d:%02d:%02d.%02d", h, m, s, ms));
    }

    private void applyTheme() {
        Color bg, panelBg, text, utilityBtnBg, borderTextColor;

        if (currentTheme == 0) { // Emerald Neon Base Mode
            bg = new Color(15, 23, 42);
            panelBg = new Color(30, 41, 59);
            text = new Color(52, 211, 153);
            utilityBtnBg = new Color(51, 65, 85);
            borderTextColor = Color.WHITE;
            themeBtn.setText("THEME: EMERALD");
        } else if (currentTheme == 1) { // Dark Sleek Mode
            bg = new Color(18, 18, 18);
            panelBg = new Color(33, 33, 33);
            text = new Color(240, 240, 240);
            utilityBtnBg = new Color(50, 50, 50);
            borderTextColor = new Color(200, 200, 200);
            themeBtn.setText("THEME: DARK");
        } else { // Flat Light Paper Mode
            bg = new Color(245, 245, 247);
            panelBg = Color.WHITE;
            text = new Color(28, 28, 30);
            utilityBtnBg = new Color(210, 210, 215);
            borderTextColor = new Color(40, 40, 40);
            themeBtn.setText("THEME: LIGHT");
        }

        this.getContentPane().setBackground(bg);
        topPanel.setBackground(bg);
        centerPanel.setBackground(bg);
        btnPanel.setBackground(bg);

        timeDisplay.setForeground(text);
        lapArea.setBackground(panelBg);
        lapArea.setForeground(currentTheme == 2 ? Color.BLACK : Color.LIGHT_GRAY);

        alwaysOnTopCb.setForeground(currentTheme == 2 ? Color.BLACK : Color.WHITE);


        JButton[] miniBtns = {viewToggleBtn, themeBtn, clearLapsBtn, saveLapsBtn};
        for (JButton btn : miniBtns) {
            btn.setBackground(utilityBtnBg);
            btn.setForeground(currentTheme == 2 ? Color.BLACK : Color.WHITE);
        }

        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 116, 139), 1), "Recorded Laps", 0, 0, new Font("Segoe UI", Font.BOLD, 12), borderTextColor));

        analogPanel.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == startPauseBtn) {
            if (!isRunning) {
                timer.start();
                startPauseBtn.setText("PAUSE");
                startPauseBtn.setBackground(new Color(241, 196, 15)); // Vibrant Alert Yellow
                resetBtn.setBackground(new Color(231, 76, 60)); // Enable visual reset availability route
                resetBtn.setText("RESET");
            } else {
                timer.stop();
                startPauseBtn.setText("RESUME");
                startPauseBtn.setBackground(new Color(46, 204, 113));
            }
            isRunning = !isRunning;
        }
        else if (source == lapBtn) {
            if (isRunning || elapsedTime > 0) {
                lapCount++;
                lapArea.append(String.format(" Lap %02d: %s%n", lapCount, timeDisplay.getText()));
            }
        }
        else if (source == resetBtn) {
            timer.stop();
            isRunning = false;
            elapsedTime = 0;
            lapCount = 0;
            lapArea.setText("");
            updateDisplay();
            analogPanel.repaint();
            startPauseBtn.setText("START");
            startPauseBtn.setBackground(new Color(46, 204, 113));
            resetBtn.setBackground(new Color(149, 165, 166)); // Fallback back to grey state
        }
        else if (source == viewToggleBtn) {
            CardLayout cl = (CardLayout) displayCardPanel.getLayout();
            isAnalogMode = !isAnalogMode;
            if (isAnalogMode) {
                cl.show(displayCardPanel, "ANALOG");
                viewToggleBtn.setText("DIGITAL VIEW");
            } else {
                cl.show(displayCardPanel, "DIGITAL");
                viewToggleBtn.setText("ANALOG VIEW");
            }
        }
        else if (source == themeBtn) {
            currentTheme = (currentTheme + 1) % 3;
            applyTheme();
        }
        else if (source == clearLapsBtn) {
            lapArea.setText("");
            lapCount = 0;
        }
        else if (source == saveLapsBtn) {
            if (lapArea.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "No laps recorded to save!", "Empty Log", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("stopwatch_laps.txt", false))) {
                writer.write("--- Stopwatch Lap Records ---\n");
                writer.write(lapArea.getText());
                JOptionPane.showMessageDialog(this, "Laps successfully saved to 'stopwatch_laps.txt'!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving file data.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        else if (source == closeBtn) {
            timer.stop();
            this.dispose();
            System.exit(0);
        }
    }

    private static class ModernButton extends JButton {
        public ModernButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (getModel().isPressed()) {
                g2d.setColor(getBackground().darker());
            } else if (getModel().isRollover()) {
                g2d.setColor(getBackground().brighter());
            } else {
                g2d.setColor(getBackground());
            }

            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2d.dispose();

            super.paintComponent(g);
        }
    }

    private class AnalogClockPanel extends JPanel {
        public AnalogClockPanel() {
            this.setPreferredSize(new Dimension(200, 160));
            this.setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int size = Math.min(width, height) - 16;
            int centerX = width / 2;
            int centerY = height / 2;
            int radius = size / 2;

            g2d.setColor(currentTheme == 2 ? new Color(230, 230, 235) : new Color(28, 28, 30));
            g2d.fillOval(centerX - radius, centerY - radius, size, size);

            g2d.setStroke(new BasicStroke(2.5f));
            g2d.setColor(timeDisplay.getForeground());
            g2d.drawOval(centerX - radius, centerY - radius, size, size);

            for (int i = 0; i < 60; i++) {
                double angle = Math.toRadians(i * 6 - 90);
                int outX = (int) (centerX + radius * Math.cos(angle));
                int outY = (int) (centerY + radius * Math.sin(angle));
                int tickLen = (i % 5 == 0) ? 9 : 4;
                int inX = (int) (centerX + (radius - tickLen) * Math.cos(angle));
                int inY = (int) (centerY + (radius - tickLen) * Math.sin(angle));

                g2d.setStroke(new BasicStroke((i % 5 == 0) ? 2.0f : 1.0f));
                g2d.drawLine(inX, inY, outX, outY);
            }

            double seconds = (elapsedTime / 1000.0);
            double handAngle = Math.toRadians(seconds * 6 - 90);

            int handLength = (int) (radius * 0.82);
            int handX = (int) (centerX + handLength * Math.cos(handAngle));
            int handY = (int) (centerY + handLength * Math.sin(handAngle));

            g2d.setColor(new Color(231, 76, 60));
            g2d.setStroke(new BasicStroke(2.0f));
            g2d.drawLine(centerX, centerY, handX, handY);

            g2d.setColor(timeDisplay.getForeground());
            g2d.fillOval(centerX - 4, centerY - 4, 8, 8);
        }
    }
}