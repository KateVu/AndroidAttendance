package com.katevu.attendance

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.nfc.*
import android.nfc.tech.*
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.katevu.attendance.data.PrefRepo
import com.katevu.attendance.data.model.Attendance
import com.katevu.attendance.data.model.LoggedInUser
import com.katevu.attendance.data.model.StudentActivity
import com.katevu.attendance.ui.checkinresult.CheckinFailureFragment
import com.katevu.attendance.ui.checkinresult.CheckinSuccessFragment
import com.katevu.attendance.ui.classes.TodayClassFragment
import com.katevu.attendance.utils.NfcUtils
import com.katevu.attendance.utils.NfcTag
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.experimental.and


class CheckinActivity : AppCompatActivity(), CheckinSuccessFragment.Callbacks,
        CheckinFailureFragment.Callbacks {

    private val TAG = "CheckinActivity"

    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

    private val checkinActivityViewModel: CheckinActivityViewModel by viewModels()
    private val prefRepository by lazy { PrefRepo(this) }

    //init for NFC
    private var adapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    private var spinner: ProgressBar? = null
    private var intentFiltersArray: Array<IntentFilter> = arrayOf()
    private var techListsArray: Array<Array<String>> = arrayOf(arrayOf())
    var tag: NfcTag? = null
    var tagId: String? = null
    var phoneId: String? = ""

//    private var telephonyManager: TelephonyManager? = null
    private var telephoneId: String? = null


    //init for submit data
    var logginUser: LoggedInUser? = null;
    var isValid: Boolean = false;
    var result: Boolean = false;
    var activity: StudentActivity? = null
    var errorMessage: String = "Cannot connect. Please try again"
    var activeNetwork: NetworkInfo? = null
    var isConnected: Boolean = false;

    //list activity
    private var _listActivites: List<StudentActivity>? = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkin)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        spinner = findViewById<ProgressBar>(R.id.spinner)
        setSupportActionBar(toolbar)

        val isFragmentContainerEmpty = savedInstanceState == null
        if (isFragmentContainerEmpty) {
            insertTodayClassesFragment()
        }

        //check login first

        logginUser = prefRepository.getLogginUser();

        if (logginUser != null) {
            isValid = true;
//            val cal: Calendar = Calendar.getInstance();
//            isValid = cal.timeInMillis < logginUser!!.expiredDate!!
//            if (!isValid) {
//                prefRepository.clearData()
//            }
        }

        if (!isValid) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            showToast("Please log in first")
        };

        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        activeNetwork = cm.activeNetworkInfo
        isConnected = activeNetwork?.isConnectedOrConnecting == true
//        telephonyManager = this.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
//        var phoneId: String? = ""

        phoneId = "Android Id: " + getIMEIDeviceId(this)
