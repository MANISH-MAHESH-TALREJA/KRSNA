package net.manish.navratri.item;

public class ItemVideos
{

	final String videoId;
    final String videoType;
	final String videoTitle;
	final String videoURL;
	final String videoImage;
	final String videoLayout;

	public ItemVideos(String videoId, String videoType, String videoTitle, String videoURL, String videoImage, String videoLayout) {
		this.videoId = videoId;
		this.videoType = videoType;
		this.videoTitle = videoTitle;
		this.videoURL = videoURL;
		this.videoImage = videoImage;
		this.videoLayout = videoLayout;
	}

	public String getVideoId()
	{
		return videoId;
	}

	public String getVideoType()
	{
		return videoType;
	}

	public String getVideoTitle()
	{
		return videoTitle;
	}

	public String getVideoURL()
	{
		return videoURL;
	}

	public String getVideoImage()
	{
		return videoImage;
	}

	public String getVideoLayout()
	{
		return videoLayout;
	}
}