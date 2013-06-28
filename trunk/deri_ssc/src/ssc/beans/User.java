package ssc.beans;

public class User {
	private String id;
	private String nickname;
	private String username;//email
	private String pass;//>6
	private String email;
	
	public User() {
		super();
	}
	
	public User(String nickname, String username, String password) {
		this();
		this.nickname = nickname;
		this.username = username;
		this.pass = password;
	}
	public String getId() {
		return id;
	}
	@SuppressWarnings("unused")
	public void setId(String id){
		this.id = id;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	
	
}
