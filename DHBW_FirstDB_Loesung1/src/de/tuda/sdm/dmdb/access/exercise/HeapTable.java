package de.tuda.sdm.dmdb.access.exercise;

import de.tuda.sdm.dmdb.storage.AbstractPage;
import de.tuda.sdm.dmdb.storage.AbstractRecord;
import de.tuda.sdm.dmdb.storage.PageManager;
import de.tuda.sdm.dmdb.storage.Record;
import de.tuda.sdm.dmdb.storage.exercise.RowPage;
import de.tuda.sdm.dmdb.storage.types.AbstractSQLValue;
import de.tuda.sdm.dmdb.access.RowIdentifier;
import de.tuda.sdm.dmdb.access.HeapTableBase;

public class HeapTable extends HeapTableBase {

	/**
	 * 
	 * Constructs table from record prototype
	 * @param prototypeRecord
	 */
	public HeapTable(AbstractRecord prototypeRecord) {
		super(prototypeRecord);
	}

	@Override
	public RowIdentifier insert(AbstractRecord record) {
		if (!this.lastPage.recordFitsIntoPage(record)) {
			this.lastPage = PageManager.createDefaultPage(this.prototype.getFixedLength());
			this.addPage(lastPage);
		}
		this.lastPage.insert(record);
		return new RowIdentifier(this.lastPage.getPageNumber(), this.lastPage.getNumRecords()-1);
	}

	@Override
	public AbstractRecord lookup(int pageNumber, int slotNumber) {
		AbstractPage ap = this.getPage(pageNumber);
		AbstractRecord res = this.prototype.clone();
		ap.read(slotNumber, res);	
		return res;
	}
	
}