//        showToast("IMEI: $phoneId")
        Log.d(TAG, "ANDROI ID: $phoneId")

        checkinActivityViewModel.checkinResult1.observe(this, androidx.lifecycle.Observer { checkinResult ->
            spinner?.visibility = View.GONE
            checkinResult.error?.let {
                insertFailureFragment(errorMessage)
            }
            checkinResult.success?.let {
                if (this.activity != null) {
                    insertSuccessFragment("Location: " + this.activity!!.roomId, "Check in at: " + it.date)
                } else {
                    insertSuccessFragment(this.activity!!.roomId, it.date)
                }

            }
        })

        checkinActivityViewModel.getActivitiesResult.observe(
                this,
                { getActivityResult ->
                    spinner?.visibility = View.GONE
                    getActivityResult.success?.let {
                        _listActivites = it.data
                    }
                }
        )

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

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.checkin, menu)
        return true
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

    // Embeds the child fragment dynamically
    private fun insertSuccessFragment(location: String, date: String) {
        val childFragment =
                CheckinSuccessFragment.newInstance(location, date)

        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_checkin, childFragment)
                .commit()
    }

    private fun insertFailureFragment(errorMessage: String) {
        val childFragment = CheckinFailureFragment.newInstance(errorMessage)

        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_checkin, childFragment)
                .commit()
    }

    private fun insertTodayClassesFragment() {
        val childFragment = TodayClassFragment.newInstance(1)
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_checkin, childFragment)
                .commit()
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

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun handleIntent(intent: Intent?) {

        if (intent != null) {
            val tagFromIntent = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            Log.d(TAG, "new card")
            try {
                tag = tagFromIntent?.let { NfcTag(it) }
            } catch (e: FormatException) {
                Log.e(TAG, "Unsupported tag tapped", e)
                return
            }
            tagId = tag!!.tagId
//            Log.d(TAG, "$tagId")
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
                            if (ndefRecord.tnf == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(
                                            ndefRecord.type,
                                            NdefRecord.RTD_TEXT
                                    )
                            ) {
                                try {
                                    payload = readText(ndefRecord)
                                } catch (e: UnsupportedEncodingException) {
                                    Log.e("Bug", "Unsupported Encoding", e)
                                }

                            }
                        }
                    }

                    if (payload != null) {
//                        onTagTapped(NfcUtils.getUID(intent), payload)
                        var nfcId = NfcUtils.getUID(intent)

                        if (logginUser != null && logginUser!!.token != null) {

//                            "startTime": "2021-10-05T12:48:00.000Z",
                            this.activity = getActivityId(nfcId)

//                            this.activity = getActivityId("enab101nfc")

                            if (activity == null) {
                                insertFailureFragment("Please check your location and time again!")
                            } else {
                                val date = LocalDateTime.now()
                                val text = date.format(formatter)
                                val parsedDate = LocalDateTime.parse(text, formatter)

                                phoneId = "Android Id: " + getIMEIDeviceId(this)
                                var attendance = Attendance(
                                        logginUser!!.data.studentID,
                                        text,
//                                Calendar.getInstance().timeInMillis.toString(),
                                        NfcUtils.getUID(intent),
                                        this.activity!!.id,
                                        phoneId!!
                                );

                                val url: String = "https://mobile-attendance-recorder.herokuapp.com/api/v1/checkIn";
                                val token: String = logginUser!!.token;

//                                Log.d(TAG, "requestURL: $url")
//                                Log.d(TAG, "attendance: $attendance")

//                            getActivityId(nfcId)
                                if (isConnected) {
                                    spinner?.visibility = View.VISIBLE
                                    checkinActivityViewModel.checkin(url, token, attendance)

//                                Log.d(TAG, "result in Activity: $result")

                                    val test = checkinActivityViewModel.result

//                                Log.d(TAG, "test result in Activity: $test")

                                } else {
                                    showToast("No internet access!!")
                                }
                            }
                        }


                    }
                } else {
                    Log.d("Bug", "Wrong mime type: " + type)
                }
            } else if (NfcAdapter.ACTION_TECH_DISCOVERED == action) {
                tagId = "ACTION_TECH_DISCOVERED"
//                onTagTapped(tagId.toString(), "test")

            }

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
        return String(
                payload,
                languageCodeLength + 1,
                payload.size - languageCodeLength - 1,
                Charset.defaultCharset()
        )
    }


    private fun onTagTapped(superTagId: String, superTagData: String) {
        showToast("id: ${superTagId} and data: ${superTagData}")
    }

    override fun dismissSuccessMessage() {
        insertTodayClassesFragment()
    }

    override fun dismissFailureMessage() {
        insertTodayClassesFragment()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getActivityId(nfcId: String): StudentActivity? {
        Log.d(TAG, "Call get activitieID with param ${nfcId}")

        Log.d(TAG, "Call get activitieID with data: $_listActivites")

        _listActivites.let {

            var date: LocalDateTime = LocalDateTime.now()
            val result: List<StudentActivity>? = it?.filter { item -> item.nfcId == nfcId && LocalDateTime.parse(item.startTime, formatter).minusMinutes(30).isBefore(date) && date.isBefore(LocalDateTime.parse(item.endTime, formatter)) }

//            val result: List<StudentActivity>? = it?.filter { item -> item.nfcId == nfcId }
            Log.d(TAG, "Call get activitieID with result: ${result}")

            if (result.isNullOrEmpty()) {
                isConnected = activeNetwork?.isConnectedOrConnecting == true

                var value = activeNetwork?.isConnectedOrConnecting
//                showToast("Internet value: $value")

                if (!isConnected) {
                    showToast("No internet access!!!")
                } else {
                    showToast("You do not have any class at the moment!")
                }
//                Log.d(TAG, "You do not have any class at the moment!")
                return null
            } else {
                Log.d(TAG, "Call get activitieID with id: ${result}")

                for (item in result) {
                    Log.d(TAG, "CHECK DATE FOR ITEM: ${item}")

                    var checkdate1 = LocalDateTime.parse(item.startTime, formatter).isBefore(date);
                }

                return result.first()

            }
        }

        return null
    }

    fun getIMEIDeviceId(context: Context): String? {
        val deviceId: String
        deviceId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.d(TAG, "Get Android ID")
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        } else {
            val mTelephony = context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return ""
                }
            }
            assert(mTelephony != null)
            if (mTelephony.deviceId != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mTelephony.imei
                } else {
                    mTelephony.deviceId
                }
            } else {
                Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            }
        }
        Log.d("deviceId", deviceId)
        return deviceId
    }

}