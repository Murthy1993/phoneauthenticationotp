package com.example.user.phoneauthenticationotp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit
import android.support.design.widget.Snackbar
import android.widget.Toast
import com.google.android.gms.tasks.TaskExecutors


class MainActivity : AppCompatActivity() {
    lateinit var auth:FirebaseAuth
   lateinit var mVerificationId:String
    lateinit var mcode:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        auth = FirebaseAuth.getInstance()

       button_send_Otp.setOnClickListener {
           Toast.makeText(this@MainActivity," btnSendblock" ,Toast.LENGTH_LONG).show()
           PhoneAuthProvider.getInstance().verifyPhoneNumber(
               "+91"+editText_phone.text.toString(),
               60,
               TimeUnit.SECONDS,
               TaskExecutors.MAIN_THREAD,
               object: PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                   override fun onVerificationCompleted(p0: PhoneAuthCredential?) {

                       var code = p0!!.smsCode
                       if(code!=null){
                           editText_otp.setText(code)
                           Toast.makeText(this@MainActivity," $code" ,Toast.LENGTH_LONG).show()

                           mcode = code
//                           verifyVerificationCode(code);

                       }

                   }

                   override fun onVerificationFailed(p0: FirebaseException?) {
                       Toast.makeText(this@MainActivity,"verification fialed",Toast.LENGTH_SHORT).show();

                   }

                   override fun onCodeSent(p0: String?, p1: PhoneAuthProvider.ForceResendingToken?) {
                       super.onCodeSent(p0, p1)

                       mVerificationId = p0!!
                   }

               }
           )
       }

        button_verify.setOnClickListener {
            Toast.makeText(this," $mcode" ,Toast.LENGTH_LONG).show()

            verifyVerificationCode(mcode);

        }


    }//oncreateprivate

    private fun verifyVerificationCode(code: String?) {
        if(mVerificationId!=null && code!=null){

            var credential = PhoneAuthProvider.getCredential(mVerificationId!!,code!!)
            Toast.makeText(this,"$mVerificationId, $code" ,Toast.LENGTH_LONG).show()

            //signing the user
            signInWithPhoneAuthCredential(credential);
        }else{
            Toast.makeText(this,"Null pointer execp" ,Toast.LENGTH_LONG).show()

        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        if(credential!=null){
            auth!!.signInWithCredential(credential)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        //verification successful we will start the profile activity
                        val intent = Intent(this@MainActivity, ProfileActivity::class.java)
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)

                    } else {

                        //verification unsuccessful.. display an error message

                        var message = "Somthing is wrong, we will fix it soon..."

                        if (it.exception is FirebaseAuthInvalidCredentialsException) {
                            message = "Invalid code entered..."
                        }
                        val snackbar = Snackbar.make(findViewById<View>(R.id.parent), message, Snackbar.LENGTH_LONG)
                        snackbar.setAction("Dismiss", View.OnClickListener { })
                        snackbar.show()
                    }
                }
        }

    }


}
