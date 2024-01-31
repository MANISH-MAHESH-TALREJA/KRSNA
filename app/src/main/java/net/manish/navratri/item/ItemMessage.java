package net.manish.navratri.item;

import java.io.Serializable;

public class ItemMessage implements Serializable {

	private String id, message;

	public ItemMessage(String id, String message) {
		this.id = id;
		this.message = message;
	}

	public String getId() {
		return id;
	}

	public String getMessage() {
		return message;
	}
}
