package de.tuda.sdm.dmdb.storage.types.exercise;

import de.tuda.sdm.dmdb.storage.types.SQLIntegerBase;

/**
 * SQL integer value
 * @author cbinnig
 *
 */
public class SQLInteger extends SQLIntegerBase {
	
	/**
	 * Constructor with default value
	 */
	public SQLInteger(){
		super();
	}
	
	/**
	 * Constructor with value
	 * @param value Integer value
	 */
	public SQLInteger(int value){
		super(value);
	}
	
	@Override
	public byte[] serialize() {
		//TODO: implement this method
		return null;
	}

	@Override
	public void deserialize(byte[] data) {
		//TODO: implement this method
		//this.value = ?;
	}
	
	
	@Override
	public SQLInteger clone(){
		return new SQLInteger(this.value);
	}

}
