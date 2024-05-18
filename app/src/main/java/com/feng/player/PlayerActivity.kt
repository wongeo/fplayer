package com.feng.player

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.feng.player.fragment.PlayerFragment

class PlayerActivity : AppCompatActivity() {
    private var mFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startFragment(PlayerFragment())
    }

    private fun startFragment(fragment: Fragment) {
        this.mFragment = fragment
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.add(android.R.id.content, fragment)
        transaction.commit()
    }

    override fun onBackPressed() {
        if (mFragment is PlayerFragment && (mFragment as PlayerFragment).onBackPress()) {
            return
        }
        super.onBackPressed()
    }
}