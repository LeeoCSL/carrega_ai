package br.com.carregai.carregaai;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class LoginActivity extends AppCompatActivity {

    private EditText mLogin, mPassword;
    private ProgressDialog mDialog, mDialogFacebook;

    private FirebaseAuth mAuth;

    private LoginButton mLoginFacebook;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mDialog = new ProgressDialog(this);
        mDialogFacebook = new ProgressDialog(this);

        mLogin = (EditText)findViewById(R.id.input_email);
        mPassword = (EditText)findViewById(R.id.input_password);

        mLoginFacebook = (LoginButton)findViewById(R.id.login_fb);
    }

    public void login(View v){

        String email = mLogin.getText().toString();
        String password = mPassword.getText().toString();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            mDialog.setTitle("Entrando");
            mDialog.setMessage("Por favor, espere enquanto fazemos o login.");
            mDialog.show();
            mDialog.setCanceledOnTouchOutside(false);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                mDialog.dismiss();
                                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(mainIntent);
                                finish();
                            }else{
                                mDialog.hide();
                                Toast.makeText(LoginActivity.this, "Ocorreu um erro ao fazer o login. Tente novamente mais tarde.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }else{
            Toast.makeText(LoginActivity.this,  "Algum erro ocorreu. Confira os campos e tente novamente.", Toast.LENGTH_LONG).show();
        }


    }

    public void register(View v){
        startActivity(new Intent(this, RegisterActivity.class));
    }

    public void loginWithFacebook(View view) {
        mCallbackManager = CallbackManager.Factory.create();
        mLoginFacebook.setReadPermissions("email", "public_profile");
        mLoginFacebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAcessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, "Erro: " +error.toString(), Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        });
    }

    private void handleFacebookAcessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mDialogFacebook.setTitle("Verificando credenciais");
        mDialogFacebook.setMessage("Por favor, aguarde enquanto confirmamos seu login.");
        mDialogFacebook.setCanceledOnTouchOutside(true);
        mDialogFacebook.show();

        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        mDialog.dismiss();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mDialog.dismiss();

                        if (e.getClass() == FirebaseAuthUserCollisionException.class) {
                            Toast.makeText(LoginActivity.this, "Essa conta do facebook já esta sendo " +
                                    "usada por outro e-mail.", Toast.LENGTH_LONG).show();
                            LoginManager.getInstance().logOut();
                        } else {
                            Toast.makeText(LoginActivity.this, "Algum erro ocorreu. Tente novamente mais tarde.", Toast.LENGTH_LONG).show();
                        }
                        e.printStackTrace();
                    }
                });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
