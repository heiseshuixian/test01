package just.lost.name;

import java.io.Serializable;

public class ViewNewSelectBean implements Serializable {
	private static final long serialVersionUID = -5102109576617005077L;
	/** 视频id */
	private long _id;
	/** 视频名字 */
	private String name;
	/** 视频路径 */
	private String path;
	/** 视频时长 */
	private long duration;
	/** 视频宽 */
	private int width;
	/** 视频高 */
	private int height;

	public long get_id() {
		return _id;
	}

	public void set_id(long _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

}
