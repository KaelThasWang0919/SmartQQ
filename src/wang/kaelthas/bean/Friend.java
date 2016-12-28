package wang.kaelthas.bean;

public class Friend {

	private String nickName;//昵称
	private String uin;//临时id
	private String qq;//qq号码
	
	
	
	
	public Friend(String nickName, String uin, String qq) {
		super();
		this.nickName = nickName;
		this.uin = uin;
		this.qq = qq;
	}
	
	public Friend(String nickName, String uin) {
		super();
		this.nickName = nickName;
		this.uin = uin;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getUin() {
		return uin;
	}
	public void setUin(String uin) {
		this.uin = uin;
	}
	public String getQq() {
		return qq;
	}
	public void setQq(String qq) {
		this.qq = qq;
	}

	@Override
	public String toString() {
		return "Friend [nickName=" + nickName + ", uin=" + uin + ", qq=" + qq + "]";
	}
	
	
	
	
	
}
