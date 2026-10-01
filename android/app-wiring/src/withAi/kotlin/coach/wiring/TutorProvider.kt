package coach.wiring

import coach.ai.AiTutor
import coach.ports.TutorPort

/** Selected when the build includes :ai-adapter. Until 14G gives it a call site it is unavailable. */
internal fun provideTutor(): TutorPort = AiTutor()
