package com.java.redactrix.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.java.redactrix.entity.ConfirmationCode;
import com.java.redactrix.entity.User;
import com.java.redactrix.service.ConfirmationCodeService;
import com.java.redactrix.service.EmailSenderService;
import com.java.redactrix.service.UserServiceImp;

@RestController
public class LogInSignUpController {

	@Autowired
	private UserServiceImp userService;

	@Autowired
	private ConfirmationCodeService confirmationCodeService;

	
	@Autowired
	private EmailSenderService emailSenderService;

	/*
	 * FORM LOG IN
	 */
	@PostMapping("/formLogIn")
	public ResponseEntity<String> formLogIn(@RequestParam(value = "logInValue") List<String> logInValue) {
		System.out.println("CHECK LOG IN");
		Optional<User> existingUser = userService.findUserByEmail(logInValue.get(0));
		if (existingUser.isPresent() == false) {
			System.out.println(" Return Email INCORRECT");
			return ResponseEntity.ok().body("-1");
		} else {
			User getExistingUser = existingUser.get();
			if (logInValue.get(1).equals(getExistingUser.getPassword())) {
				System.out.println(getExistingUser.getUserName());
				return ResponseEntity.ok().body(getExistingUser.getUserName());
			}
		}

		System.out.println(" Return Password INCORRECT");
		return ResponseEntity.ok().body("-1");

	}
	
	
	

	/*
	 * SOCIAL SIGN UP OR SIGN IN
	 */

	@PostMapping("/signUp")
	public ResponseEntity<String> signUp(@RequestParam(value = "email") String email,
			@RequestParam(value = "name") String userName)
			throws InvalidTokenException, GeneralSecurityException, IOException {
		/*
		 * when the user first sign up, we should send a mail to their account also when
		 * user confirms its mail then we save the details of the user to the database
		 */
		Optional<User> existingUser = userService.findUserByEmail(email);
		if (existingUser.isPresent()) {
			System.out.println("Hey user exists");
			return ResponseEntity.ok().body("-1");
		}

		else {
			ConfirmationCode confCode = new ConfirmationCode();
			confCode.setConfCode();
			confCode.setEmailId(email);
			confCode.setName(userName);
			confirmationCodeService.save(confCode);
			SimpleMailMessage mailMessage = new SimpleMailMessage();

			mailMessage.setTo(confCode.getEmailId());
			mailMessage.setSubject("Complete Registration!");
			mailMessage.setFrom("ajit10000000@gmail.com");
			mailMessage.setText("To confirm your account, please click here : " + "http://localhost:4200/confirm/"
					+ confCode.getConfCode());

			emailSenderService.sendEmail(mailMessage);

			return ResponseEntity.ok().body(confCode.getName());
		}

	}
	
	
	

	/*
	 * Form Sign UP
	 */

	// @PostMapping("/formSignUp")
	/*
	 * @RequestMapping(value="/formSignUp", method = RequestMethod.POST) public void
	 * formSignUp(@RequestParam("logInRequest") signUpValue) {
	 * System.out.println(logInRequest); System.out.println("OK form sign Up");
	 * System.out.println(signUpValue); System.out.println(signUpValue.get(1));
	 * Optional<User> existingUser =
	 * userService.findUserByEmail(signUpValue.get(1)); if
	 * (existingUser.isPresent()) { System.out.println("user exists");
	 * System.out.println("-1"); return ResponseEntity.ok().body("-1"); } else {
	 * System.out.println("Add in db User doesnot exist"); /* Since user is not
	 * present in the db Create a token set code, set email, set password, set name
	 * First need to send a verification code to the mail
	 */

