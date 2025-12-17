package filemanager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Stack;

/**
 * Главный класс файлового менеджера.
 */
public class FileManagerApp {

    private static final Logger logger = LogManager.getLogger(FileManagerApp.class);

    private JFrame frame;
    private JList<File> fileList;
    private DefaultListModel<File> listModel;
    private File currentDir;

    // стек для возврата назад
    private final Stack<File> history = new Stack<>();

    public static void main(String[] args) {
        logger.info("Запуск файлового менеджера");
        SwingUtilities.invokeLater(FileManagerApp::new);
    }

    public FileManagerApp() {
        frame = new JFrame("File Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);

        listModel = new DefaultListModel<>();
        fileList = new JList<>(listModel);
        fileList.setCellRenderer(new FileRenderer());

        // двойной клик по папке
        fileList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    File selected = fileList.getSelectedValue();
                    if (selected != null && selected.isDirectory()) {
                        openDirectory(selected);
                    }
                }
            }
        });

        JButton openBtn = new JButton("Открыть папку");
        JButton copyBtn = new JButton("Копировать файл");
        JButton backBtn = new JButton("Назад");

        openBtn.addActionListener(e -> chooseDirectory());
        copyBtn.addActionListener(e -> copySelectedFile());
        backBtn.addActionListener(e -> goBack());

        JPanel topPanel = new JPanel();
        topPanel.add(openBtn);
        topPanel.add(copyBtn);
        topPanel.add(backBtn);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(fileList), BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void chooseDirectory() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            if (currentDir != null) {
                history.push(currentDir);
            }
            currentDir = chooser.getSelectedFile();
            loadFiles();
            logger.info("Открыта директория: {}", currentDir.getAbsolutePath());
        }
    }

    private void openDirectory(File dir) {
        if (currentDir != null) {
            history.push(currentDir);
        }
        currentDir = dir;
        loadFiles();
        logger.info("Переход в директорию: {}", dir.getAbsolutePath());
    }

    private void goBack() {
        if (!history.isEmpty()) {
            currentDir = history.pop();
            loadFiles();
            logger.info("Возврат назад: {}", currentDir.getAbsolutePath());
        } else {
            JOptionPane.showMessageDialog(frame, "Некуда возвращаться");
        }
    }

    private void loadFiles() {
        listModel.clear();
        File[] files = currentDir.listFiles();
        if (files != null) {
            for (File f : files) {
                listModel.addElement(f);
            }
        }
    }

    private void copySelectedFile() {
        File selected = fileList.getSelectedValue();
        if (selected == null || selected.isDirectory()) {
            JOptionPane.showMessageDialog(frame, "Выберите файл");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            try {
                FileUtils.copyFile(
                        selected.toPath(),
                        chooser.getSelectedFile().toPath()
                );
                logger.info("Файл скопирован: {}", selected.getName());
            } catch (Exception ex) {
                logger.error("Ошибка копирования", ex);
                JOptionPane.showMessageDialog(frame, "Ошибка копирования файла");
            }
        }
    }

    /**
     * Отрисовка файлов с размером.
     */
    static class FileRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            File file = (File) value;

            if (file.isFile()) {
                try {
                    long size = FileUtils.getFileSize(file.toPath());
                    setText(file.getName() + " (" + size + " байт)");
                } catch (Exception e) {
                    setText(file.getName());
                }
            } else {
                setText("[DIR] " + file.getName());
            }

            return this;
        }
    }
}

