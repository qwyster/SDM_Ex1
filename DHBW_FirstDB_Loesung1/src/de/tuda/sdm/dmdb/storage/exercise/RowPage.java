package de.tuda.sdm.dmdb.storage.exercise;

import java.util.HashMap;
import java.util.Vector;

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
	private HashMap<Integer, Vector<Integer>> map= new HashMap<Integer,Vector<Integer>>();
	

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
					if (!map.containsKey(numRecords)) {
						Vector<Integer> v = new Vector<Integer>();
						v.add(offsetEnd);
						v.addElement(value.getVariableLength());
						map.put(numRecords, v);
					}
					else {
						Vector<Integer> v = map.get(numRecords);
						v.add(offsetEnd);
						v.add(value.getVariableLength());
						map.put(numRecords, v);
					}
					
					System.arraycopy(value.serialize(), 0, this.data, offsetEnd, value.getVariableLength());
				}
				
			}
			
			numRecords++;
			
		}
		return numRecords;
	}

	@Override
	public void read(int slotNumber, AbstractRecord record) {
		if (slotNumber >= numRecords || slotNumber < 0) {
			throw new RuntimeException("Slot empty!");
		} else {
			
			int fixedAttSize = 0;
			for (AbstractSQLValue val : record.getValues()) {
				if (val.isFixedLength()) {
					fixedAttSize+=val.getFixedLength();
				}
			}
			
			int offsetToFixSizeAttribute = slotNumber * fixedAttSize;
			Vector<Integer> offsetVector = map.get(slotNumber);
			int count = 0;
			// read columns
			for (AbstractSQLValue val : record.getValues()) {
				byte[] byteData = null;
				if (val.isFixedLength()) {
					// read fix-sized attribute
					byteData = new byte[val.getFixedLength()];
					System.arraycopy(this.data, offsetToFixSizeAttribute, byteData, 0, val.getFixedLength());
					offsetToFixSizeAttribute += val.getFixedLength();
				} else {
					// read variable-sized attribute
					byteData = new byte[offsetVector.get(count+1)];
					System.arraycopy(this.data, offsetVector.get(count), byteData, 0, offsetVector.get(count+1));
					count = count+2;
				}
				val.deserialize(byteData);
			}
		}
	}
}