	/*
	 * Currently... first saving to the database and sending the mail modifying...
	 * first check whether the mail is sent or not if sent then create those in the
	 * database
	 */
	/*
	 * ConfirmationCode configCode = new ConfirmationCode();
	 * configCode.setConfCode(); System.out.println("info");
	 * System.out.println(signUpValue.get(0));
	 * System.out.println(signUpValue.get(1));
	 * System.out.println(signUpValue.get(2));
	 * configCode.setEmailId(signUpValue.get(1));
	 * configCode.setName(signUpValue.get(0));
	 * configCode.setPassword(signUpValue.get(2));
	 * 
	 * confirmationCodeService.save(configCode); SimpleMailMessage mailMessage = new
	 * SimpleMailMessage();
	 * 
	 * 
	 * mailMessage.setTo(configCode.getEmailId());
	 * mailMessage.setSubject("Complete Registration!");
	 * mailMessage.setFrom("ajit10000000@gmail.com");
	 * mailMessage.setText("To confirm your account, please click here : "
	 * +"http://localhost:4200/confirm/"+configCode.getConfCode());
	 * 
	 * emailSenderService.sendEmail(mailMessage);
	 * 
	 * 
	 * return ResponseEntity.ok().body(configCode.getName()); }
	 * 
	 * }
	 */

	
	
	
	/*
	 * confirmation of the code
	 */

	@RequestMapping(value = "/confirm-account", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<String> confirmUserAccount(@RequestParam("token") String confToken) {
		/*
		 * check the token is in the database or not in confirmation table if its there
		 * then take email, username, name and password and save it in the user database
		 */
		Optional<ConfirmationCode> confCode = confirmationCodeService.findById(confToken);
		if (confCode.isPresent()) {

			System.out.println("Save the details to the database");

			ConfirmationCode getConfCode = confCode.get();
			User newUser = new User();
			newUser.setUserName(getConfCode.getName());
			newUser.setEmail(getConfCode.getEmailId());
			newUser.setPassword(getConfCode.getPassword());
			System.out.println(newUser.getEmail());
			userService.createUser(newUser);
			return ResponseEntity.ok().body(newUser.getUserName());
		} else {
			System.out.println("Invalid configuration code");
			return ResponseEntity.ok().body("-1");
		}

	}

	
	
	
	/*
	 * Forgot Password
	 */
	@PostMapping("/sendResetLink")
	public void sendResetLink(@RequestParam(value = "email") String email) {
		System.out.println(email);
		Optional<User> existingUser = userService.findUserByEmail(email);
		if (existingUser.isPresent()) {
			System.out.println("User is present and ok to reset there password");
			/*
			 * Create a confirmation code pass a link of angular reset form
			 */

			ConfirmationCode confCode = new ConfirmationCode();
			confCode.setConfCode();
			confCode.setEmailId(email);

			SimpleMailMessage mailMessage = new SimpleMailMessage();

			mailMessage.setTo(confCode.getEmailId());
			mailMessage.setSubject("Reset Link!");
			mailMessage.setFrom("ajit10000000@gmail.com");
			mailMessage.setText("To reset your password, please click here : " + "http://localhost:4200/reset/"
					+ confCode.getConfCode());

			emailSenderService.sendEmail(mailMessage);

			confirmationCodeService.save(confCode);

		} else {
			System.out.println("-1");
		}
	}

	
	
	
	/*
	 * resetting the password
	 */
	@RequestMapping(value = "/resetPassword", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<String> resetPassword(@RequestParam("token") String token,
			@RequestParam("password") String password) {

		/*
		 * find the the code row to get email once email is found search in user table
		 * and get the row after getting the row reset its password and save it in the
		 * database
		 */

		Optional<ConfirmationCode> confCode = confirmationCodeService.findById(token);
		if (confCode.isPresent()) {
			ConfirmationCode getConfCode = confCode.get();
			Optional<User> user = userService.findUserByEmail(getConfCode.getEmailId());
			if (user.isPresent()) {
				System.out.println("User is present and ready to update its password in db");
				User getUser = user.get();
				getUser.setPassword(password);
				userService.createUser(getUser);
				return ResponseEntity.ok().body(getUser.getUserName());
			} else {
				System.out.println("-1");
				return ResponseEntity.ok().body("-1");
			}

		} else {
			System.out.println("-1");
			return ResponseEntity.ok().body("-1");
		}
	}

}
