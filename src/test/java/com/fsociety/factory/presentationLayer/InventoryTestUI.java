package com.fsociety.factory.presentationLayer;

import com.fsociety.factory.BusinessLayer.Inventory.Inventory;
import com.fsociety.factory.BusinessLayer.Inventory.Item;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryTestUI extends JFrame {

    private final Inventory inventory;
    private final JTextArea outputArea;
    private final JComboBox<String> categoryFilterComboBox;

    public InventoryTestUI() {
        this.inventory = Inventory.getInstance();
        setTitle("Inventory Logic Test UI");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton showAllButton = new JButton("عرض الكل");
        JButton addButton = new JButton("إضافة عنصر");
        JButton deleteButton = new JButton("حذف المحدد");
        JButton consumeButton = new JButton("استهلاك كمية");
        JButton persistButton = new JButton("حفظ التغييرات");

        controlPanel.add(showAllButton);
        controlPanel.add(addButton);
        controlPanel.add(deleteButton);
        controlPanel.add(consumeButton);
        controlPanel.add(persistButton);

        controlPanel.add(new JSeparator(SwingConstants.VERTICAL));
        controlPanel.add(new JLabel("فلترة حسب الفئة:"));
        categoryFilterComboBox = new JComboBox<>(new String[]{"ALL", "ELECTRONICS", "CLOTHING", "FOOD"});
        JButton filterButton = new JButton("فلترة");
        controlPanel.add(categoryFilterComboBox);
        controlPanel.add(filterButton);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(outputArea);

        add(controlPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        showAllButton.addActionListener(e -> testShowAllItems());
        addButton.addActionListener(e -> testAddNewItemWithDialog());
        deleteButton.addActionListener(e -> testDeleteItem());
        consumeButton.addActionListener(e -> testConsumeItem());
        persistButton.addActionListener(e -> testPersistChanges());
        filterButton.addActionListener(e -> testFilterByCategory());

        outputArea.setText("مرحباً! المخزون جاهز للاختبار.\nاضغط على 'عرض كل العناصر' للبدء.\n");
    }

    private void displayItems(List<Item> itemsToShow) {
        if (itemsToShow.isEmpty()) {
            outputArea.append("لا توجد عناصر تطابق هذا البحث.\n");
            return;
        }
        for (Item item : itemsToShow) {
            String line = String.format("ID: %-3d | Name: %-15s | Category: %-12s | Qty: %-5d | Price: %.2f\n",
                    item.getId(), item.getName(), item.getCategoryName(), item.getAvailableQuantity(), item.getPrice());
            outputArea.append(line);
        }
    }

    private void testShowAllItems() {
        outputArea.setText("--- عرض كل العناصر في الذاكرة ---\n");
        List<Item> allItems = inventory.getAllItems();
        displayItems(allItems);
    }

    private void testAddNewItemWithDialog() {
        JTextField nameField = new JTextField();
        JTextField categoryIdField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField minQuantityField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Category ID (0=E, 1=C, 2=F):"));
        panel.add(categoryIdField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Available Quantity:"));
        panel.add(quantityField);
        panel.add(new JLabel("Minimum Quantity:"));
        panel.add(minQuantityField);

        int result = JOptionPane.showConfirmDialog(this, panel, "إضافة عنصر جديد",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Item newItem = new Item();
                newItem.setName(nameField.getText());
                newItem.setCategoryID(Integer.parseInt(categoryIdField.getText()));
                newItem.setPrice(Double.parseDouble(priceField.getText()));
                newItem.setAvailableQuantity(Integer.parseInt(quantityField.getText()));
                newItem.setMinAllowedQuantity(Integer.parseInt(minQuantityField.getText()));

                outputArea.setText("--- اختبار إضافة عنصر جديد ---\n");
                outputArea.append("محاولة إضافة: " + newItem.getName() + "\n");
                boolean success = inventory.addNewItem(newItem);
                if (success) {
                    outputArea.append(">>> نجاح! تم إضافة العنصر.\n");
                } else {
                    outputArea.append(">>> فشل! لم يتم إضافة العنصر.\n");
                }
                testShowAllItems();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "خطأ في الإدخال. يرجى إدخال أرقام صحيحة.", "خطأ", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void testDeleteItem() {
        String idString = JOptionPane.showInputDialog(this, "أدخل ID العنصر الذي تريد حذفه:");
        if (idString == null || idString.trim().isEmpty()) return;
        try {
            int idToDelete = Integer.parseInt(idString);
            outputArea.setText("--- اختبار حذف العنصر ID: " + idToDelete + " ---\n");
            boolean success = inventory.deleteItem(idToDelete);
            if (success) {
                outputArea.append(">>> نجاح! تم حذف العنصر.\n");
            } else {
                outputArea.append(">>> فشل! العنصر غير موجود.\n");
            }
            testShowAllItems();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "الرجاء إدخال رقم صحيح.");
        }
    }

    private void testConsumeItem() {
        String idString = JOptionPane.showInputDialog(this, "أدخل ID العنصر الذي تريد استهلاكه:");
        if (idString == null) return;
        String qtyString = JOptionPane.showInputDialog(this, "أدخل الكمية المراد استهلاكها:");
        if (qtyString == null) return;
        try {
            int idToConsume = Integer.parseInt(idString);
            int qtyToConsume = Integer.parseInt(qtyString);
            outputArea.setText("--- اختبار استهلاك " + qtyToConsume + " من العنصر ID: " + idToConsume + " ---\n");
            boolean success = inventory.consumeItemQuantity(idToConsume, qtyToConsume);
            if (success) {
                outputArea.append(">>> نجاح! تم استهلاك الكمية من الذاكرة.\n");
            } else {
                outputArea.append(">>> فشل! الكمية غير كافية أو العنصر غير موجود.\n");
            }
            testShowAllItems();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "الرجاء إدخال أرقام صحيحة.");
        }
    }

    private void testPersistChanges() {
        outputArea.setText("--- اختبار حفظ التغييرات في الذاكرة إلى الملف ---\n");
        inventory.persistChanges();
        outputArea.append(">>> تم استدعاء دالة الحفظ. تحقق من ملف items.csv للتأكد من تحديث الكميات.\n");
    }

    private void testFilterByCategory() {
        String selectedCategoryName = (String) categoryFilterComboBox.getSelectedItem();
        outputArea.setText("--- فلترة العناصر حسب الفئة: " + selectedCategoryName + " ---\n");
        List<Item> allItems = inventory.getAllItems();
        if ("ALL".equalsIgnoreCase(selectedCategoryName)) {
            displayItems(allItems);
            return;
        }
        List<Item> filteredItems = allItems.stream()
                .filter(item -> selectedCategoryName.equalsIgnoreCase(item.getCategoryName()))
                .collect(Collectors.toList());
        displayItems(filteredItems);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InventoryTestUI().setVisible(true));
    }
}
