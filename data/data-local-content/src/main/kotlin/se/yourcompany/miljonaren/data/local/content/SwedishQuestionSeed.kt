package se.yourcompany.miljonaren.data.local.content

import se.yourcompany.miljonaren.domain.model.AnswerOption
import se.yourcompany.miljonaren.domain.model.Difficulty
import se.yourcompany.miljonaren.domain.model.Question

object SwedishQuestionSeed {
    val questions: List<Question> = listOf(
        question(
            id = "q001",
            text = "Vad ar huvudstaden i Sverige?",
            a = "Stockholm",
            b = "Goteborg",
            c = "Malmo",
            d = "Uppsala",
            correct = "A",
            category = "Geografi",
            difficulty = Difficulty.EASY
        ),
        question(
            id = "q002",
            text = "Hur manga dagar har ett skottar?",
            a = "365",
            b = "366",
            c = "364",
            d = "367",
            correct = "B",
            category = "Allmant",
            difficulty = Difficulty.EASY
        ),
        question(
            id = "q003",
            text = "Vilket grundamne har kemisk beteckning O?",
            a = "Guld",
            b = "Syre",
            c = "Silver",
            d = "Vate",
            correct = "B",
            category = "Vetenskap",
            difficulty = Difficulty.EASY
        ),
        question(
            id = "q004",
            text = "Vilken planet ar nast solen?",
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
            text = "Vilket ar varldens storsta hav?",
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
            text = "Vilket ar Sveriges storsta landskap till ytan?",
            a = "Lappland",
            b = "Skane",
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
            text = "Vilken farg far du om du blandar bla och gul?",
            a = "Lila",
            b = "Orange",
            c = "Gront",
            d = "Rott",
            correct = "C",
            category = "Allmant",
            difficulty = Difficulty.EASY
        ),
        question(
            id = "q010",
            text = "Vilket land kommer bandet ABBA ifran?",
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
            text = "Vilket ar Sveriges hogsta berg?",
            a = "Kebnekaise",
            b = "Sarektjakka",
            c = "Helags",
            d = "Akka",
            correct = "A",
            category = "Sverige",
            difficulty = Difficulty.MEDIUM
        ),
        question(
            id = "q012",
            text = "Vilket ar Europas langsta vattendrag?",
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
            text = "Hur manga ben har en spindel?",
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
            text = "Vilken stad ar kanda for Eiffeltornet?",
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
            text = "Vilket ar det minsta primtalet?",
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
            text = "Vem malade Mona Lisa?",
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
            c = "Kvaeve",
            d = "Vate",
            correct = "C",
            category = "Vetenskap",
            difficulty = Difficulty.HARD
        ),
        question(
            id = "q018",
            text = "Vilket ar Sveriges storsta insjo?",
            a = "Vattern",
            b = "Malaren",
            c = "Hjalmaren",
            d = "Vanern",
            correct = "D",
            category = "Sverige",
            difficulty = Difficulty.MEDIUM
        ),
        question(
            id = "q019",
            text = "Hur manga minuter ar en halv timme?",
            a = "15",
            b = "20",
            c = "30",
            d = "45",
            correct = "C",
            category = "Allmant",
            difficulty = Difficulty.EASY
        ),
        question(
            id = "q020",
            text = "Vilken metall ar flytande vid rumstemperatur?",
            a = "Jarn",
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
            b = "Hjarnan",
            c = "Hjartat",
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
            text = "Vad heter den langsta av Egyptens pyramider?",
            a = "Cheopspyramiden",
            b = "Menkaures pyramid",
            c = "Roda pyramiden",
            d = "Djosers pyramid",
            correct = "A",
            category = "Historia",
            difficulty = Difficulty.HARD
        ),
        question(
            id = "q024",
            text = "Vilken svensk forfattare skapade Pippi Langstrump?",
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
            text = "Hur manga kontinenter brukar man rakna med?",
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
