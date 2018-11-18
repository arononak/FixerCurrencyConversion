package com.example.currencyconversion.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.currencyconversion.R
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity : AppCompatActivity() {

    companion object {
        const val INTENT_DATE = "date"
        const val INTENT_CURRENCY = "currency"

        fun newIntent(context: Context, date: String, currency: String): Intent {
            return Intent(context, DetailsActivity::class.java).apply {
                putExtra(INTENT_DATE, date)
                putExtra(INTENT_CURRENCY, currency)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        date.text = intent.getStringExtra(INTENT_DATE)
                ?: throw IllegalStateException("field $INTENT_DATE missing in Intent")

        currency.text = intent.getStringExtra(INTENT_CURRENCY)
                ?: throw IllegalStateException("field $INTENT_CURRENCY missing in Intent")
    }
}
