package com.example.model

import androidx.annotation.DrawableRes
import com.example.R

enum class StoryCategory(
    val title: String,
    val badgeLabel: String,
    @DrawableRes val artRes: Int,
    val paletteIndex: Int
) {
    SCIFI(
        title = "Sci-Fi Shenanigans",
        badgeLabel = "Sci-Fi",
        artRes = R.drawable.img_pack_scifi,
        paletteIndex = 1
    ),
    FANTASY(
        title = "Fantasy Fiasco",
        badgeLabel = "Fantasy",
        artRes = R.drawable.img_pack_fantasy,
        paletteIndex = 4
    ),
    EVERYDAY(
        title = "Everyday Chaos",
        badgeLabel = "Everyday",
        artRes = R.drawable.img_pack_chaos,
        paletteIndex = 0
    ),
    MYSTERY(
        title = "Mystery & Mayhem",
        badgeLabel = "Mystery",
        artRes = R.drawable.img_pack_chaos,
        paletteIndex = 2
    )
}

enum class PartOfSpeech(
    val label: String,
    val hint: String,
    val paletteIndex: Int,
    val suggestions: List<String>
) {
    ADJECTIVE(
        label = "Adjective",
        hint = "A describing word — the sillier the better!",
        paletteIndex = 0,
        suggestions = listOf(
            "squishy", "radioactive", "glittering", "suspiciously damp",
            "turbo-charged", "wobbly", "dramatic", "soggy", "ferocious",
            "sparkly", "grumpy", "fizzy", "invisible", "crumbly", "majestic"
        )
    ),
    NOUN(
        label = "Noun",
        hint = "Any person, place, or weird object.",
        paletteIndex = 1,
        suggestions = listOf(
            "toaster", "kazoo", "rubber chicken", "lava lamp", "trampoline",
            "tuba", "garden gnome", "disco ball", "spatula", "cactus",
            "bowling ball", "accordion", "waffle iron", "pogo stick", "monocle"
        )
    ),
    PLURAL_NOUN(
        label = "Plural Noun",
        hint = "More than one thing (usually ends in -s).",
        paletteIndex = 3,
        suggestions = listOf(
            "pickles", "squirrels", "rainbows", "banjos", "sneakers",
            "marshmallows", "goblin socks", "pancakes", "jellybeans", "umbrellas",
            "noodles", "glitter bombs", "hamsters", "rubber ducks", "tacos"
        )
    ),
    VERB_ING(
        label = "Verb (-ing)",
        hint = "An action happening right now, ending in -ing.",
        paletteIndex = 4,
        suggestions = listOf(
            "yodeling", "moonwalking", "juggling", "karate-chopping", "somersaulting",
            "beatboxing", "tap-dancing", "snorkeling", "giggling", "wrestling",
            "skateboard-flipping", "catapulting", "hula-hooping", "speed-knitting"
        )
    ),
    VERB_PAST(
        label = "Past-Tense Verb",
        hint = "An action that already happened (e.g., exploded).",
        paletteIndex = 5,
        suggestions = listOf(
            "exploded", "cartwheeled", "teleported", "belly-flopped", "yeeted",
            "sneezed", "galloped", "backflipped", "tiptoed", "boinged",
            "marched", "slithered", "whistled", "zoomed"
        )
    ),
    ADVERB(
        label = "Adverb",
        hint = "How someone does something (often ends in -ly).",
        paletteIndex = 2,
        suggestions = listOf(
            "aggressively", "gracefully", "suspiciously", "dramatically", "chaotically",
            "enthusiastically", "awkwardly", "heroically", "loudly", "frantically",
            "politely", "mysteriously", "wildly", "gloriously"
        )
    ),
    EXCLAMATION(
        label = "Exclamation!",
        hint = "Something you'd shout in total shock!",
        paletteIndex = 6,
        suggestions = listOf(
            "Great Googly Moogly!", "Bazinga!", "Sweet Sassafras!", "Holy Guacamole!",
            "By Neptune's Beard!", "Yikes on Bikes!", "Zowie!", "Jumping Jellyfish!",
            "Kaboom!", "Wubba Lubba!", "Snickerdoodles!", "Gadzooks!"
        )
    ),
    FOOD(
        label = "Silly Food",
        hint = "A tasty snack or bizarre culinary creation.",
        paletteIndex = 0,
        suggestions = listOf(
            "cheese fondue", "glitter-frosted donut", "pickled nachos", "spicy meatball",
            "anchovy sundae", "giant burrito", "garlic waffle", "peanut butter lasagna",
            "bubblegum soup", "crispy churro", "mega-pretzel", "tofu smoothie"
        )
    ),
    ANIMAL(
        label = "Animal",
        hint = "Any critter — real, tiny, or ridiculously fluffy.",
        paletteIndex = 3,
        suggestions = listOf(
            "capybara", "llama", "flamingo", "raccoon", "narwhal",
            "chihuahua", "penguin", "platypus", "hamster", "alpaca",
            "octopus", "corgi", "porcupine", "sloth"
        )
    ),
    BODY_PART(
        label = "Body Part",
        hint = "An elbow, eyebrow, pinky toe — anything goes!",
        paletteIndex = 5,
        suggestions = listOf(
            "left eyebrow", "pinky toe", "funny bone", "belly button", "kneecap",
            "earlobe", "nostril", "mustache", "big toe", "chin", "elbow", "collarbone"
        )
    ),
    CELEBRITY(
        label = "Famous Name",
        hint = "A celebrity, historical figure, or funny made-up hero.",
        paletteIndex = 1,
        suggestions = listOf(
            "Captain Crunch", "Sir Reginald Fluff", "Einstein", "Cleopatra",
            "DJ Banana", "Sherlock Holmes", "Grandma Turbo", "Mozart",
            "Baron Von Cheese", "Blackbeard", "Shakespeare", "Dr. Noodle"
        )
    ),
    NUMBER(
        label = "Number",
        hint = "Any wild number — tiny or absurdly huge.",
        paletteIndex = 2,
        suggestions = listOf(
            "42", "9,001", "7", "3.5", "104", "1,000,000", "13", "88", "27", "512"
        )
    ),
    PLACE(
        label = "Place",
        hint = "A real city, room, or wacky destination.",
        paletteIndex = 4,
        suggestions = listOf(
            "the bouncy castle", "Mars", "a haunted laundromat", "Atlantis",
            "the cheese factory", "Timbuktu", "a giant treehouse", "the moon",
            "a bowling alley", "the DMV", "a submarine", "a pancake diner"
        )
    ),
    SOUND_EFFECT(
        label = "Sound Effect",
        hint = "A funny noise word like SPLAT or HONK!",
        paletteIndex = 6,
        suggestions = listOf(
            "SPLAT!", "HONK-HONK!", "BOING!", "KABLOOEY!", "SQUELCH!",
            "WHOOSH!", "BEEP-BOOP!", "TRUMPET BLAST!", "CLANK!", "FIZZLE!"
        )
    ),
    OCCUPATION(
        label = "Job / Title",
        hint = "A profession or quirky job title.",
        paletteIndex = 1,
        suggestions = listOf(
            "circus clown", "space pirate", "cheese sommelier", "disco detective",
            "ninja librarian", "bubblegum scientist", "professional napper",
            "wizard barista", "yodeling coach", "squirrel whisperer"
        )
    )
}

