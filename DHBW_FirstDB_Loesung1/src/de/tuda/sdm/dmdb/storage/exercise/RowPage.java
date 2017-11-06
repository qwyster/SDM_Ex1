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

	private HashMap<Integer, Vector<Integer>> map = new HashMap<Integer, Vector<Integer>>(); // storing recordID with
																								// offsets and real
																								// width of each
																								// var-sized attributes

	private int get_size_of_fix_part_of_record(AbstractRecord record) {
		int fixedAttSize = 0;
		for (AbstractSQLValue val : record.getValues()) {
			if (val.isFixedLength()) {
				fixedAttSize += val.getFixedLength();
			}
		}
		return fixedAttSize;
	}

	/**
	 * @param record
	 * @param location
	 * @return a vector containing offsets of the given record
	 */
	private Vector<Integer> copy_record_to_given_location(int slotNumber, AbstractRecord record, int location) {
		int fixedAttSize = get_size_of_fix_part_of_record(record);

		Vector<Integer> new_offset_for_new_record = new Vector<Integer>();
		for (int i = record.getValues().length - 1; i >= 0; i--) {
			if (!record.getValue(i).isFixedLength()) {
				System.arraycopy(record.getValue(i).serialize(), 0, this.data, location,
						record.getValue(i).getVariableLength());
				new_offset_for_new_record.insertElementAt(location, 0);
				new_offset_for_new_record.insertElementAt(record.getValue(i).getVariableLength(), 1);
				location += record.getValue(i).getVariableLength();
			} else {
				System.arraycopy(record.getValue(i).serialize(), 0, this.data,
						slotNumber * fixedAttSize + fixedAttSize - record.getValue(i).getFixedLength(),
						record.getValue(i).getFixedLength());
			}
		}
		return new_offset_for_new_record;
	}

	@Override
	public void insert(int slotNumber, AbstractRecord record, boolean doInsert) {
		if (doInsert) {
			if (!recordFitsIntoPage(record)) {
				throw new RuntimeException("Record not fit to current page");
			} else {
				// make place for the fix part
				// get the sum of the width of the fix sized attributes
				int fixedAttSize = get_size_of_fix_part_of_record(record);
				// shift right the fixed part of the existing records
				System.arraycopy(this.data, slotNumber * fixedAttSize, this.data, (slotNumber + 1) * fixedAttSize,
						fixedAttSize);
				offset += fixedAttSize;

				// make place for the variable part
				// shift left the other records from slotnr +1
				int offset_to_slot_w_slotnr;
				if (slotNumber >= 1) {
					Vector<Integer> vec = map.get(slotNumber - 1);
					offset_to_slot_w_slotnr = vec.get(vec.size() - 2);
				} else {
					offset_to_slot_w_slotnr = data.length - HEADER_SIZE;
				}
				System.arraycopy(this.data, offsetEnd, this.data, offsetEnd - record.getVariableLength(),
						offset_to_slot_w_slotnr - offsetEnd + 1);
				offsetEnd -= record.getVariableLength();

				// update hashmap
				for (int i = numRecords - 1; i >= slotNumber; i--) {
					Vector<Integer> v = map.get(i);
					for (int j = 0; j < v.size(); j = j + 2) {
						v.set(j, v.get(j) - record.getVariableLength());
					}
					map.put(i + 1, v);
				}
				// insert new record
				Vector<Integer> new_offset_for_new_record = copy_record_to_given_location(slotNumber, record,
						offset_to_slot_w_slotnr - record.getVariableLength());
				// update hashmap
				map.put(slotNumber, new_offset_for_new_record);
				numRecords++;
			}
		} else { // overwrite existing record
			// make place for the variable part of the new record
			int newVariablePartLength = record.getVariableLength();
			// extract the variable part's length of the old record
			int oldVariablePartLength = 0;
			Vector<Integer> v = map.get(slotNumber);
			for (int i = 1; i < v.size(); i = i + 2) {
				oldVariablePartLength += v.get(i);
			}

			int offset2 = v.get(v.size() - 2); // offset to the variable part of old record
			int new_offset2 = offset2; // update offset pointing to the new position, from which the new record
										// fits in

			if (newVariablePartLength >= oldVariablePartLength) {
				// update the hash map for records w. rid > slotnumber
				for (int i = slotNumber + 1; i < numRecords; i++) {
					Vector<Integer> vec = map.get(i);
					for (int j = 0; j < vec.size(); j = j + 2) {
						vec.set(j, vec.get(j) - (newVariablePartLength - oldVariablePartLength));
					}
					map.put(i, vec);
				}
				// shift left the other records
				System.arraycopy(this.data, offsetEnd, this.data,
						offsetEnd - (newVariablePartLength - oldVariablePartLength), offset2 - offsetEnd + 1);
				new_offset2 -= newVariablePartLength - oldVariablePartLength;
			} else {
				// update the hash map for records w. rid > slotnumber
				for (int i = slotNumber + 1; i < numRecords; i++) {
					Vector<Integer> vec = map.get(i);
					for (int j = 0; j < vec.size(); j = j + 2) {
						vec.set(j, vec.get(j) + (oldVariablePartLength - newVariablePartLength));
					}
					map.put(i, vec);
				}
				// shift right the other records
				System.arraycopy(this.data, offsetEnd, this.data,
						offsetEnd + (oldVariablePartLength - newVariablePartLength), offset2 - offsetEnd + 1);
				new_offset2 += oldVariablePartLength - newVariablePartLength;
			}

			// get the sum of the width of the fix sized attributes
			Vector<Integer> new_offset_for_new_record = copy_record_to_given_location(slotNumber, record, new_offset2);
			// update the hash map entry for new inserted record
			map.put(slotNumber, new_offset_for_new_record);
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
						v.add(value.getVariableLength());
						map.put(numRecords, v);
					} else {
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

			int fixedAttSize = get_size_of_fix_part_of_record(record);

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
					byteData = new byte[offsetVector.get(count + 1)];
					System.arraycopy(this.data, offsetVector.get(count), byteData, 0, offsetVector.get(count + 1));
					count = count + 2;
				}
				val.deserialize(byteData);
			}
		}
	}
}
