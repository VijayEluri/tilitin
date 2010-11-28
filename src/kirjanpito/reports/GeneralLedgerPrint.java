package kirjanpito.reports;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import kirjanpito.db.Account;
import kirjanpito.db.Document;
import kirjanpito.db.Entry;

/**
 * Pääkirjatuloste.
 * 
 * @author Tommi Helineva
 */
public class GeneralLedgerPrint extends Print {
	private GeneralLedgerModel model;
	private NumberFormat numberFormat;
	private int[] columns;
	private int numRowsPerPage;
	private int pageCount;
	
	public GeneralLedgerPrint(GeneralLedgerModel model) {
		numberFormat = new DecimalFormat();
		numberFormat.setMinimumFractionDigits(2);
		numberFormat.setMaximumFractionDigits(2);
		this.model = model;
		setPrintId("generalLedger");
	}
	
	public String getTitle() {
		return "Pääkirja";
	}

	public int getPageCount() {
		return pageCount;
	}
	
	public void initialize() {
		super.initialize();
		numRowsPerPage = (getContentHeight() - 7) / 13;
		
		if (numRowsPerPage > 0) {
			pageCount = (int)Math.ceil(model.getRowCount() / (double)numRowsPerPage);
			pageCount = Math.max(1, pageCount); /* Vähintään yksi sivu. */
		}
		else {
			pageCount = 1;
		}
		
		columns = new int[8];
		columns[0] = getMargins().left;
		columns[1] = columns[0] + 50;
		columns[2] = columns[0] + 50;
		columns[3] = columns[0] + 95;
		columns[4] = columns[0] + 210;
		columns[5] = columns[0] + 270;
		columns[6] = columns[0] + 330;
		columns[7] = columns[0] + 340;
	}
	
	public String getVariableValue(String name) {
		if (name.equals("1")) {
			return dateFormat.format(model.getPeriod().getStartDate()) +
				" – " + dateFormat.format(model.getPeriod().getEndDate());
		}
		
		return super.getVariableValue(name);
	}
	
	protected void printHeader() {
		super.printHeader();
		
		/* Tulostetaan sarakeotsikot. */
		setBoldStyle();
		y = margins.top + super.getHeaderHeight() + 12;
		setX(columns[0]);
		drawText("Nro");
		setX(columns[1]);
		drawText("Tili");
		setY(getY() + 13);
		
		setX(columns[2]);
		drawText("Nro");
		setX(columns[3]);
		drawText("Päivämäärä");
		setX(columns[4]);
		drawTextRight("Debet");
		setX(columns[5]);
		drawTextRight("Kredit");
		setX(columns[6]);
		drawTextRight("Saldo");
		setX(columns[7]);
		drawText("Selite");
		setY(getY() + 6);
		drawHorizontalLine(2.0f);
	}
	
	protected int getHeaderHeight() {
		return super.getHeaderHeight() + 35;
	}

	protected void printContent() {
		Document document;
		Account account;
		Entry entry;
		int offset = getPageIndex() * numRowsPerPage;
		int numRows = Math.min(model.getRowCount(), offset + numRowsPerPage);
		setNormalStyle();
		y += 13;
		
		for (int i = offset; i < numRows; i++) {
			account = model.getAccount(i);
			
			if (model.getType(i) == 1) {
				document = model.getDocument(i);
				entry = model.getEntry(i);
				
				if (document.getNumber() != 0) {
					setX(columns[2]);
					drawText(Integer.toString(document.getNumber()));
					setX(columns[3]);
					drawText(dateFormat.format(document.getDate()));
					
					if (entry.isDebit()) {
						setX(columns[4]);
					}
					else {
						setX(columns[5]);
					}
					
					drawTextRight(numberFormat.format(entry.getAmount()));
				}
				
				setX(columns[6]);
				drawTextRight(numberFormat.format(model.getBalance(i)));
				setX(columns[7]);
				drawText(entry.getDescription());
			}
			else if (model.getType(i) == 2) {
				setX(columns[0]);
				drawText(account.getNumber());
				setX(columns[1]);
				drawText(account.getName());
			}
			else if (model.getType(i) == 3) {
				setX(columns[1]);
				setBoldStyle();
				drawText(model.getDocumentType(i).getName());
				setNormalStyle();
			}
			
			setY(getY() + 13);
		}
	}
}