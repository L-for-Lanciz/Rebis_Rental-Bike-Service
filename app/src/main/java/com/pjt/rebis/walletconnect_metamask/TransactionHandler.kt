package com.pjt.rebis.walletconnect_metamask

import android.util.Log
import androidx.multidex.MultiDexApplication
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import org.komputing.khex.extensions.toNoPrefixHexString
import org.walletconnect.Session
import org.walletconnect.impls.*
import org.walletconnect.nullOnThrow
import java.io.File
import java.util.*

class TransactionHandler : MultiDexApplication() {
    init {
        initMoshi()
        initClient()
        initBridge()
        initSessionStorage()
    }

    override fun onCreate() {
        super.onCreate()
        initMoshi()
        initClient()
        initBridge()
        initSessionStorage()
    }

    private fun initClient() {
        client = OkHttpClient.Builder().build()
    }

    private fun initMoshi() {
        moshi = Moshi.Builder().build()
    }


    private fun initBridge() {
        bridge = BridgeServer(moshi)
        bridge.start()
    }

    private fun initSessionStorage() {
        try {
            storage = FileWCSessionStore(File(cacheDir, "session_store.json").apply { createNewFile() }, moshi)
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private lateinit var client: OkHttpClient
        private lateinit var moshi: Moshi
        private lateinit var bridge: BridgeServer
        private lateinit var storage: WCSessionStore
        lateinit var config: Session.Config
        lateinit var session: Session

        fun resetSession() {
            try {
                nullOnThrow { session }?.clearCallbacks()
                val key = ByteArray(32).also { Random().nextBytes(it) }.toNoPrefixHexString()
                config = Session.Config(UUID.randomUUID().toString(), "http://localhost:${BridgeServer.PORT}", key)
                Log.d("##init", "config: $config")
                session = WCSession(
                        config,
                        MoshiPayloadAdapter(moshi),
                        storage,
                        OkHttpTransport.Builder(client, moshi),
                        Session.PeerMeta(name = "RebisApp")
                )
                session.offer()
            } catch (e:Exception) {e.printStackTrace()}
        }
    }

}