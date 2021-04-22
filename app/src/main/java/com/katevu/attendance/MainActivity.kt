package com.katevu.attendance

import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.katevu.attendance.ui.ClassFragment
import com.katevu.attendance.ui.login.LoginFragment
import com.katevu.attendance.utils.WritableTag

class MainActivity : AppCompatActivity(), LoginFragment.Callbacks {
    private val TAG = "MainActivity"

    private var adapter: NfcAdapter? = null
    var tag: WritableTag? = null
    var tagId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val isFragmentContainerEmpty = savedInstanceState == null
        if (isFragmentContainerEmpty) {
            displayFragment()
        }
    }

    fun displayFragment() {
        // Instantiate the fragment.
        val loginFragment: LoginFragment = LoginFragment.newInstance()

        // Get the FragmentManager and start a transaction.
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager
            .beginTransaction()

        // Add the SimpleFragment.
        fragmentTransaction.add(
            R.id.fragment_container,
            loginFragment
        ).addToBackStack(null).commit()
    }

    override fun loginSuccessful() {
        val classFragment = ClassFragment.newInstance(1)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, classFragment)
            .addToBackStack(null)
            .commit()
    }

}