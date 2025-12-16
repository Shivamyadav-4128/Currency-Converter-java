import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.awt.image.BufferedImage;

public class CurrencyConverter extends JFrame {

    private JComboBox<String> fromCurrency, toCurrency;
    private JFormattedTextField amountField;
    private JTextField resultField;
    private DefaultListModel<String> historyModel;

    private final HashMap<String, Double> rates = new HashMap<>();
    private final LinkedList<String> history = new LinkedList<>();
    private static final int MAX_HISTORY = 10;

    public CurrencyConverter() {
        setTitle("Currency Converter");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(20, 20));
        main.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(main);

        JLabel title = new JLabel("Currency Converter", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        main.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.GRAY),
                new EmptyBorder(20, 20, 20, 20)
        ));
        main.add(center, BorderLayout.CENTER);

        rates.put("USD", 1.0);
        rates.put("INR", 82.7);
        rates.put("EUR", 0.92);
        rates.put("GBP", 0.78);
        rates.put("JPY", 136.5);

        String[] currencies = rates.keySet().toArray(new String[0]);

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 10, 10, 10);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0;
        center.add(new JLabel("From:"), g);

        g.gridx = 1;
        fromCurrency = new JComboBox<>(currencies);
        fromCurrency.setRenderer(new FlagRenderer());
        center.add(fromCurrency, g);

        g.gridx = 2;
        center.add(new JLabel("To:"), g);

        g.gridx = 3;
        toCurrency = new JComboBox<>(currencies);
        toCurrency.setRenderer(new FlagRenderer());
        center.add(toCurrency, g);

        g.gridx = 0; g.gridy = 1;
        center.add(new JLabel("Amount:"), g);

        g.gridx = 1; g.gridwidth = 3;
        amountField = new JFormattedTextField(NumberFormat.getNumberInstance());
        center.add(amountField, g);

        g.gridx = 0; g.gridy = 2; g.gridwidth = 1;
        center.add(new JLabel("Result:"), g);

        g.gridx = 1; g.gridwidth = 3;
        resultField = new JTextField();
        resultField.setEditable(false);
        center.add(resultField, g);

        JButton convert = new JButton("Convert");
        convert.addActionListener(e -> convert());

        g.gridx = 1; g.gridy = 3; g.gridwidth = 2;
        center.add(convert, g);

        historyModel = new DefaultListModel<>();
        JList<String> historyList = new JList<>(historyModel);
        main.add(new JScrollPane(historyList), BorderLayout.SOUTH);

        setVisible(true);
    }

    private void convert() {
        try {
            double amt = Double.parseDouble(amountField.getValue().toString());
            String from = (String) fromCurrency.getSelectedItem();
            String to = (String) toCurrency.getSelectedItem();

            double result = amt / rates.get(from) * rates.get(to);
            resultField.setText(String.format("%.2f %s", result, to));

            String time = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("HH:mm"));

            history.addFirst("[" + time + "] " + amt + " " + from + " → " + result + " " + to);

            if (history.size() > MAX_HISTORY)
                history.removeLast();

            historyModel.clear();
            for (String s : history)
                historyModel.addElement(s);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid Input");
        }
    }

    static class FlagRenderer extends DefaultListCellRenderer {
        private final HashMap<String, ImageIcon> icons = new HashMap<>();

        public FlagRenderer() {
            icons.put("USD", icon(Color.RED));
            icons.put("INR", icon(Color.ORANGE));
            icons.put("EUR", icon(Color.BLUE));
            icons.put("GBP", icon(Color.PINK));
            icons.put("JPY", icon(Color.GREEN));
        }

        private ImageIcon icon(Color c) {
            BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            g.setColor(c);
            g.fillOval(0, 0, 16, 16);
            g.dispose();
            return new ImageIcon(img);
        }

        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

            JLabel lbl = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

            lbl.setIcon(icons.get(value));
            return lbl;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CurrencyConverter::new);
    }
}
