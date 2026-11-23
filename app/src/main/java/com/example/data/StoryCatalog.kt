package com.example.data

import com.example.model.CompletedStory
import com.example.model.PartOfSpeech
import com.example.model.StoryBlank
import com.example.model.StoryCategory
import com.example.model.StorySegment
import com.example.model.StoryTemplate

object StoryCatalog {

    private fun parseTemplate(
        id: String,
        title: String,
        subtitle: String,
        category: StoryCategory,
        laughTag: String,
        blanks: List<StoryBlank>,
        script: String
    ): StoryTemplate {
        val segments = mutableListOf<StorySegment>()
        val regex = Regex("\\{([a-zA-Z0-9_]+)\\}")
        var lastIndex = 0
        for (match in regex.findAll(script)) {
            if (match.range.first > lastIndex) {
                segments.add(StorySegment.Text(script.substring(lastIndex, match.range.first)))
            }
            segments.add(StorySegment.BlankRef(match.groupValues[1]))
            lastIndex = match.range.last + 1
        }
        if (lastIndex < script.length) {
            segments.add(StorySegment.Text(script.substring(lastIndex)))
        }
        return StoryTemplate(
            id = id,
            title = title,
            subtitle = subtitle,
            category = category,
            laughTag = laughTag,
            blanks = blanks,
            segments = segments
        )
    }

