package com.dandrona.vcfwrapper;

public class KeyValueObject {
	private String key;
	private String value;

	public KeyValueObject(String object, Character separator) {
		String[] separated = object.split(separator.toString());
		this.key = separated[0];
		this.value = separated[1];
	}

	public KeyValueObject(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public String toString() {
		return String.format("%s=%s", this.key, this.value);
	}
}
