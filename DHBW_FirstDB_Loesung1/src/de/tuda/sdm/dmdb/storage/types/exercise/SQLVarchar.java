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
		byte[] result = new byte[4];
		result = this.value.getBytes(StandardCharsets.UTF_8); 
		System.out.println(Arrays.toString(result));
		return result;
	}

	@Override
	public void deserialize(byte[] data) {
		String s = new String(data);
		this.value = s;
	}
	
	@Override
	public SQLVarchar clone(){
		return new SQLVarchar(this.value, this.maxLength);
	}

}
