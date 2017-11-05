package de.tuda.sdm.dmdb.storage.exercise;

import de.tuda.sdm.dmdb.storage.AbstractPage;
import de.tuda.sdm.dmdb.storage.AbstractRecord;
import de.tuda.sdm.dmdb.storage.types.AbstractSQLValue;
import de.tuda.sdm.dmdb.storage.types.SQLRowIdentifier;
import de.tuda.sdm.dmdb.storage.types.exercise.SQLInteger;
import de.tuda.sdm.dmdb.storage.types.exercise.SQLVarchar;

public class RowPage extends AbstractPage {

	/**
	 * Constructor for a row page with a given (fixed) slot size
	 * 
	 * @param slotSize
	 */
	public RowPage(int slotSize) {
		super(slotSize);
	}

	@Override
	public void insert(int slotNumber, AbstractRecord record, boolean doInsert) {
		if (doInsert) {
			if (!recordFitsIntoPage(record)) {
				throw new RuntimeException("Record not fit to current page");
			} else {
				// shift right existing records

				// insert new record
			}
		} else { // overwrite existing record
			// fix sized 
			int offsetToFixSizeAttribute = slotNumber * record.getFixedLength();
			
			// var sized
			int offsetToVarSizeAttribute = data.length - HEADER_SIZE - 1 - slotNumber * record.getVariableLength();
		
		}
	}

	@Override
	public int insert(AbstractRecord record) {
		if (!recordFitsIntoPage(record)) {
			throw new RuntimeException("Record not fit to current page");
		} else {
			for (AbstractSQLValue value : record.getValues()) {
				if (value.isFixedLength()) {
					// copy the fixed-length part to begin of page
					System.arraycopy(value.serialize(), 0, this.data, offset, value.getFixedLength());
					offset += value.getFixedLength();
				} else {
					// copy the variable-length part to the end of page
					offsetEnd -= value.getVariableLength();
					System.arraycopy(value.serialize(), 0, this.data, offsetEnd, value.getVariableLength());
				}
			}
			numRecords++;
		}
		return 0;
	}

	@Override
	public void read(int slotNumber, AbstractRecord record) {
		if (slotNumber >= numRecords) {
			throw new RuntimeException("Slot empty!");
		} else {
			int offsetToFixSizeAttribute = slotNumber * record.getFixedLength();
			// int offsetToVarSizeAttribute = offsetEnd + (numRecords - 1 - slotNumber) *
			// record.getVariableLength();
			int offsetToVarSizeAttribute = data.length - HEADER_SIZE - 1 - slotNumber * record.getVariableLength();
			// read columns
			for (AbstractSQLValue val : record.getValues()) {
				byte[] data = null;
				if (val.isFixedLength()) {
					// read fix-sized attribute
					data = new byte[val.getFixedLength()];
					System.arraycopy(this.data, offsetToFixSizeAttribute, data, 0, val.getFixedLength());
					offsetToFixSizeAttribute += val.getFixedLength();
				} else {
					// read variable-sized attribute
					data = new byte[val.getVariableLength()];
					offsetToVarSizeAttribute -= val.getVariableLength();
					System.arraycopy(this.data, offsetToVarSizeAttribute, data, 0, val.getVariableLength());
				}
				val.deserialize(data);
			}
		}
	}
}
