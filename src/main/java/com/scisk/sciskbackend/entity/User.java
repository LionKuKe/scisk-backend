package com.scisk.sciskbackend.entity;

import java.util.List;

import com.scisk.sciskbackend.util.GlobalParams;
import com.scisk.sciskbackend.util.Util;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(GlobalParams.USER_COLLECTION_NAME)
public class User {
	
	@Id
	private Long id;
	private String firstname;
	private String lastname;
	private String email;
	private String password;
	private String status;
	private String phone1;
	private String phone2;
	private String phone3;
	private String country;
	private String city;
	private String address;
	private Boolean employee;
	
	private List<String> roles;

	public boolean isEmailCorrect() {
		return Util.isEmailCorrect(email);
	}

	public boolean isPassordCorrect() {
		String passwordState = Util.getPasswordState(this.password);
		if (passwordState.equals(Util.PASSWOR_STATE.INVALID.name()) || passwordState.equals(Util.PASSWOR_STATE.WEAK.name())) {
			return false;
		} else {
			return true;
		}
	}
}
