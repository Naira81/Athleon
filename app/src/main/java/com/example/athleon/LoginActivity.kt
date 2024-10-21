package com.example.athleon

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.internal.CallbackManagerImpl
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Api
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FacebookAuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.properties.Delegates

class LoginActivity : AppCompatActivity() {
    // Objeto 'companion' para almacenar datos de sesión que se pueden acceder sin crear una instancia de LoginActivity
    companion object {
        lateinit var useremail: String
        lateinit var providerSession: String
    }
    private var email by Delegates.notNull<String>()
    private var password by Delegates.notNull<String>()
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var lyTermms: LinearLayout
    private lateinit var mAuth: FirebaseAuth
    private lateinit var oneTapClient: SignInClient
    private var RESULT_CODE_GOOGLE_SIGN_IN=100
    private val callbackManager= CallbackManager.Factory.create()




    // Método onCreate: Inicializa la actividad cuando se crea
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // Inicializa las vistas y variables
        lyTermms = findViewById(R.id.lyTerms)
        lyTermms.visibility = View.INVISIBLE

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        mAuth = FirebaseAuth.getInstance() // Inicializa FirebaseAuth
        oneTapClient = Identity.getSignInClient(this)


        manageButtonLogin()
        etEmail.doOnTextChanged{text, start, before, count -> manageButtonLogin()}
        etPassword.doOnTextChanged{text, start, before, count -> manageButtonLogin()}
    }
    // Método onStart: Se llama al inicio de la actividad
    public override fun onStart(){
        super.onStart()

        val currentUser = FirebaseAuth.getInstance().currentUser

        // Si el usuario está autenticado, redirige al Home directamente
        if (currentUser !=null){
            goHome(currentUser.email.toString(), currentUser.providerId)
        }
    }

// Sobrescribe el botón de "Atrás" del sistema para ir a la pantalla de inicio
   @SuppressLint("MissingSuperCall")
   override fun onBackPressed() {
       val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags =Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
    }
    private fun manageButtonLogin(){
        var tvLogin= findViewById<TextView>(R.id.tvLogin)
        email=etEmail.text.toString()
        password= etPassword.text.toString()

        if(TextUtils.isEmpty(password) || ValidateEmail.isEmail(email)==false ){
            tvLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
            tvLogin.isEnabled=false
        }
        else{
            tvLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
            tvLogin.isEnabled=true
        }
    }


    // Método para iniciar sesión (llamado desde la vista)
    fun login(view: View) {
        loginUser()
    }
    // Lógica de inicio de sesión
    private fun loginUser() {
        email = etEmail.text.toString()
        password = etPassword.text.toString()

        // Usa FirebaseAuth para iniciar sesión con correo y contraseña
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                // Si la autenticación es exitosa, se redirige al Home
                if (task.isSuccessful) goHome(email, "email")
                else {
                    // Si falla el inicio de sesión, muestra los términos y condiciones
                    if (lyTermms.visibility == View.INVISIBLE) lyTermms.visibility = View.VISIBLE
                    else {
                        var cbAcept = findViewById<CheckBox>(R.id.cbAcept)
                        // Si los términos han sido aceptados, intenta registrar al usuario
                        if (cbAcept.isChecked) register()
                    }
                }
            }
    }
    // Método para redirigir al usuario a la pantalla principal
    private fun goHome(email: String, provider: String) {
        useremail = email
        providerSession = provider

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

    }
    // Método para registrar un nuevo usuario
    private fun register() {
        email = etEmail.text.toString()
        password = etPassword.text.toString()

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    // Si se registra correctamente, guarda la fecha de registro en Firestore
                    var dateRegister= SimpleDateFormat("dd/MM/yyyy").format(Date())
                    var dbRegister= FirebaseFirestore.getInstance()
                    dbRegister.collection("users").document(email).set(hashMapOf(
                        "user" to email,
                        "dateRegister" to dateRegister
                        )
                    )
                    // Redirige al Home después del registro
                    goHome(email, "email")

                } else Toast.makeText(this, "Error, algo ha salido mal: ", Toast.LENGTH_SHORT).show()

            }
    }
    // Método para ir a los términos y condiciones
    fun goTerms(v:View){
        val intent= Intent(this, TermsActivity::class.java)
        startActivity(intent)

    }
    // Método para restablecer la contraseña
    fun forgotPassword(view: View){
        resetPassword()

    }
    // Método que valida si el email ingresado tiene el formato correcto
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Función para resetear la contraseña del usuario, solicitando el envío de un correo electrónico de restablecimiento.
    private fun resetPassword() {
        // Obtiene el texto del campo de email (etEmail) y elimina los espacios al principio y al final.
        val email = etEmail.text.toString().trim()

        // Verifica si el campo de email no está vacío y si es un email válido.
        if (email.isNotEmpty() && isValidEmail(email)) {
            // Llama al método de Firebase Authentication para enviar un correo de restablecimiento de contraseña.
            mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    // Si la tarea fue exitosa, se muestra un mensaje de confirmación al usuario.
                    if (task.isSuccessful) {
                        // El mensaje sugiere que si el correo existe en la base de datos, se enviará un email.
                        Toast.makeText(this, "Si existe una cuenta asociada, se ha enviado un correo a $email", Toast.LENGTH_SHORT).show()
                    } else {
                        // Si la tarea falla, se maneja el error. Aquí se registra el error en el Log y se muestra un mensaje al usuario.
                        task.exception?.let { exception ->
                            // `Log.e` registra el error con un mensaje en la consola Logcat de Android Studio.
                            Log.e("ResetPassword", "Error: ${exception.message}")
                            // Se muestra un mensaje de error al usuario indicando que hubo un problema.
                            Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        } else {
            // Si el campo de email está vacío o no es válido, se muestra un mensaje al usuario.
            Toast.makeText(this, "Indica un email válido", Toast.LENGTH_SHORT).show()
        }
    }

    fun callSignInGoogle (view:View){
        signInGoogle()
    }

    private fun signInGoogle(){
        val gso= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        var googleSignInClient= GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()


        startActivityForResult(googleSignInClient.signInIntent, RESULT_CODE_GOOGLE_SIGN_IN)



    }

    fun callSignInFacebook (view:View){
        signInFacebook()

    }

    private fun signInFacebook(){
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))

        LoginManager.getInstance().registerCallback(callbackManager, object :
            FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult) {
                result.let {
                    val token = it.accessToken
                    val credential = FacebookAuthProvider.getCredential(token.token)
                    mAuth.signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful) {
                            email = it.result.user?.email.toString()
                            goHome(email, "Facebook")

                        } else {
                            showError("Facebook")
                        }

                    }

                }
            }

            override fun onCancel() {}

            override fun onError(error: FacebookException) {
                showError("Facebook")

            }

        })
    }

    private fun showError(provider: String){
        Toast.makeText(this, "Error en la conexion con $provider", Toast.LENGTH_SHORT)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESULT_CODE_GOOGLE_SIGN_IN) {

            try{
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account= task.getResult(ApiException::class.java)!!

                if(account !=null){
                    email=account.email!!
                    val credential= GoogleAuthProvider.getCredential(account.idToken, null )
                    mAuth.signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful)goHome(email, "Google")
                        else showError("Google")
                    }

                }

            } catch (e: ApiException){
                showError("Google")
            }

        }

    }


}