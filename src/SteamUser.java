
public class SteamUser {

	private String nickName;
	private String name;
	private String city;
	private String state;
	
	private SteamUser() {
		
	}
	
	private SteamUser(String nickname, String name, String city, String state) {
		this.nickName = nickname;
		this.name = name;
		this.city = city;
		this.state = state;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	
	
	
}
