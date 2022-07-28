package com.qifan.nfcbank

import android.content.Intent
import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Build.VERSION_CODES.JELLY_BEAN
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.hcetest.ApduService
import com.example.hcetest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var mNfcAdapter: NfcAdapter
    private lateinit var mTurnNfcDialog: AlertDialog
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
        initNFCFunction()
    }


    private fun initNFCFunction() {
        if (checkNFCEnable() && packageManager.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION)) {
            binding.textView.visibility = View.GONE
            binding.editText.visibility = View.VISIBLE
            binding.button.visibility = View.VISIBLE
            initService()
        } else {
            binding.textView.visibility = View.VISIBLE
            binding.editText.visibility = View.GONE
            binding.button.visibility = View.GONE
            showTurnOnNfcDialog()
        }
    }

    private fun initService() {
        binding.button.setOnClickListener {
            if (TextUtils.isEmpty(binding.editText.text)) {
                Toast.makeText(
                    this@MainActivity,
                    "입력하세요.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val intent = Intent(this@MainActivity, ApduService::class.java)
                intent.putExtra("ndefMessage", binding.editText.text.toString())
                startService(intent)
            }
        }

    }

    private fun checkNFCEnable(): Boolean {
        return if (mNfcAdapter == null) {
            binding.textView.text = "디바이스에 NFC 모듈이 없음"
            false
        } else {
            mNfcAdapter.isEnabled
        }
    }

    private fun showTurnOnNfcDialog() {
        mTurnNfcDialog = AlertDialog.Builder(this)
            .setTitle("NFC가 꺼져있음")
            .setMessage("NFC 기능을 켜주세요")
            .setPositiveButton("TURN ON"
            ) { _, _ ->
                startActivity(Intent(android.provider.Settings.ACTION_NFC_SETTINGS))
            }.setNegativeButton("DISMISS") { _, _ ->
                onBackPressed()
            }
            .create()
        mTurnNfcDialog.show()
    }

    override fun onResume() {
        super.onResume()
        if (mNfcAdapter!!.isEnabled) {
            binding.textView.visibility = View.GONE
            binding.editText.visibility = View.VISIBLE
            binding.button.visibility = View.VISIBLE
            initService()
        }
    }


}