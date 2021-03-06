package ua.co.myrecipes

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

class HiltTestRunner: AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,         //classname of actual Application class
        context: Context?
    ): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)  //replace it with our own
    }
}