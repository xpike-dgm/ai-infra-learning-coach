package coach.wiring

import coach.ai.AiTutor
import coach.ports.TutorPort

/** Selected when the build includes :ai-adapter: the provider client, with the learner's key (14G). */
internal fun provideTutor(): TutorPort = AiTutor(AiWiring.client)
