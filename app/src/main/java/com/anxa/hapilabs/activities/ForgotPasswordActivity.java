package com.anxa.hapilabs.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hapilabs.R;
import com.anxa.hapilabs.common.connection.listener.ForgotPasswordListener;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.controllers.forgotpass.ForgotPassController;
import com.anxa.hapilabs.models.MessageObj;

public class ForgotPasswordActivity extends HAPIActivity implements ForgotPasswordListener {

	Button submit_btn;
	EditText email_et;
	ForgotPassController forgotPassController;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.forgotpassword);

		submit_btn = (Button) findViewById(R.id.footerbutton);
		submit_btn.setText(R.string.btn_submit);
		submit_btn.setOnClickListener(this);

		email_et = (EditText) findViewById(R.id.email_et);

	}

	@Override
	public void onClick(View v) {

		if (v == submit_btn) {
			String email = email_et.getText().toString();
			if (email == null || email.equals("")) {
				displayToastMessage(getResources().getString(
						R.string.ALERTMESSAGE_FORGOTPASSWORD));
			} else if (!AppUtil.isEmail(email)) {
				displayToastMessage(getResources().getString(
						R.string.SIGNUP_EMAIL_ERROR));
			}

			else {
				processPasswordReset(email);
			}
		}

	}

	private void processPasswordReset(String email) {

		if (forgotPassController == null) {
			forgotPassController = new ForgotPassController(this, this, this);

//			displayToastMessage(getResources().getString(R.string.ONETOONECOACHING_PASSWORD_EMAIL));
		}
		startProgress();
		forgotPassController.forgotPasswordStart(email.trim(), "");
	}

	@Override
	public void forgotPasswordSuccess(String message){
		displayToastMessage(getResources().getString(R.string.ONETOONECOACHING_PASSWORD_EMAIL));
	}

	@Override
	public void forgotPasswordFailedWithError(MessageObj response){
		if(response.getMessage_string().equalsIgnoreCase("User does not exist.")) {
			displayToastMessage(getResources().getString(R.string.ONETOONECOACHING_PASSWORD_EMAIL_FAILED));
		}else{
			displayToastMessage(response.getMessage_string());
		}
	}

	public void goBackToPreviousPage(View view){
		finish();
	}

}
