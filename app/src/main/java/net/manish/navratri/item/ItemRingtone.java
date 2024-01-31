package net.manish.navratri.item;

public class ItemRingtone {

	private String id, name, tag, url, download;

	public ItemRingtone(String id, String name, String tag, String url, String download) {
		this.id = id;
		this.name = name;
		this.tag = tag;
		this.url = url;
		this.download = download;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getTag() {
		return tag;
	}

	public String getUrl() {
		return url;
	}

	public String getDownload() {
		return download;
	}
}