package coach.wiring

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import coach.ui.AppRoot

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val graph = AppGraph()
        setContent {
            AppRoot(statusLine = "study day ${graph.clock.now().studyDay}")
        }
    }
}