data class StoryBlank(
    val id: String,
    val partOfSpeech: PartOfSpeech,
    val customPrompt: String? = null
)

sealed class StorySegment {
    data class Text(val content: String) : StorySegment()
    data class BlankRef(val blankId: String) : StorySegment()
}

data class StoryTemplate(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: StoryCategory,
    val laughTag: String,
    val blanks: List<StoryBlank>,
    val segments: List<StorySegment>
)

data class FilledBlank(
    val blankId: String,
    val word: String,
    val enteredByPlayer: String? = null
)

data class CompletedStory(
    val id: String,
    val template: StoryTemplate,
    val filledBlanks: Map<String, FilledBlank>,
    val createdAtMillis: Long,
    val laughRating: Int = 0,
    val wasPartyMode: Boolean = false
)

enum class VoiceStyle(
    val label: String,
    val pitch: Float,
    val speechRate: Float
) {
    STORYTELLER(label = "Storyteller", pitch = 1.0f, speechRate = 0.96f),
    CHIPMUNK(label = "Chipmunk", pitch = 1.75f, speechRate = 1.25f),
    GIANT(label = "Dramatic Giant", pitch = 0.58f, speechRate = 0.78f),
    AUCTIONEER(label = "Turbo Mode", pitch = 1.15f, speechRate = 1.55f)
}

data class PartyPlayer(
    val id: String,
    val name: String,
    val colorIndex: Int
)
