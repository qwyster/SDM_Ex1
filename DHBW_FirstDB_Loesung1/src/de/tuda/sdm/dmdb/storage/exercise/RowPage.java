package de.tuda.sdm.dmdb.storage.exercise;

import de.tuda.sdm.dmdb.storage.AbstractPage;
import de.tuda.sdm.dmdb.storage.AbstractRecord;

public class RowPage extends AbstractPage {

	/**
	 * Constructir for a row page with a given (fixed) slot size
	 * @param slotSize
	 */
	public RowPage(int slotSize) {
		super(slotSize);
	}
	
	@Override
	public void insert(int slotNumber, AbstractRecord record, boolean doInsert) {
		//TODO: implement this method
	}
	
	@Override
	public int insert(AbstractRecord record){
		//TODO: implement this method
		return 0;
	}
	
	@Override
	public void read(int slotNumber, AbstractRecord record){
		//TODO: implement this method
	}
}
