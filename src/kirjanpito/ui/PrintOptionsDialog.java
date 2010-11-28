package kirjanpito.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import kirjanpito.db.Period;

/**
 * Tulosteen asetusikkuna.
 * 
 * @author Tommi Helineva
 */
public class PrintOptionsDialog extends JDialog {
	protected JButton okButton;
	protected JButton cancelButton;
	private JPanel[] panels;
	private JRadioButton[] radioButtons;
	private DateTextField[] startDateTextFields;
	private DateTextField[] endDateTextFields;
	private JComboBox monthComboBox;
	private JComboBox yearComboBox;
	private Period period;
	private Date startDate;
	private Date endDate;
	private int tab;
	private int result;
	
	private static final long serialVersionUID = 1L;
	
	public PrintOptionsDialog(Frame owner, String title) {
		super(owner, title, true);
		this.tab = -1;
	}

	public void setStartDateEditable(boolean editable) {
		startDateTextFields[1].setEditable(editable);
	}
	
	/**
	 * Palauttaa tilikauden.
	 * 
	 * @return tilikausi
	 */
	public Period getPeriod() {
		return period;
	}

	/**
	 * Asettaa tilikauden.
	 * 
	 * @param period tilikausi
	 */
	public void setPeriod(Period period) {
		this.period = period;
	}

	/**
	 * Palauttaa alkamispäivämäärän.
	 * 
	 * @return alkamispäivämäärä
	 */
	public Date getStartDate() {
		return startDate;
	}
	
	/**
	 * Asettaa alkamispäivämäärän.
	 * 
	 * @param startDate alkamispäivämäärä
	 */
	public void setStartDate(Date startDate) {
		startDateTextFields[1].setDate(startDate);
	}

	/**
	 * Palauttaa päättymispäivämäärän.
	 * 
	 * @return päättymispäivämäärä
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Asettaa päättymispäivämäärän.
	 * 
	 * @param endDate päättymispäivämäärä
	 */
	public void setEndDate(Date endDate) {
		endDateTextFields[1].setDate(endDate);
	}
	
	public int getResult() {
		return result;
	}
	
	/**
	 * Valitsee välilehden.
	 * 
	 * @param index välilehden numero
	 */
	public void showTab(int index) {
		changePanel(index);
		radioButtons[index].setSelected(true);
	}
	
	/**
	 * Luo ikkunan komponentit.
	 */
	public void create() {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setMinimumSize(getFrameMinimumSize());
		setLocationRelativeTo(getOwner());
		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				if (tab == 0) {
					startDateTextFields[0].requestFocus();
				}
				else if (tab == 1) {
					monthComboBox.requestFocus();
				}
				else {
					startDateTextFields[1].requestFocus();
				}
			}

