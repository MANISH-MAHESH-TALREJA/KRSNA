package net.manish.navratri.item;

import java.io.Serializable;

public class ItemMessage implements Serializable {

	private String id, message, number;

	public ItemMessage(String id, String message, String number) {
		this.id = id;
		this.message = message;
		this.number = number;
	}

	public String getId() {
		return id;
	}

	public String getMessage() {
		return message;
	}

	public String getNumber()
	{
		return number;
	}
}
