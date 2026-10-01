package coach.wiring

import coach.application.NullTutor
import coach.ports.TutorPort

/**
 * Selected when the build excludes :ai-adapter. The shipped null tutor answers nothing, authored help is
 * still shown, and nothing leaves the device (`TUTX-v0` §15).
 */
internal fun provideTutor(): TutorPort = NullTutor