			public void windowClosing(WindowEvent e) {
				result = JOptionPane.CANCEL_OPTION;
				dispose();
			}
		});
		
		panels = new JPanel[3];
		startDateTextFields = new DateTextField[2];
		endDateTextFields = new DateTextField[2];
		
		createOptionButtonPanel();
		panels[0] = createDateSelectionPanel(false, 0);
		panels[1] = createMonthSelectionPanel();
		panels[2] = createDateSelectionPanel(true, 1);
		
		startDateTextFields[0].setDate(period.getStartDate());
		endDateTextFields[0].setDate(period.getEndDate());
		
		Calendar cal = Calendar.getInstance();
		cal.setLenient(true);
		cal.clear(Calendar.HOUR_OF_DAY);
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);
		
		if (cal.get(Calendar.DAY_OF_MONTH) < 15) {
			cal.add(Calendar.MONTH, -1);
		}
		
		cal.set(Calendar.DAY_OF_MONTH, 1);
		startDateTextFields[1].setDate(cal.getTime());
		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		endDateTextFields[1].setDate(cal.getTime());
		
		createButtonPanel();
		showTab(0);
		pack();
	}
	
	private void createOptionButtonPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		BoxLayout layout = new BoxLayout(panel, BoxLayout.LINE_AXIS);
		panel.setLayout(layout);
		
		radioButtons = new JRadioButton[3];
		radioButtons[0] = new JRadioButton("Koko tilikausi");
		radioButtons[0].setMnemonic('t');
		radioButtons[0].setSelected(true);
		radioButtons[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showTab(0);
			}
		});
		
		radioButtons[1] = new JRadioButton("Kuukausi");
		radioButtons[1].setMnemonic('K');
		radioButtons[1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showTab(1);
			}
		});
		
		radioButtons[2] = new JRadioButton("Muu aikaväli");
		radioButtons[2].setMnemonic('M');
		radioButtons[2].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showTab(2);
			}
		});
		
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(radioButtons[0]);
		buttonGroup.add(radioButtons[1]);
		buttonGroup.add(radioButtons[2]);
		
		panel.add(radioButtons[0]);
		panel.add(Box.createRigidArea(new Dimension(10, 0)));
		panel.add(radioButtons[1]);
		panel.add(Box.createRigidArea(new Dimension(10, 0)));
		panel.add(radioButtons[2]);
		add(panel, BorderLayout.NORTH);
	}
	
	private JPanel createDateSelectionPanel(boolean editable, int index) {
		DateTextField startDateTextField, endDateTextField;
		GridBagConstraints c1 = new GridBagConstraints();
		GridBagConstraints c2 = new GridBagConstraints();
		JPanel panel = new JPanel(new GridBagLayout());
		startDateTextField = new DateTextField();
		startDateTextField.setEditable(editable);
		endDateTextField = new DateTextField();
		endDateTextField.setEditable(editable);
		JLabel label;
		
		label = new JLabel("Alkamispäivämäärä");
		label.setLabelFor(startDateTextField);
		c1.anchor = GridBagConstraints.WEST;
		c1.insets = new Insets(10, 10, 10, 5);
		panel.add(label, c1);
		
		c2.fill = GridBagConstraints.HORIZONTAL;
		c2.insets = new Insets(10, 5, 10, 10);
		c2.weightx = 1.0;
		panel.add(startDateTextField, c2);
		
		label = new JLabel("Päättymispäivämäärä");
		label.setLabelFor(endDateTextField);
		c1.gridx = 0;
		c1.gridy = 1;
		panel.add(label, c1);
		
		c2.gridx = c2.gridy = 1;
		c2.insets = new Insets(10, 5, 10, 10);
		panel.add(endDateTextField, c2);
		
		startDateTextFields[index] = startDateTextField;
		endDateTextFields[index] = endDateTextField;
		return panel;
	}
	
	private JPanel createMonthSelectionPanel() {
		GridBagConstraints c = new GridBagConstraints();
		JPanel panel = new JPanel(new GridBagLayout());
		
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		
		String[] monthItems = new String[] {
				"1  tammikuu",
				"2  helmikuu",
				"3  maaliskuu",
				"4  huhtikuu",
				"5  toukokuu",
				"6  kesäkuu",
				"7  heinäkuu",
				"8  elokuu",
				"9  syyskuu",
				"10  lokakuu",
				"11  marraskuu",
				"12  joulukuu"
		};
		
		monthComboBox = new JComboBox(monthItems);
		monthComboBox.setSelectedIndex(cal.get(Calendar.MONTH));
		c.insets = new Insets(5, 5, 5, 5);
		panel.add(monthComboBox, c);
		
		cal.setTime(period.getStartDate());
		int startYear = cal.get(Calendar.YEAR);
		cal.setTime(period.getEndDate());
		int endYear = cal.get(Calendar.YEAR);
		
		String[] yearItems = new String[endYear - startYear + 1];
		int selectedIndex = 0;
		
		for (int i = 0; i < yearItems.length; i++) {
			yearItems[i] = Integer.toString(startYear + i);
			
			if (startYear + i == year) {
				selectedIndex = i;
			}
		}
		
		yearComboBox = new JComboBox(yearItems);
		yearComboBox.setSelectedIndex(selectedIndex);
		panel.add(yearComboBox, c);
		return panel;
	}
	
	private void createButtonPanel() {
		GridBagConstraints c = new GridBagConstraints();
		JPanel panel = new JPanel(new GridBagLayout());
		
		okButton = new JButton("OK");
		okButton.setPreferredSize(new Dimension(100, 30));
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				accept();
			}
		});
		
		cancelButton = new JButton("Peruuta");
		cancelButton.setPreferredSize(new Dimension(100, 30));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				result = JOptionPane.CANCEL_OPTION;
				dispose();
			}
		});
		
		c.gridx = 1;
		c.gridy = addExtraOptions(panel);
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(5, 10, 10, 5);
		c.weightx = 1.0;
		panel.add(okButton, c);
		
		c.gridx = 2;
		c.insets = new Insets(5, 5, 10, 10);
		c.weightx = 0.0;
		panel.add(cancelButton, c);
		add(panel, BorderLayout.SOUTH);
		rootPane.setDefaultButton(okButton);
	}
	
	protected Dimension getFrameMinimumSize() {
		return new Dimension(450, 200);
	}
	
	protected int addExtraOptions(JPanel panel) {
		return 0;
	}
	
	public void accept() {
		if (tab == 0) {
			startDate = period.getStartDate();
			endDate = period.getEndDate();
		}
		else if (tab == 1) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(period.getStartDate());
			cal.add(Calendar.YEAR, yearComboBox.getSelectedIndex());
			cal.set(Calendar.MONTH, monthComboBox.getSelectedIndex());
			cal.set(Calendar.DAY_OF_MONTH, 1);
			startDate = cal.getTime();
			cal.setLenient(true);
			cal.add(Calendar.MONTH, 1);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			endDate = cal.getTime();
		}
		else {
			try {
				startDate = startDateTextFields[1].getDate();
			}
			catch (ParseException e) {
				SwingUtils.showErrorMessage(this, "Virheellinen alkamispäivämäärä.");
				startDateTextFields[1].requestFocus();
				return;
			}
			
			try {
				endDate = endDateTextFields[1].getDate();
			}
			catch (ParseException e) {
				SwingUtils.showErrorMessage(this, "Virheellinen päättymispäivämäärä.");
				endDateTextFields[1].requestFocus();
				return;
			}
		}
		
		result = JOptionPane.OK_OPTION;
		dispose();
	}
	
	private void changePanel(int index) {
		if (tab == index) return;
		this.tab = index;
		
		for (int i = 0; i < panels.length; i++) {
			if (i == index) {
				add(panels[i], BorderLayout.CENTER);
			}
			else {
				remove(panels[i]);
			}
		}
		
		validate();
		repaint();
	}
}