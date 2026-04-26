package se.yourcompany.miljonaren.data.local.content

import se.yourcompany.miljonaren.domain.model.AnswerOption
import se.yourcompany.miljonaren.domain.model.Difficulty
import se.yourcompany.miljonaren.domain.model.Question

object SwedishQuestionSeed {
    val questions: List<Question> = listOf(
        question(
            id = "q001",
            text = "Vad är huvudstaden i Sverige?",
            a = "Stockholm",
            b = "Göteborg",
            c = "Malmö",
            d = "Uppsala",
            correct = "A",
            category = "Geografi",
            difficulty = Difficulty.EASY
        ),
        question(
            id = "q002",
            text = "Hur många dagar har ett skottår?",
            a = "365",
            b = "366",
            c = "364",
            d = "367",
            correct = "B",
            category = "Allmänt",
            difficulty = Difficulty.EASY
        ),
        question(
            id = "q003",
            text = "Vilket grundämne har kemisk beteckning O?",
            a = "Guld",
            b = "Syre",
            c = "Silver",
            d = "Väte",
            correct = "B",
            category = "Vetenskap",
            difficulty = Difficulty.EASY
        ),
        question(
            id = "q004",
            text = "Vilken planet är närmast solen?",
            a = "Mars",
            b = "Jorden",
            c = "Merkurius",
            d = "Venus",
            correct = "C",
            category = "Rymden",
            difficulty = Difficulty.EASY
        ),
        question(
            id = "q005",
            text = "Vem skrev Romeo och Julia?",
            a = "William Shakespeare",
            b = "Astrid Lindgren",
            c = "Selma Lagerlof",
            d = "August Strindberg",
            correct = "A",
            category = "Kultur",
            difficulty = Difficulty.MEDIUM
        ),
        question(
            id = "q006",
            text = "Vilket är världens största hav?",
            a = "Atlanten",
            b = "Indiska oceanen",
            c = "Stilla havet",
            d = "Norra ishavet",
            correct = "C",
            category = "Geografi",
            difficulty = Difficulty.MEDIUM
        ),
        question(
            id = "q007",
            text = "Vilket är Sveriges största landskap till ytan?",
            a = "Lappland",
            b = "Skåne",
            c = "Dalarna",
            d = "Norrbotten",
            correct = "A",
            category = "Sverige",
            difficulty = Difficulty.MEDIUM
        ),
        question(
            id = "q008",
            text = "Vad heter Sveriges nuvarande valuta?",
            a = "Euro",
            b = "Krona",
            c = "Dollar",
            d = "Pund",
            correct = "B",
            category = "Ekonomi",
            difficulty = Difficulty.EASY
        ),
        question(
            id = "q009",
            text = "Vilken färg får du om du blandar blå och gul?",
            a = "Lila",
            b = "Orange",
            c = "Grönt",
            d = "Rött",
            correct = "C",
            category = "Allmänt",
            difficulty = Difficulty.EASY
        ),
        question(
            id = "q010",
            text = "Vilket land kommer bandet ABBA ifrån?",
            a = "Norge",
            b = "Sverige",
            c = "Danmark",
            d = "Finland",
            correct = "B",
            category = "Musik",
            difficulty = Difficulty.EASY
        ),
        question(
            id = "q011",
            text = "Vilket är Sveriges högsta berg?",
            a = "Kebnekaise",
            b = "Sarektjåkka",
            c = "Helags",
            d = "Áhkká",
            correct = "A",
            category = "Sverige",
            difficulty = Difficulty.MEDIUM
        ),
        question(
            id = "q012",
            text = "Vilket är Europas längsta vattendrag?",
            a = "Rhen",
            b = "Donau",
            c = "Volga",
            d = "Seine",
            correct = "C",
            category = "Geografi",
            difficulty = Difficulty.MEDIUM
        ),
        question(
            id = "q013",
            text = "Hur många ben har en spindel?",
            a = "6",
            b = "8",
            c = "10",
            d = "12",
            correct = "B",
            category = "Natur",
            difficulty = Difficulty.EASY
        ),
        question(
            id = "q014",
            text = "Vilken stad är känd för Eiffeltornet?",
            a = "Rom",
            b = "Madrid",
            c = "Paris",
            d = "Berlin",
            correct = "C",
            category = "Geografi",
            difficulty = Difficulty.EASY
        ),
        question(
            id = "q015",
            text = "Vilket är det minsta primtalet?",
            a = "1",
            b = "2",
            c = "3",
            d = "5",
            correct = "B",
            category = "Matematik",
            difficulty = Difficulty.EASY
        ),
        question(
            id = "q016",
            text = "Vem målade Mona Lisa?",
            a = "Vincent van Gogh",
            b = "Pablo Picasso",
            c = "Leonardo da Vinci",
            d = "Claude Monet",
            correct = "C",
            category = "Konst",
            difficulty = Difficulty.MEDIUM
        ),
        question(
            id = "q017",
            text = "Vilken gas andas vi in mest av i luften?",
            a = "Syre",
            b = "Koldioxid",
            c = "Kväve",
            d = "Väte",
            correct = "C",
            category = "Vetenskap",
            difficulty = Difficulty.HARD
        ),
        question(
            id = "q018",
            text = "Vilket är Sveriges största insjö?",
            a = "Vättern",
            b = "Mälaren",
            c = "Hjälmaren",
            d = "Vänern",
            correct = "D",
            category = "Sverige",
            difficulty = Difficulty.MEDIUM
        ),
        question(
            id = "q019",
            text = "Hur många minuter är en halv timme?",
            a = "15",
            b = "20",
            c = "30",
            d = "45",
            correct = "C",
            category = "Allmänt",
            difficulty = Difficulty.EASY
        ),
        question(
            id = "q020",
            text = "Vilken metall är flytande vid rumstemperatur?",
            a = "Järn",
            b = "Kvicksilver",
            c = "Aluminium",
            d = "Koppar",
            correct = "B",
            category = "Vetenskap",
            difficulty = Difficulty.HARD
        ),
        question(
            id = "q021",
            text = "Vilket organ pumpar blod i kroppen?",
            a = "Levern",
            b = "Hjärnan",
            c = "Hjärtat",
            d = "Lungan",
            correct = "C",
            category = "Biologi",
            difficulty = Difficulty.EASY
        ),
        question(
            id = "q022",
            text = "Vilken dag firar Sverige nationaldag?",
            a = "6 juni",
            b = "24 december",
            c = "1 maj",
            d = "31 december",
            correct = "A",
            category = "Sverige",
            difficulty = Difficulty.EASY
        ),
        question(
            id = "q023",
            text = "Vad heter den största av Egyptens pyramider?",
            a = "Cheopspyramiden",
            b = "Menkaures pyramid",
            c = "Röda pyramiden",
            d = "Djosers pyramid",
            correct = "A",
            category = "Historia",
            difficulty = Difficulty.HARD
        ),
        question(
            id = "q024",
            text = "Vilken svensk författare skapade Pippi Långstrump?",
            a = "Astrid Lindgren",
            b = "Tove Jansson",
            c = "Elsa Beskow",
            d = "Kerstin Ekman",
            correct = "A",
            category = "Kultur",
            difficulty = Difficulty.EASY
        ),
        question(
            id = "q025",
            text = "Hur många kontinenter brukar man räkna med?",
            a = "5",
            b = "6",
            c = "7",
            d = "8",
            correct = "C",
            category = "Geografi",
            difficulty = Difficulty.MEDIUM
        )
    )

    private fun question(
        id: String,
        text: String,
        a: String,
        b: String,
        c: String,
        d: String,
        correct: String,
        category: String,
        difficulty: Difficulty
    ): Question = Question(
        id = id,
        textSv = text,
        options = listOf(
            AnswerOption(id = "A", textSv = a),
            AnswerOption(id = "B", textSv = b),
            AnswerOption(id = "C", textSv = c),
            AnswerOption(id = "D", textSv = d)
        ),
        correctOptionId = correct,
        category = category,
        difficulty = difficulty
    )
}
