package com.pjt.rebis.walletconnect_metamask

import android.app.Activity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_trx_connect_mask.*
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import com.pjt.rebis.R
//import com.pjt.rebis.ui.qrScan.custom_dialogCR.payloadObjectTMP
import com.pjt.rebis.utility.SaveSharedPreference
import com.pjt.rebis.webAPI.Payload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.walletconnect.Session
import org.walletconnect.nullOnThrow
import java.lang.Exception

class Trx_connectMask : Activity(), Session.Callback {

    private var txRequest: Long? = null
    private val uiScope = CoroutineScope(Dispatchers.Main)

    override fun onStatus(status: Session.Status) {
        when(status) {
            Session.Status.Approved -> sessionApproved()
            Session.Status.Closed -> sessionClosed()
            Session.Status.Connected,
            Session.Status.Disconnected,
            is Session.Status.Error -> {
                // Do Stuff
            }
        }
    }

    override fun onMethodCall(call: Session.MethodCall) {
    }
    private fun sessionApproved() {
        uiScope.launch {
            screen_main_status.text = "Connected: ${TransactionHandler.session.approvedAccounts()}"
            screen_main_connect_button.visibility = View.GONE
            screen_main_disconnect_button.visibility = View.VISIBLE
            screen_main_tx_button.visibility = View.VISIBLE
        }
    }

    private fun sessionClosed() {
        uiScope.launch {
            screen_main_status.text = "Disconnected"
            screen_main_connect_button.visibility = View.VISIBLE
            screen_main_disconnect_button.visibility = View.GONE
            screen_main_tx_button.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trx_connect_mask)
    }

    override fun onStart() {
        try {
            super.onStart()
            initialSetup()

        /*    // DEFINE PAYLOAD OBJECT GOT FROM 'custom_dialogCR'(previous step)
            val payObj: Payload = payloadObjectTMP
            payloadObjectTMP = null
            val typeUser: String = SaveSharedPreference.getUserType(this)
            val toAdr: String
            if (typeUser == ("renter"))
                toAdr = payObj.getRentalItem().addressCustomer
            else
                toAdr = payObj.getRentalItem().addressRenter

            screen_main_connect_button.setOnClickListener {
                try {
                    TransactionHandler.resetSession()
                    TransactionHandler.session.addCallback(this)
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(TransactionHandler.config.toWCUri())
                    startActivity(i)
                } catch (e:Exception) {e.printStackTrace()}
            }
            screen_main_disconnect_button.setOnClickListener {
                TransactionHandler.session.kill()
            }
            screen_main_tx_button.setOnClickListener {
                val from = TransactionHandler.session.approvedAccounts()?.first()
                        ?: return@setOnClickListener
                val txRequest = System.currentTimeMillis()
                TransactionHandler.session.performMethodCall(
                        Session.MethodCall.SendTransaction(
                                txRequest,
                                from,
                                toAdr,
                                null,
                                null,
                                null,
                                "0x5AF3107A4000",
                                ""
                        ),
                        ::handleResponse
                )
                this.txRequest = txRequest
            } */
        } catch (e:Exception) {e.printStackTrace()}
    }

    private fun initialSetup() {
        val session = nullOnThrow { TransactionHandler.session } ?: return
        Log.d("###setup", "Session: $session")
        session.addCallback(this)
        sessionApproved()
    }

    private fun handleResponse(resp: Session.MethodCall.Response) {
        if (resp.id == txRequest) {
            txRequest = null
            uiScope.launch {
                screen_main_response.visibility = View.VISIBLE
                screen_main_response.text = "Last response: " + ((resp.result as? String) ?: "Unknown response")
            }
        }
    }

    override fun onDestroy() {
        TransactionHandler.session.removeCallback(this)
        super.onDestroy()
    }
}