package net.manish.navratri.item;

public class ItemWallpaper {
	
	private String id, name, tag, imageBig, imageSmall, layout;

	public ItemWallpaper(String id, String name, String tag, String imageBig, String imageSmall, String layout) {
		this.id = id;
		this.name = name;
		this.tag = tag;
		this.imageBig = imageBig;
		this.imageSmall = imageSmall;
		this.layout = layout;
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

	public String getLayout()
	{
		return layout;
	}
}
