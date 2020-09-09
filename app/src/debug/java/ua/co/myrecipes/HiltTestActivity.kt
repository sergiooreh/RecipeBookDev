package ua.co.myrecipes

import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

/*cause with fragmentRule fragments will be attached to empty activity without @AndroidEntryPoint*/
@AndroidEntryPoint
class HiltTestActivity : AppCompatActivity() {          //custom activity which we will attach to fragments for test cases(through HiltExt)
}