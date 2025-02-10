package example.login

import android.content.Intent
import android.os.Bundle
import android.text.method.DigitsKeyListener
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.style.base.BaseActivity
import com.style.entity.UserInfo
import com.style.framework.R
import com.style.framework.databinding.ActivityLoginBinding
import example.home.MainActivity
import missing.namespace.R
import missing.namespace.databinding.ActivityLoginBinding

class LoginActivity : BaseActivity() {

    private val userId: Long = 18
    private lateinit var bd: ActivityLoginBinding
    private lateinit var loginModel: LoginModel

    override fun onCreate(arg0: Bundle?) {
        super.onCreate(arg0)
        bd = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bd.root) 
        setFullScreenStableDarkMode(false)

        val digits = getContext().getString(R.string.digits_password)
        bd.etPassword.keyListener = DigitsKeyListener.getInstance(digits)
        //keyListener与inputType有冲突，先设置的会被覆盖
        //bd.etPassword.inputType = InputType.TYPE_CLASS_TEXT
        loginModel = ViewModelProvider(this).get(LoginModel::class.java)

        loginModel.user.observe(this, object : Observer<UserInfo> {
            override fun onChanged(t: UserInfo?) {

                setUserView(loginModel.user.value!!)
            }
        })
        loginModel.loginState.observe(this, Observer<Boolean> { t ->
            if (t!!) {
                startActivity(Intent(getContext(), MainActivity::class.java))
                finish()
            } else {

            }
        })

        bd.btSignIn.setOnClickListener {}
        bd.btSignIn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                login()
            }
        })
        bd.btSignIn.setOnClickListener { view ->
            view.visibility = View.VISIBLE
            view.visibility = View.VISIBLE
        }
        login()
    }

    fun login() {
        val userId = bd.etAccount.text.toString()
        val password = bd.etPassword.text.toString()
        //mPresenter.login(userId, password);
        loginModel.login()
    }

    fun setUserView(user: UserInfo) {
        bd.etAccount.setText(user.userName)
        bd.etPassword.setText(user.password)
    }

    fun loginSuccess() {

    }

    fun loginFailed() {

    }
}