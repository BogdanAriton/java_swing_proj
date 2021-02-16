package guiPackage;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;

import generateReportPackage.GenerateReportImpl;
import guiPackage.cr_worker;

public class cr_tester {

    private int rptcount = 0;
    private JFrame frmReportValidationTool;
    private JTable table;
    private JTextField txtGetLocalLocastion;
    final static Logger logger = Logger.getLogger(cr_tester.class);

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                	cr_tester window = new cr_tester();
                    window.frmReportValidationTool.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public cr_tester() {       
		initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
    	GenerateReportImpl reportImpl = new GenerateReportImpl();
        frmReportValidationTool = new JFrame();
        frmReportValidationTool.setResizable(false);
        frmReportValidationTool.setTitle("RPT checker");
        frmReportValidationTool.setBounds(100, 100, 878, 802);
        frmReportValidationTool.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmReportValidationTool.getContentPane().setLayout(null);

        JLabel lblUseThisTool =
                new JLabel("Info: Use this tool to determine if a report has a sub-report with un-mapped parameters.");
        lblUseThisTool.setBounds(10, 11, 816, 14);
        frmReportValidationTool.getContentPane().add(lblUseThisTool);

        JPanel panel_1 = new JPanel();
        panel_1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Select report folder",
                TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        panel_1.setBounds(10, 47, 437, 76);
        frmReportValidationTool.getContentPane().add(panel_1);
        panel_1.setLayout(null);

        JLabel lblNewLabel = new JLabel("New label");
        lblNewLabel.setBounds(10, 48, 417, 23);
        panel_1.add(lblNewLabel);

        lblNewLabel.setText("C:\\rpts");

        JProgressBar progressBar = new JProgressBar();
        progressBar.setBounds(370, 142, 158, 23);
        frmReportValidationTool.getContentPane().add(progressBar);
        progressBar.setVisible(false);

        JButton btnNewButton = new JButton("Browse...");
        btnNewButton.setBounds(10, 21, 102, 23);
        panel_1.add(btnNewButton);
        btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 10));

        btnNewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                jfc.setDialogTitle("Choose the reports directory: ");
                jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnValue = jfc.showOpenDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    if (jfc.getSelectedFile().isDirectory()) {
                        System.out.println("You selected the directory: " + jfc.getSelectedFile());
                        if (jfc.getSelectedFile().getPath() != null) {
                            lblNewLabel.setText(jfc.getSelectedFile().getPath());
                        }
                    }
                }
            }
        });

        DefaultTableModel model = new DefaultTableModel(new Object[][][][][][][][]{},
                new String[]{"ID", "RPT Name", "Found un-mapped parameters", "DB Driver Info","Non-String DB Params", "Stored Procedures Found in RPT", "Issues" });

        JPanel panel = new JPanel();
        panel.setToolTipText("Reports found under selected directory");
        panel.setBorder(
                new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Reports found under selected directory",
                        TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        panel.setBounds(10, 176, 852, 586);
        frmReportValidationTool.getContentPane().add(panel);
        panel.setLayout(null);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(6, 16, 836, 526);
        panel.add(scrollPane);
        table = new JTable();
        scrollPane.setViewportView(table);
        table.setFont(new Font("Tahoma", Font.PLAIN, 10));
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setModel(model);

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                System.exit(0);
            }
        });
        btnClose.setBounds(753, 553, 89, 23);
        panel.add(btnClose);

        JTableHeader th = table.getTableHeader();
        TableColumnModel tcm = th.getColumnModel();
        TableColumn tc = tcm.getColumn(0);
        tc.setHeaderValue("ID");
        tc.setMinWidth(0);
        tc.setPreferredWidth(2);
        tc = tcm.getColumn(1);
        tc.setHeaderValue("RPT Name");
        tc = tcm.getColumn(2);
        tc.setResizable(true);
        tc.setHeaderValue("Found un-mapped parameters");
        tc = tcm.getColumn(3);
        tc.setHeaderValue("DB Driver Info");
        tc = tcm.getColumn(4);
        tc.setHeaderValue("Non-String DB Param Types");
        tc = tcm.getColumn(5);
        tc.setHeaderValue("Stored Procedures Found in RPT");
        tc = tcm.getColumn(6);
        tc.setHeaderValue("Issues");
        th.repaint();

        JButton btnSelectAll = new JButton("Select All");
        JButton btnNewButton_1 = new JButton("Load RPTs");
        btnNewButton_1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (lblNewLabel.getText() == null) {
                    JOptionPane.showMessageDialog(frmReportValidationTool, "Please select the reports location!");
                } else {
                    DefaultTableModel tmdl = (DefaultTableModel) table.getModel();
                    if (tmdl.getRowCount() > 0) {
                        int rowcount = tmdl.getRowCount();
                        for (int i = rowcount - 1; i >= 0; i--) {
                            tmdl.removeRow(i);
                        }
                    }
                    File folder = new File(lblNewLabel.getText());
                    if (folder.isDirectory()) {
                        File[] listOfFiles = folder.listFiles();
                        int i = 1;
                        System.out.println("Searching in directory: " + lblNewLabel.getText());
                        for (File listOfFile : listOfFiles) {
                            if (listOfFile.isFile()) {
                                System.out.println("Found file: " + listOfFile.getName());
                                if (listOfFile.getName().contains(".rpt")) {
                                    System.out.println("Found RPT: " + listOfFile.getName());
                                    model.addRow(new Object[]{"" + i, listOfFile.getName(), "", "", "", "" });
                                    i++;
                                }
                            }
                        }
                        if (i == 1) {
                            JOptionPane.showMessageDialog(frmReportValidationTool,
                                    "No RPT files were found at this location: !" + lblNewLabel.getText());
                        } else {
                            rptcount = i - 1;
                        }
                    } else {
                        JOptionPane.showMessageDialog(frmReportValidationTool, "Location was not found!");
                    }
                    table.repaint();
                }
            }
        });
        btnNewButton_1.setFont(new Font("Tahoma", Font.PLAIN, 10));
        btnNewButton_1.setBounds(10, 142, 118, 23);
        frmReportValidationTool.getContentPane().add(btnNewButton_1);

        JSeparator separator = new JSeparator();
        separator.setBounds(10, 132, 852, 14);
        frmReportValidationTool.getContentPane().add(separator);

        JButton btnSeeOnly = new JButton("Show only RPTs with issues");
        JButton btnNewButton_2 = new JButton("Get report info");
        btnNewButton_2.setFont(new Font("Tahoma", Font.PLAIN, 10));
        cr_worker workerObj = new cr_worker();
        btnNewButton_2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (table.getSelectedRowCount() == 0) {
                    JOptionPane.showMessageDialog(frmReportValidationTool, "Please select rows from the above table!");
                } else {
                    rptcount = table.getSelectedRowCount();
                    progressBar.setMaximum(rptcount);
                    progressBar.setVisible(true);

                    for (int i : table.getSelectedRows()) {
                        logger.debug("Checking report: " + table.getValueAt(i, 0));
                        SwingWorker<String[], Object> worker = new SwingWorker<String[], Object>() {
                            @Override
                            public String[] doInBackground() {
                                btnNewButton_2.setEnabled(false);
                                btnNewButton_1.setEnabled(false);
                                btnSeeOnly.setEnabled(false);
                                btnSelectAll.setEnabled(false);
                                table.setEnabled(false);
                                return workerObj.SwingWorker(
                                        lblNewLabel.getText() + "/" + table.getValueAt(i, 1).toString(),
                                        txtGetLocalLocastion.getText());
                            }

                            @Override
                            public void done() {
                                try {
                                    String[] workerResults = get();
                                    String resum = "";
                                    if (workerResults[1] == "0") {
                                        table.setValueAt(workerResults[0], i, 2);
                                        table.setValueAt(workerResults[2], i, 3);
                                        table.setValueAt(workerResults[3], i, 4);
                                        table.setValueAt(workerResults[4], i, 5);
                                        if (!workerResults[0].contains("count: 0")) {
                                            resum = resum + "Found un-mapped params; ";
                                        }
                                        if (workerResults[2].contains("SQLNCLI10")) {
                                            resum = resum + "Found SQLNCLI10 provider; ";
                                        }
                                        if (!workerResults[3].isEmpty())
                                        {
                                        	resum = resum + "Found non-string DB Param Types; ";
                                        }
                                        if (workerResults[4].isEmpty())
                                        {
                                        	table.setValueAt("No DB Connection defined for Report", i, 5);
                                        }
                                        table.setValueAt(resum, i, 6);
                                    } else {
                                        table.setValueAt("Error while reading report", i, 2);
                                        table.setValueAt("Error while reading report", i, 3);
                                        table.setValueAt("Error while reading report", i, 4);
                                        table.setValueAt("Error while reading report", i, 5);
                                        table.setValueAt(workerResults[0], i, 6);
                                    }
                                    rptcount = rptcount - 1;
                                    progressBar.setValue(progressBar.getMaximum() - rptcount);
                                    if (rptcount == 0) {
                                        JOptionPane.showMessageDialog(frmReportValidationTool,
                                                "Finished processing reports!");
                                        btnNewButton_2.setEnabled(true);
                                        btnNewButton_1.setEnabled(true);
                                        btnSeeOnly.setEnabled(true);
                                        btnSelectAll.setEnabled(true);
                                        btnSeeOnly.setEnabled(true);
                                        table.setEnabled(true);
                                        progressBar.setVisible(false);
                                    }
                                    System.gc();
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                } catch (ExecutionException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        };
                        worker.execute();
                    }
                }
            }
        });
        btnNewButton_2.setBounds(237, 142, 123, 23);
        frmReportValidationTool.getContentPane().add(btnNewButton_2);

        JSeparator separator_1 = new JSeparator();
        separator_1.setBounds(10, 36, 856, 8);
        frmReportValidationTool.getContentPane().add(separator_1);

        JPanel panel_2 = new JPanel();
        panel_2.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Logging details",
                TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        panel_2.setBounds(457, 47, 405, 76);
        frmReportValidationTool.getContentPane().add(panel_2);
        panel_2.setLayout(null);

        JLabel lblLogLocation = new JLabel("Log location");
        lblLogLocation.setBounds(16, 24, 80, 14);
        panel_2.add(lblLogLocation);

        txtGetLocalLocastion = new JTextField();
        txtGetLocalLocastion.setBounds(16, 45, 235, 20);
        panel_2.add(txtGetLocalLocastion);
        txtGetLocalLocastion.setText("C:\\RPTTesterToolLogs");
        txtGetLocalLocastion.setColumns(10);

        reportImpl.setLogger(txtGetLocalLocastion.getText() + "/oms-reporting.log",
                txtGetLocalLocastion.getText() + "/sap-reporting.log", "DEBUG", "ERROR");

        JButton btnRefresh = new JButton("Repoint Log Location");
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	cr_worker.CreateLogs(txtGetLocalLocastion.getText()+"/oms-reporting.log");
            	cr_worker.CreateLogs(txtGetLocalLocastion.getText()+"/sap-reporting.log");
                reportImpl.setLogger(txtGetLocalLocastion.getText() + "/oms-reporting.log",
                        txtGetLocalLocastion.getText() + "/sap-reporting.log", "DEBUG", "ERROR");
            }
        });
        btnRefresh.setFont(new Font("Tahoma", Font.PLAIN, 10));
        btnRefresh.setBounds(261, 44, 134, 23);
        panel_2.add(btnRefresh);

        btnSelectAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (table.getRowCount() > 0) {
                    table.setRowSelectionInterval(0, table.getRowCount() - 1);
                }
            }
        });
        btnSelectAll.setFont(new Font("Tahoma", Font.PLAIN, 10));
        btnSelectAll.setBounds(138, 142, 89, 23);
        frmReportValidationTool.getContentPane().add(btnSelectAll);

        btnSeeOnly.setFont(new Font("Tahoma", Font.PLAIN, 10));
        btnSeeOnly.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                // Will remove reports with no issues
                // Issues: Driver Provider to be SQLNCLI10
                // Count of unlinked params greater than 0
                // Errors found while running the scan
                DefaultTableModel tmdl = (DefaultTableModel) table.getModel();
                if (tmdl.getRowCount() > 0) {
                    int rowcount = tmdl.getRowCount();
                    for (int i = rowcount - 1; i >= 0; i--) {
                        if ((table.getValueAt(i, 4).toString().isEmpty())) {
                            tmdl.removeRow(i);
                        }
                    }
                }
                if (tmdl.getRowCount() > 0) {
                    int rowcount = tmdl.getRowCount();
                    for (int i = 0; i < rowcount; i++) {
                        table.setValueAt(i + 1, i, 0);
                    }
                }
                table.repaint();
            }
        });
        btnSeeOnly.setBounds(672, 142, 179, 23);
        frmReportValidationTool.getContentPane().add(btnSeeOnly);

    }
}
