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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText mLogin, mPassword;
    private ProgressDialog mDialog;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mDialog = new ProgressDialog(this);

        mLogin = (EditText)findViewById(R.id.input_email);
        mPassword = (EditText)findViewById(R.id.input_password);
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
}
