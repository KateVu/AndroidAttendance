package com.katevu.attendance

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.*
import android.nfc.tech.*
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.katevu.attendance.data.PrefRepo
import com.katevu.attendance.data.model.Attendance
import com.katevu.attendance.data.model.Auth
import com.katevu.attendance.utils.NfcUtils
import com.katevu.attendance.utils.WritableTag
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*
import kotlin.experimental.and

class CheckinActivity : AppCompatActivity() {

    private val TAG = "CheckinActivity"

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val checkinActivityViewModel: CheckinActivityViewModel by viewModels()
    private val prefRepository by lazy { PrefRepo(this) }

    //init for NFC
    private var adapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    private var intentFiltersArray: Array<IntentFilter> = arrayOf()
    private var techListsArray: Array<Array<String>> = arrayOf(arrayOf())
    var tag: WritableTag? = null
    var tagId: String? = null

    //init for submit data
    var logginUser: Auth? = null;
    var isValid: Boolean = false;
    var result: Boolean = false;
    private var callbacks: Callbacks? = null

    interface Callbacks {
        fun submitSuccessful();
        fun submitFailure()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkin)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

//        callbacks = applicationContext as Callbacks?


        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
                setOf(
                        R.id.nav_home, R.id.nav_slideshow
                ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //check login first

        logginUser = prefRepository.getLogginUser();

        if (logginUser != null) {
            val cal: Calendar = Calendar.getInstance();
            isValid = cal.timeInMillis < logginUser!!.expiredDate!!
            if (!isValid) {
                prefRepository.clearData()
            }
        }

        if (!isValid) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            showToast("Please log in first")
        };

        checkinActivityViewModel.checkinResult.observe(this, androidx.lifecycle.Observer {
            checkinResult -> result = checkinResult;

        })

//        chec.loginResult.observe(viewLifecycleOwner,
//                Observer { loginResult ->


                initNfcAdapter()

        val intent = Intent(this, javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)


        val ndef = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED).apply {
            try {
                addDataType("*/*")    /* Handles all MIME based dispatches.
                                 You should specify only the ones that you need. */
            } catch (e: IntentFilter.MalformedMimeTypeException) {
                throw RuntimeException("fail", e)
            }
        }

        intentFiltersArray = arrayOf(ndef)
        techListsArray = arrayOf(
                arrayOf<String>(NfcA::class.java.name),
                arrayOf<String>(NfcB::class.java.name),
                arrayOf<String>(IsoDep::class.java.name),
                arrayOf<String>(MifareClassic::class.java.name),
                arrayOf<String>(NfcV::class.java.name),
                arrayOf<String>(NfcF::class.java.name),
                arrayOf<String>(NdefFormatable::class.java.name),
                arrayOf<String>(MifareUltralight::class.java.name),
        )

    }

    override fun onDestroy() {
        super.onDestroy()
        callbacks = null

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.checkin, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_logout -> {
            // User chose the "Settings" item, show the app settings UI...
            prefRepository.clearData()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        adapter?.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray)
    }

    override fun onPause() {
        disableNfcForegroundDispatch()
        super.onPause()
    }
    private fun disableNfcForegroundDispatch() {
        try {
            adapter?.disableForegroundDispatch(this)
        } catch (ex: IllegalStateException) {
            Log.e(TAG, "Error disabling NFC foreground dispatch", ex)
        }
    }


    //Fun to handle
    private fun initNfcAdapter() {
        val nfcManager = getSystemService(Context.NFC_SERVICE) as NfcManager
        adapter = nfcManager.defaultAdapter
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)


        handleIntent(intent)
    }

    fun handleIntentTest(intent: Intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            tagId = "ACTION_NDEF_DISCOVERED"
            onTagTapped(tagId.toString(), "test")
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
            tagId = "ACTION_TECH_DISCOVERED"
            onTagTapped(tagId.toString(), "test")
        } else if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
            tagId = "ACTION_TAG_DISCOVERED"
            onTagTapped(tagId.toString(), "test")
        } else {
            tagId = "ERROR"
            onTagTapped(tagId.toString(), "test")
        }
    }

    fun handleIntent(intent: Intent?) {

        if (intent != null) {
            val tagFromIntent = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            Log.d(TAG, "new card")
            try {
                tag = tagFromIntent?.let { WritableTag(it) }
            } catch (e: FormatException) {
                Log.e(TAG, "Unsupported tag tapped", e)
                return
            }
            tagId = tag!!.tagId
            Log.d(TAG, "$tagId")
//            showToast("Tag tapped: $tagId")

            val action = intent.action


            if (NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
                val MIME_TEXT_PLAIN = "text/plain"
                val type = intent.type
                if (MIME_TEXT_PLAIN == type) {

                    val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)

                    val ndef = Ndef.get(tag) // NDEF is not supported by this Tag.
                    var payload: String? = ""

                    if (ndef != null) {
                        val ndefMessage = ndef.cachedNdefMessage

                        val records = ndefMessage.records


                        for (ndefRecord in records) {
                            if (ndefRecord.tnf == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.type, NdefRecord.RTD_TEXT)) {
                                try {
                                    payload = readText(ndefRecord)
                                } catch (e: UnsupportedEncodingException) {
                                    Log.e("Bug", "Unsupported Encoding", e)
                                }

                            }
                        }
                    }

                    if (payload != null) {
                        onTagTapped(NfcUtils.getUID(intent), payload)

                        if (logginUser!= null && logginUser!!.token != null) {
                            var attendance = Attendance(
                                    logginUser!!.token!!,
                                    logginUser!!.userID,
                                    NfcUtils.getUID(intent),
                                    Calendar.getInstance().timeInMillis.toString()
                            );

                            val url: String = "https://recordattendance-1fa08-default-rtdb.firebaseio.com/orders/${logginUser!!.userID}.json?auth=${logginUser!!.token}";

                            Log.d(TAG, "requestURL: $url")
                            Log.d(TAG, "attendance: $attendance")


                            checkinActivityViewModel.checkin(url,attendance)

                            if (result) {
                                Log.d(TAG, "Check in result: $result")
                                callbacks?.submitSuccessful()
                            } else {
                                callbacks?.submitFailure()
                            }
                        }


                    }
                } else {
                    Log.d("Bug", "Wrong mime type: " + type)
                }
            } else if (NfcAdapter.ACTION_TECH_DISCOVERED == action) {
                tagId = "ACTION_TECH_DISCOVERED"
                onTagTapped(tagId.toString(), "test")

            }

//            if (NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
//                val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
//                if (rawMsgs != null) {
//                    onTagTapped(NfcUtils.getUID(intent), NfcUtils.getData(rawMsgs))
//                }
//            }
        }
    }

    fun readText(record: NdefRecord): String {
        /*
     * See NFC forum specification for "Text Record Type Definition" at 3.2.1
     *
     * http://www.nfc-forum.org/specs/
     *
     * bit_7 defines encoding
     * bit_6 reserved for future use, must be 0
     * bit_5..0 length of IANA language code
     */

        val payload = record.payload

        // Get the Text Encoding
        val textEncoding = "UTF-8"

        // Get the Language Code
        val languageCodeLength = payload[0] and 51

        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
        // e.g. "en"

        // Get the Text
        return String(payload, languageCodeLength + 1, payload.size - languageCodeLength - 1, Charset.defaultCharset())
    }


    private fun onTagTapped(superTagId: String, superTagData: String) {
        showToast("id: ${superTagId} and data: ${superTagData}")
    }


}