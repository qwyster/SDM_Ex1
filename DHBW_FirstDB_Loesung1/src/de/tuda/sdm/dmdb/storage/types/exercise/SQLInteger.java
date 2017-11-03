package de.tuda.sdm.dmdb.storage.types.exercise;

import java.util.Arrays;

import de.tuda.sdm.dmdb.storage.types.SQLIntegerBase;

/**
 * SQL integer value
 * 
 * @author cbinnig
 *
 */
public class SQLInteger extends SQLIntegerBase {

	/**
	 * Constructor with default value
	 */
	public SQLInteger() {
		super();
	}

	/**
	 * Constructor with value
	 * 
	 * @param value
	 *            Integer value
	 */
	public SQLInteger(int value) {
		super(value);
	}

	@Override
	public byte[] serialize() {
		byte[] result = new byte[4];
		result[3] = (byte) this.value;

		result[2] = (byte) (this.value >> 8);

		result[1] = (byte) (this.value >> 16);

		result[0] = (byte) (this.value >> 24);
		// System.out.println(Arrays.toString(result));
		return result;

	}

	@Override
	public void deserialize(byte[] data) {
		int x = (Byte.toUnsignedInt(data[0])) << 24;
		x = x | (Byte.toUnsignedInt(data[1])) << 16;
		x = x | (Byte.toUnsignedInt(data[2])) << 8;
		x = x | (Byte.toUnsignedInt(data[3]));

//		System.out.println(x);
		this.value = x;
	}

	@Override
	public SQLInteger clone() {
		return new SQLInteger(this.value);
	}

}
