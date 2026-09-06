package com.nepalicode.dev.nepalilang.core

object NepaliConverter {

    private val pythonToNepaliMap = listOf(
        Regex("\\bdef\\b") to "kaam",
        Regex("\\breturn\\b") to "firta",
        Regex("\\bif\\b") to "yedi",
        Regex("\\belif\\b") to "athawa",
        Regex("\\belse\\b") to "natra",
        Regex("\\bwhile\\b") to "jabasamma",
        Regex("\\bfor\\b") to "ko_lagi",
        Regex("\\bin\\b") to "ma",
        Regex("\\bimport\\b") to "lyau",
        Regex("\\bfrom\\b") to "bata",
        Regex("\\bclass\\b") to "kakshya",
        Regex("\\btry\\b") to "koshish",
        Regex("\\bexcept\\b") to "samau",
        Regex("\\bfinally\\b") to "antya",
        Regex("\\braise\\b") to "uthau",
        Regex("\\bTrue\\b") to "sacho",
        Regex("\\bFalse\\b") to "jhut",
        Regex("\\bNone\\b") to "khali",
        Regex("\\band\\b") to "ra",
        Regex("\\bor\\b") to "wa",
        Regex("\\bnot\\b") to "hoina",
        Regex("\\bbreak\\b") to "rok",
        Regex("\\bcontinue\\b") to "agadi",
        Regex("\\bpass\\b") to "chhoda",
        Regex("\\bprint\\b") to "dekha",
        Regex("\\binput\\b") to "sodha",
        Regex("\\brequests\\.get\\b") to "anurodh.get",
        Regex("\\brequests\\.post\\b") to "anurodh.post"
    )

    private val nepaliToPythonMap = listOf(
        Regex("\\bkaam\\b") to "def",
        Regex("\\bfirta\\b") to "return",
        Regex("\\byedi\\b") to "if",
        Regex("\\bathawa\\b") to "elif",
        Regex("\\bnatabhaye\\b") to "elif",
        Regex("\\bnatra\\b") to "else",
        Regex("\\bjabasamma\\b") to "while",
        Regex("\\bko_lagi\\b") to "for",
        Regex("\\bma\\b") to "in",
        Regex("\\blyau\\b") to "import",
        Regex("\\bbata\\b") to "from",
        Regex("\\bkakshya\\b") to "class",
        Regex("\\bkoshish\\b") to "try",
        Regex("\\bsamau\\b") to "except",
        Regex("\\bantya\\b") to "finally",
        Regex("\\buthau\\b") to "raise",
        Regex("\\bsacho\\b") to "True",
        Regex("\\bjhut\\b") to "False",
        Regex("\\bkhali\\b") to "None",
        Regex("\\bra\\b") to "and",
        Regex("\\bwa\\b") to "or",
        Regex("\\bhoina\\b") to "not",
        Regex("\\brok\\b") to "break",
        Regex("\\bagadi\\b") to "continue",
        Regex("\\bjaari\\b") to "continue",
        Regex("\\bchhoda\\b") to "pass",
        Regex("\\bdekha\\b") to "print",
        Regex("\\bsodha\\b") to "input",
        Regex("\\bleu\\b") to "input",
        Regex("\\banurodh\\.get\\b") to "requests.get",
        Regex("\\banurodh\\.post\\b") to "requests.post"
    )

    fun pythonToNepali(pythonCode: String): String {
        var result = pythonCode
        for ((pattern, replacement) in pythonToNepaliMap) {
            result = result.replace(pattern, replacement)
        }
        return result
    }

    fun nepaliToPython(nepaliCode: String): String {
        var result = nepaliCode
        for ((pattern, replacement) in nepaliToPythonMap) {
            result = result.replace(pattern, replacement)
        }
        return result
    }
}
