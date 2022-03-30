package message;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

// Handle unmarshalling of incoming messages
// Convert from byte array to object type
public class ByteUnpacker {
	private ArrayList<String> properties;
	private HashMap <String, TYPE> propToValue;

	public ByteUnpacker(){
		properties = new ArrayList<>();
		propToValue = new HashMap<>();
	}
	
	public ByteUnpacker defineComponents(ByteUnpacker unpacker){
		if(unpacker!=null){
			properties.addAll(unpacker.properties);
			propToValue.putAll(unpacker.propToValue);
		}
		return this;
	}

	// Identify type of each property and build a new hashmap with fields and actual values
	public UnpackedMsg parseByteArray(byte[] data){
		int offset = 0;
		HashMap<String, Object> map = new HashMap<>();
		try{
			for(String property: properties){
				TYPE value = propToValue.get(property);
				switch(value){
				case INTEGER:
					map.put(property, parseInt(data, offset));
					offset+=4;
					break;
				case DOUBLE:
					map.put(property, parseDouble(data,offset));
					offset+=8;
					break;
				case STRING:
					int length = parseInt(data, offset);
                    map.put(property, parseString(data, offset + 4, length));
                    offset += 4 + length;
                    break;
				case BYTE_ARRAY:
					int byte_length = parseInt(data, offset);
                    map.put(property, Arrays.copyOfRange(data, offset + 4, offset + 4 + byte_length));
					break;
				case ONE_BYTE_INT:
					map.put(property, new OneByteInt(data[offset] & 0xFF));
                    offset += 1;
                    break;
				}
			}
			UnpackedMsg result = new UnpackedMsg(map);
			return result;
		}catch(Exception e){
			return null;
		}
	}
	
	// Convert byte array to string
	private String parseString(byte[] data, int offset, int length) {
		try{
			StringBuilder sb = new StringBuilder();
			for(int i=0;i<length;i++,offset++){
				sb.append((char)data[offset]);
			}
			return sb.toString();
		}catch(IndexOutOfBoundsException e){
			return null;
		}
		
	}
	
	// Convert byte array to double
	private Double parseDouble(byte[] data, int offset) {
		int doubleSize = 8;
		byte[] temp = new byte[doubleSize];
		for(int i =0;i<doubleSize;i++){
			temp[i] = data[offset+i];
		}
		double value = ByteBuffer.wrap(temp).getDouble();
		return value;
	}
	
	// Convert byte array to integer
	private Integer parseInt(byte[] data, int offset) {
		int intSize = 4;
		byte[] temp = new byte[intSize];
		for(int i=0;i<intSize;i++){
			temp[i] = data[offset+i];
		}
		
		int value = ByteBuffer.wrap(temp).getInt();
		return value;
	}

	
	// Retrieve contents of a message using hashmap
	public static class UnpackedMsg{
		private HashMap<String, Object> map;

		public UnpackedMsg(HashMap<String,Object> map){
			this.map = map;
		}
		
		public Integer getInteger(String key){
			if(map.containsKey(key) && (map.get(key) instanceof Integer)){
				return (Integer) map.get(key);
			}
			return null;
		}
		public String getString(String key){
			if(map.containsKey(key) && map.get(key) instanceof String){
				return (String) map.get(key);
			}
			return null;
		}
		public Double getDouble(String key){
			if(map.containsKey(key) && map.get(key) instanceof Double){
				return (Double) map.get(key);
			}
			return null;
		}
		public byte[] getByteArray(String value) {
            if (map.containsKey(value) && map.get(value) instanceof byte[]) {
                return (byte[]) map.get(value);
            }
            return null;
        }

        public OneByteInt getOneByteInt(String value) {
            if (map.containsKey(value) && map.get(value) instanceof OneByteInt) {
                return (OneByteInt) map.get(value);
            }
            return null;
        }
	}
	
	
	
	
	public enum TYPE {
        INTEGER, DOUBLE, STRING, BYTE_ARRAY, ONE_BYTE_INT
    }
	
	public static class Builder{
		private ByteUnpacker unpacker;
		public Builder(){
			unpacker = new ByteUnpacker();
			
		}
		public Builder setType(String property, TYPE type){
			unpacker.properties.add(property);
			unpacker.propToValue.put(property, type);
			return this;
		}
		public ByteUnpacker build(){
			return unpacker;
		}
	}
}
