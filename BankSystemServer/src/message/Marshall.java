package message;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import message.Marshall.Builder;

// Class to perform marshaling
// Convert objects to bytes and then store in byte array
public class Marshall {
	private ArrayList<String> properties;
	private HashMap<String, Object> propToValue;

	public Marshall() {
		properties = new ArrayList<>();
		propToValue = new HashMap<>();

	}

	// Add message field to list of fields, and put message field and value in
	// hashmap
	public void setValue(String key, Object value) {
		properties.add(key);
		propToValue.put(key, value);
	}

	// Return lookup value from key in hashmap
	public HashMap<String, Object> getPropToValue() {
		return this.propToValue;
	}

	public Object getValue(String key) {
		return propToValue.get(key);
	}

	// Converts objects in hashmap into bytes
	public byte[] getByteArray() {
		/*
		 * Calculate the size required for the byte array based on the type of the
		 * object
		 * Integer 4 bytes
		 * String 4 + length of string bytes (+4 in front is to store the length of the
		 * string)
		 * Long 8 bytes
		 * Byte Array 4 + length of byte array
		 * float 4 bytes
		 * double 8 bytes
		 * OneByteInt 1 byte
		 */
		int size = 1;
		for (Object value : propToValue.values()) {
			if (value instanceof Integer) {
				size += 4;
			} else if (value instanceof byte[]) {
				size += 4 + ((byte[]) value).length;
			} else if (value instanceof String) {
				size += 4 + ((String) value).length();
			} else if (value instanceof Double) {
				size += 8;
			} else if (value instanceof OneByteInt) {
				size += 1;
			}
		}
		byte[] buffer = new byte[size];

		int index = 0;
		for (String property : properties) {
			Object value = propToValue.get(property);
			if (value instanceof Integer) {
				index = intToByte((Integer) value, buffer, index);
			} else if (value instanceof String) {
				index = intToByte(((String) value).length(), buffer, index);
				index = stringToByte(((String) value), buffer, index);
			} else if (value instanceof Double) {
				index = doubleToByte((Double) value, buffer, index);
			} else if (value instanceof byte[]) {
				index = intToByte(((byte[]) value).length, buffer, index);
				System.arraycopy(value, 0, buffer, index, ((byte[]) value).length);
				index += ((byte[]) value).length;
			} else if (value instanceof OneByteInt) {
				int OBIValue = ((OneByteInt) value).getValue();
				buffer[index++] = (byte) (OBIValue & 0xFF);
			}

		}

		return buffer;
	}

	// Integer to byte array : 4 bytes => 4 slots in buffer
	private int intToByte(int i, byte[] buffer, int index) {
		byte[] temp = new byte[4];
		ByteBuffer.wrap(temp).putInt(i);
		for (byte b : temp) {
			buffer[index++] = b;
		}

		return index;
	}

	// Double to byte array i.e. 8 bytes => 8 slots in buffer
	private int doubleToByte(Double d, byte[] buffer, int index) {
		byte[] temp = new byte[8];
		ByteBuffer.wrap(temp).putDouble(d);
		for (byte b : temp) {
			buffer[index++] = b;
		}
		return index;
	}

	// String (made up of characters) to byte array - 1 character 1 slot in buffer
	private int stringToByte(String s, byte[] buffer, int index) {
		for (byte b : s.getBytes()) {
			buffer[index++] = b;
		}
		return index;
	}

	public static class Builder {
		private Marshall packer;

		public Builder() {
			packer = new Marshall();
		}

		public Builder setProperty(String key, int value) {
			return set(key, value);
		}

		public Builder set(String key, Object value) {
			packer.setValue(key, value);
			return this;
		}

		public Builder setProperty(String key, double value) {
			return set(key, value);
		}

		public Builder setProperty(String key, String string) {
			return set(key, string);
		}

		public Builder setProperty(String key, OneByteInt value) {
			return set(key, value);
		}

		public Marshall build() {
			return packer;
		}
	}
}
