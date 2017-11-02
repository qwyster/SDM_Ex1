package de.tuda.sdm.dmdb.access.exercise;

import de.tuda.sdm.dmdb.storage.AbstractRecord;
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
		//TODO: implement this method
		return null;
	}

	@Override
	public AbstractRecord lookup(int pageNumber, int slotNumber) {
		//TODO: implement this method
		return null;
	}
	
}