    val allStories: List<StoryTemplate> = listOf(
        // ==================== SCI-FI SHENANIGANS ====================
        parseTemplate(
            id = "scifi_toaster_ship",
            title = "Attack of the Galactic Toaster",
            subtitle = "First contact goes deliciously off the rails.",
            category = StoryCategory.SCIFI,
            laughTag = "Cosmic Chaos",
            blanks = listOf(
                StoryBlank("adj1", PartOfSpeech.ADJECTIVE),
                StoryBlank("hero", PartOfSpeech.CELEBRITY, "Space Captain's Name"),
                StoryBlank("noun1", PartOfSpeech.NOUN, "Weird Spaceship Part"),
                StoryBlank("sound", PartOfSpeech.SOUND_EFFECT),
                StoryBlank("animal", PartOfSpeech.ANIMAL),
                StoryBlank("food", PartOfSpeech.FOOD),
                StoryBlank("verb_ing", PartOfSpeech.VERB_ING),
                StoryBlank("plural", PartOfSpeech.PLURAL_NOUN),
                StoryBlank("excl", PartOfSpeech.EXCLAMATION)
            ),
            script = "Captain's Log, Stardate 9000: Our {adj1} starship, commanded by the fearless {hero}, suddenly lost power in the main {noun1}! With a deafening \"{sound}\", a fleet of three-eyed aliens beamed onto the bridge riding a giant {animal}. Their supreme leader demanded a tribute of {food} or else they would blast us with their tickle-ray! Thinking fast, our crew began {verb_ing} around the control deck while tossing {plural} into the air. The aliens stared in total awe, shouted \"{excl}\", and immediately crowned us Emperors of the Galaxy."
        ),
        parseTemplate(
            id = "scifi_robot_butler",
            title = "My Glitchy Robot Butler",
            subtitle = "Never buy a discount android off a late-night infomercial.",
            category = StoryCategory.SCIFI,
            laughTag = "Beep Boop Oops",
            blanks = listOf(
                StoryBlank("number", PartOfSpeech.NUMBER),
                StoryBlank("adj1", PartOfSpeech.ADJECTIVE),
                StoryBlank("job", PartOfSpeech.OCCUPATION),
                StoryBlank("food", PartOfSpeech.FOOD),
                StoryBlank("body", PartOfSpeech.BODY_PART),
                StoryBlank("adverb", PartOfSpeech.ADVERB),
                StoryBlank("noun1", PartOfSpeech.NOUN),
                StoryBlank("verb_past", PartOfSpeech.VERB_PAST)
            ),
            script = "Last Tuesday, I spent {number} dollars on a shiny new robot butler named Gizmo. The box promised a {adj1} assistant trained by a world-class {job}, so I asked it to make breakfast. Instead, Gizmo blended {food} in my washing machine and politely smeared it onto my {body}. When I yelled \"STOP!\", Gizmo spun {adverb} in circles, grabbed my favorite {noun1}, and {verb_past} straight through the drywall. Five stars for enthusiasm, zero stars for housekeeping."
        ),
        parseTemplate(
            id = "scifi_time_machine",
            title = "Oops, I Broke the Timeline",
            subtitle = "One tiny snack changes human history forever.",
            category = StoryCategory.SCIFI,
            laughTag = "Time Warp",
            blanks = listOf(
                StoryBlank("place", PartOfSpeech.PLACE),
                StoryBlank("noun1", PartOfSpeech.NOUN),
                StoryBlank("celeb", PartOfSpeech.CELEBRITY),
                StoryBlank("adj1", PartOfSpeech.ADJECTIVE),
                StoryBlank("plural", PartOfSpeech.PLURAL_NOUN),
                StoryBlank("verb_ing", PartOfSpeech.VERB_ING),
                StoryBlank("animal", PartOfSpeech.ANIMAL),
                StoryBlank("excl", PartOfSpeech.EXCLAMATION)
            ),
            script = "I accidentally built a time machine in {place} out of duct tape and a spare {noun1}. When I zipped back to the year 1492, I bumped straight into {celeb} and dropped my pocket full of {adj1} {plural}! Naturally, everyone immediately stopped what they were doing and started {verb_ing}. By the time I jumped back to the present, history had totally changed: every world leader was a {animal}, and the national anthem was just people yelling \"{excl}\" for three straight minutes."
        ),

        // ==================== FANTASY FIASCO ====================
        parseTemplate(
            id = "fantasy_grumpy_dragon",
            title = "The Dragon's Cooking Showdown",
            subtitle = "Slaying monsters is out — baking battles are in!",
            category = StoryCategory.FANTASY,
            laughTag = "Spicy Quest",
            blanks = listOf(
                StoryBlank("adj1", PartOfSpeech.ADJECTIVE),
                StoryBlank("animal", PartOfSpeech.ANIMAL, "Noble Steed"),
                StoryBlank("hero", PartOfSpeech.CELEBRITY),
                StoryBlank("food", PartOfSpeech.FOOD),
                StoryBlank("plural", PartOfSpeech.PLURAL_NOUN, "Secret Ingredients"),
                StoryBlank("adverb", PartOfSpeech.ADVERB),
                StoryBlank("sound", PartOfSpeech.SOUND_EFFECT),
                StoryBlank("body", PartOfSpeech.BODY_PART)
            ),
            script = "Hear ye! Deep inside the {adj1} volcano lived Barnaby the Fire Dragon, who refused to stop singing opera at midnight. Riding a noble {animal}, the legendary knight {hero} arrived to challenge him — not with a sword, but with a bake-off! Our hero whipped up a steaming platter of {food} stuffed with extra {plural}. Barnaby took one bite {adverb}, let out a thunderous \"{sound}\", and wept happy tears all over his own {body}. The kingdom was saved, and they opened a five-star bistro the very next morning."
        ),
        parseTemplate(
            id = "fantasy_wizard_exam",
            title = "Disaster at Wizard Academy",
            subtitle = "Always double-check your spell pronunciation.",
            category = StoryCategory.FANTASY,
            laughTag = "Magic Mishap",
            blanks = listOf(
                StoryBlank("excl", PartOfSpeech.EXCLAMATION, "Magic Spell Word"),
                StoryBlank("noun1", PartOfSpeech.NOUN),
                StoryBlank("adj1", PartOfSpeech.ADJECTIVE),
                StoryBlank("plural", PartOfSpeech.PLURAL_NOUN),
                StoryBlank("verb_ing", PartOfSpeech.VERB_ING),
                StoryBlank("job", PartOfSpeech.OCCUPATION),
                StoryBlank("number", PartOfSpeech.NUMBER),
                StoryBlank("place", PartOfSpeech.PLACE)
            ),
            script = "It was final exam day at Wizarding University! I waved my wand and shouted \"{excl}!\" trying to turn a teacup into a golden {noun1}. Unfortunately, I sneezed mid-spell. Suddenly the entire classroom filled with {adj1} {plural} that wouldn't stop {verb_ing}! Even the Headmaster — who used to be a professional {job} — got stuck on the ceiling for {number} hours. Long story short, I got an A-plus for creativity and a lifetime ban from {place}."
        ),
        parseTemplate(
            id = "fantasy_pirate_duck",
            title = "Legend of Captain Quackbeard",
            subtitle = "The seven seas have never seen a crew this ridiculous.",
            category = StoryCategory.FANTASY,
            laughTag = "Ahoy Laughs",
            blanks = listOf(
                StoryBlank("adj1", PartOfSpeech.ADJECTIVE),
                StoryBlank("food", PartOfSpeech.FOOD),
                StoryBlank("plural", PartOfSpeech.PLURAL_NOUN),
                StoryBlank("verb_past", PartOfSpeech.VERB_PAST),
                StoryBlank("animal", PartOfSpeech.ANIMAL),
                StoryBlank("body", PartOfSpeech.BODY_PART),
                StoryBlank("adverb", PartOfSpeech.ADVERB),
                StoryBlank("noun1", PartOfSpeech.NOUN, "Ultimate Treasure")
            ),
            script = "Shiver me timbers! Aboard the {adj1} pirate ship S.S. Biscuit, the crew ran completely out of {food} and started mutinying with {plural}. Out of nowhere, a giant sea monster {verb_past} right over the deck! Without blinking, the ship's mascot — a feisty {animal} wearing an eyepatch — booped the monster on its {body} and danced {adverb} across the mast. Impressed, the sea monster handed over the legendary buried {noun1} and asked to join the crew."
        ),

        // ==================== EVERYDAY CHAOS ====================
        parseTemplate(
            id = "everyday_job_interview",
            title = "The Worst Job Interview Ever",
            subtitle = "Confidence is key, even when everything is on fire.",
            category = StoryCategory.EVERYDAY,
            laughTag = "Office Riot",
            blanks = listOf(
                StoryBlank("job", PartOfSpeech.OCCUPATION, "Dream Job"),
                StoryBlank("adj1", PartOfSpeech.ADJECTIVE),
                StoryBlank("noun1", PartOfSpeech.NOUN),
                StoryBlank("adverb", PartOfSpeech.ADVERB),
                StoryBlank("food", PartOfSpeech.FOOD),
                StoryBlank("verb_ing", PartOfSpeech.VERB_ING),
                StoryBlank("number", PartOfSpeech.NUMBER),
                StoryBlank("excl", PartOfSpeech.EXCLAMATION)
            ),
            script = "I really wanted the job as Chief {job}, so I wore my most {adj1} suit and brought a lucky {noun1} for moral support. When the hiring manager asked about my greatest strength, I looked them dead in the eye and {adverb} pulled a lukewarm {food} out of my briefcase. Then, to show I was a \"team player,\" I demonstrated my talent for {verb_ing} for {number} awkward seconds. The boss slammed the desk, yelled \"{excl}\", and hired me as Vice President on the spot!"
        ),
        parseTemplate(
            id = "everyday_cooking_disaster",
            title = "Midnight Kitchen Meltdown",
            subtitle = "Who needs a recipe when you have pure chaotic energy?",
            category = StoryCategory.EVERYDAY,
            laughTag = "Chef's Kiss",
            blanks = listOf(
                StoryBlank("food", PartOfSpeech.FOOD),
                StoryBlank("number", PartOfSpeech.NUMBER),
                StoryBlank("plural", PartOfSpeech.PLURAL_NOUN),
                StoryBlank("adj1", PartOfSpeech.ADJECTIVE),
                StoryBlank("sound", PartOfSpeech.SOUND_EFFECT),
                StoryBlank("animal", PartOfSpeech.ANIMAL),
                StoryBlank("verb_past", PartOfSpeech.VERB_PAST),
                StoryBlank("celeb", PartOfSpeech.CELEBRITY)
            ),
            script = "At 2 AM, I decided to invent the ultimate {food}. The secret? Mixing {number} cups of crushed {plural} with a dash of {adj1} hot sauce. The moment I pressed \"START\" on the blender, the lid flew off with a giant \"{sound}\"! Sauce splattered all over the ceiling, my neighbor's {animal} peeked through the window in judgment, and the oven {verb_past} across the linoleum. Honestly, even {celeb} couldn't cook with this much drama."
        ),
        parseTemplate(
            id = "everyday_gym_class",
            title = "Olympic Chaos at the Gym",
            subtitle = "New personal record in accidental comedy.",
            category = StoryCategory.EVERYDAY,
            laughTag = "Sweat & Giggles",
            blanks = listOf(
                StoryBlank("adj1", PartOfSpeech.ADJECTIVE),
                StoryBlank("noun1", PartOfSpeech.NOUN),
                StoryBlank("verb_ing", PartOfSpeech.VERB_ING),
                StoryBlank("body", PartOfSpeech.BODY_PART),
                StoryBlank("adverb", PartOfSpeech.ADVERB),
                StoryBlank("sound", PartOfSpeech.SOUND_EFFECT),
                StoryBlank("plural", PartOfSpeech.PLURAL_NOUN),
                StoryBlank("place", PartOfSpeech.PLACE)
            ),
            script = "My first day at the {adj1} Fitness Megaplex started smoothly — until I mistook the treadmill for a giant {noun1}. Trying to look cool while {verb_ing}, I accidentally bumped the speed to MAXIMUM with my {body}. I launched {adverb} backward into a pyramid of yoga balls with a magnificent \"{sound}\", scattering {plural} everywhere! The personal trainer clapped slowly and suggested I stick to stretching at {place} instead."
        ),

        // ==================== MYSTERY & MAYHEM ====================
        parseTemplate(
            id = "mystery_missing_mascot",
            title = "Case of the Missing Donut",
            subtitle = "Detective Whiskers is on the trail of the century.",
            category = StoryCategory.MYSTERY,
            laughTag = "Noir Giggles",
            blanks = listOf(
                StoryBlank("food", PartOfSpeech.FOOD, "Stolen Item"),
                StoryBlank("place", PartOfSpeech.PLACE),
                StoryBlank("adj1", PartOfSpeech.ADJECTIVE),
                StoryBlank("celeb", PartOfSpeech.CELEBRITY, "Prime Suspect"),
                StoryBlank("plural", PartOfSpeech.PLURAL_NOUN, "Suspicious Clues"),
                StoryBlank("verb_ing", PartOfSpeech.VERB_ING),
                StoryBlank("adverb", PartOfSpeech.ADVERB),
                StoryBlank("excl", PartOfSpeech.EXCLAMATION)
            ),
            script = "It was a dark and stormy afternoon when the priceless {food} vanished from {place}. Detective Whiskers adjusted his {adj1} trench coat and immediately suspected {celeb}. Using a giant magnifying glass, he discovered a trail of glittery {plural} leading straight to the broom closet! Inside, the culprit was caught red-handed {verb_ing} with frosting on their nose. Whiskers pointed {adverb} and declared, \"{excl} Case closed — now pass the napkins!\""
        ),
        parseTemplate(
            id = "mystery_haunted_attic",
            title = "The Disco Ghost of Manor Hill",
            subtitle = "Turns out this haunted mansion just wants to party.",
            category = StoryCategory.MYSTERY,
            laughTag = "Spooky Silly",
            blanks = listOf(
                StoryBlank("adj1", PartOfSpeech.ADJECTIVE),
                StoryBlank("sound", PartOfSpeech.SOUND_EFFECT),
                StoryBlank("noun1", PartOfSpeech.NOUN),
                StoryBlank("animal", PartOfSpeech.ANIMAL),
                StoryBlank("verb_ing", PartOfSpeech.VERB_ING),
                StoryBlank("plural", PartOfSpeech.PLURAL_NOUN),
                StoryBlank("body", PartOfSpeech.BODY_PART),
                StoryBlank("number", PartOfSpeech.NUMBER)
            ),
            script = "Everyone warned us never to enter the {adj1} attic after sundown. Sure enough, at midnight we heard an eerie \"{sound}\" echoing behind a dusty {noun1}! Trembling, we shined our flashlight and gasped — it was a glowing phantom wearing roller skates and holding a {animal}! Instead of scaring us, the ghost started {verb_ing} while tossing neon {plural} everywhere. Soon my {body} was grooving too, and we danced with that ghost for {number} hours straight."
        ),
        parseTemplate(
            id = "mystery_museum_heist",
            title = "The Clumsiest Museum Heist",
            subtitle = "Zero stealth, maximum slapstick.",
            category = StoryCategory.MYSTERY,
            laughTag = "Heist Hijinks",
            blanks = listOf(
                StoryBlank("celeb", PartOfSpeech.CELEBRITY, "Mastermind Name"),
                StoryBlank("noun1", PartOfSpeech.NOUN, "Priceless Artifact"),
                StoryBlank("adj1", PartOfSpeech.ADJECTIVE),
                StoryBlank("verb_past", PartOfSpeech.VERB_PAST),
                StoryBlank("sound", PartOfSpeech.SOUND_EFFECT),
                StoryBlank("job", PartOfSpeech.OCCUPATION),
                StoryBlank("food", PartOfSpeech.FOOD),
                StoryBlank("adverb", PartOfSpeech.ADVERB)
            ),
            script = "At midnight, notorious art thief {celeb} rappelled from the skylight to swipe the legendary Diamond {noun1}. Wearing squeaky {adj1} sneakers was mistake number one. Mistake number two happened when they {verb_past} directly into a laser beam, triggering a colossal \"{sound}\"! Before security even arrived, a night-shift {job} walked in eating {food}, casually tripped the thief with a mop, and {adverb} called it the easiest Tuesday on record."
        )
    )

    fun renderPlainText(story: CompletedStory): String {
        val sb = StringBuilder()
        sb.appendLine(story.template.title)
        sb.appendLine()
        for (segment in story.template.segments) {
            when (segment) {
                is StorySegment.Text -> sb.append(segment.content)
                is StorySegment.BlankRef -> {
                    val word = story.filledBlanks[segment.blankId]?.word?.takeIf { it.isNotBlank() }
                        ?: "_____"
                    sb.append(word.uppercase())
                }
            }
        }
        sb.appendLine()
        sb.appendLine()
        sb.append("— Created with WordWacky!")
        return sb.toString()
    }

    fun renderSpokenText(story: CompletedStory): String {
        val sb = StringBuilder()
        sb.append(story.template.title).append(". ")
        for (segment in story.template.segments) {
            when (segment) {
                is StorySegment.Text -> sb.append(segment.content)
                is StorySegment.BlankRef -> {
                    val word = story.filledBlanks[segment.blankId]?.word?.takeIf { it.isNotBlank() }
                        ?: "blank"
                    sb.append(word)
                }
            }
        }
        return sb.toString()
    }
}
