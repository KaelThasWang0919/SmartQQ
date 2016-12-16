package wang.kaelthas.bean;

public class BarcodeStateBean {
	private int code;
	private String description;
	private String url;
	private String nickName;
	
	

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "BarcodeStateBean [code=" + code + ", description=" + description + ", url=" + url + ", nickName="
				+ nickName + "]";
	}

	

}
