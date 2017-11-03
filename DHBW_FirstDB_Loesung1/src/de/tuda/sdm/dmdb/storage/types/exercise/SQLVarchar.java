package de.tuda.sdm.dmdb.storage.types.exercise;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import de.tuda.sdm.dmdb.storage.types.SQLVarcharBase;

/**
 * SQL varchar value
 * @author cbinnig
 *
 */
public class SQLVarchar extends SQLVarcharBase {	
	/**
	 * Constructor with default value and max. length 
	 * @param maxLength
	 */
	public SQLVarchar(int maxLength){
		super(maxLength);

	}
	
	/**
	 * Constructor with string value and max. length 
	 * @param value
	 * @param maxLength
	 */
	public SQLVarchar(String value, int maxLength){
		super(value, maxLength);
	}
	
	@Override
	public byte[] serialize() {
		return this.value.getBytes(StandardCharsets.UTF_8);
	}

	@Override
	public void deserialize(byte[] data) {
		this.value = new String(data);
	}
	
	@Override
	public SQLVarchar clone(){
		return new SQLVarchar(this.value, this.maxLength);
	}

}
