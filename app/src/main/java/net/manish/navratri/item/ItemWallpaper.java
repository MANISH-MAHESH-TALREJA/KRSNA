package net.manish.navratri.item;

public class ItemWallpaper {
	
	private String id, name, tag, imageBig, imageSmall;

	public ItemWallpaper(String id, String name, String tag, String imageBig, String imageSmall) {
		this.id = id;
		this.name = name;
		this.tag = tag;
		this.imageBig = imageBig;
		this.imageSmall = imageSmall;
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

	public String getImageBig() {
		return imageBig;
	}

	public String getImageSmall() {
		return imageSmall;
	}
}
