package com.example.traveldiary.domain

import com.example.traveldiary.domain.model.Continent
import java.text.Normalizer
import java.util.Locale

object ContinentResolver {

    private val countriesByContinent: Map<Continent, String> = mapOf(
        Continent.EUROPA to "albania|alemania|germany|andorra|austria|belgica|belgium|bielorrusia|belarus|" +
            "bosnia|bosnia y herzegovina|bosnia and herzegovina|bulgaria|croacia|croatia|chequia|republica checa|" +
            "czech republic|czechia|dinamarca|denmark|eslovaquia|slovakia|eslovenia|slovenia|espana|spain|estonia|" +
            "finlandia|finland|francia|france|grecia|greece|hungria|hungary|irlanda|ireland|islandia|iceland|" +
            "italia|italy|kosovo|letonia|latvia|liechtenstein|lituania|lithuania|luxemburgo|luxembourg|" +
            "macedonia|macedonia del norte|north macedonia|malta|moldavia|moldova|monaco|montenegro|noruega|norway|" +
            "paises bajos|holanda|netherlands|the netherlands|holland|polonia|poland|portugal|reino unido|uk|" +
            "united kingdom|gran bretana|great britain|inglaterra|england|escocia|scotland|gales|wales|" +
            "irlanda del norte|northern ireland|rumania|romania|rusia|russia|san marino|serbia|suecia|sweden|" +
            "suiza|switzerland|ucrania|ukraine|vaticano|ciudad del vaticano|vatican|vatican city",
        Continent.ASIA to "afganistan|afghanistan|arabia saudi|arabia saudita|saudi arabia|armenia|azerbaiyan|" +
            "azerbaijan|banglades|bangladesh|barein|bahrain|birmania|myanmar|burma|brunei|butan|bhutan|camboya|" +
            "cambodia|catar|qatar|china|chipre|cyprus|corea|korea|corea del sur|south korea|corea del norte|" +
            "north korea|emiratos|emiratos arabes unidos|united arab emirates|uae|filipinas|philippines|georgia|" +
            "india|indonesia|irak|iraq|iran|israel|japon|japan|jordania|jordan|kazajistan|kazakhstan|kirguistan|" +
            "kyrgyzstan|kuwait|laos|libano|lebanon|malasia|malaysia|maldivas|maldives|mongolia|nepal|oman|" +
            "pakistan|palestina|palestine|singapur|singapore|siria|syria|sri lanka|tailandia|thailand|taiwan|" +
            "tayikistan|tajikistan|timor oriental|east timor|timor leste|turkmenistan|turquia|turkey|turkiye|" +
            "uzbekistan|vietnam|yemen|hong kong|macao|macau",
        Continent.AMERICA to "antigua y barbuda|antigua and barbuda|argentina|bahamas|barbados|belice|belize|" +
            "bolivia|brasil|brazil|canada|chile|colombia|costa rica|cuba|dominica|ecuador|el salvador|" +
            "estados unidos|estados unidos de america|eeuu|usa|us|united states|united states of america|" +
            "grenada|guatemala|guyana|haiti|honduras|jamaica|mexico|nicaragua|panama|paraguay|peru|puerto rico|" +
            "republica dominicana|dominican republic|san cristobal y nieves|saint kitts and nevis|santa lucia|" +
            "saint lucia|san vicente y las granadinas|saint vincent and the grenadines|surinam|suriname|" +
            "trinidad y tobago|trinidad and tobago|uruguay|venezuela|groenlandia|greenland",
        Continent.AFRICA to "argelia|algeria|angola|benin|botsuana|botswana|burkina faso|burundi|cabo verde|" +
            "cape verde|camerun|cameroon|chad|comoras|comoros|congo|republica democratica del congo|" +
            "democratic republic of the congo|costa de marfil|ivory coast|cote divoire|egipto|egypt|eritrea|" +
            "etiopia|ethiopia|gabon|gambia|ghana|guinea|guinea ecuatorial|equatorial guinea|guinea bisau|" +
            "guinea bissau|kenia|kenya|lesoto|lesotho|liberia|libia|libya|madagascar|malaui|malawi|mali|" +
            "marruecos|morocco|mauricio|mauritius|mauritania|mozambique|namibia|niger|nigeria|ruanda|rwanda|" +
            "santo tome y principe|sao tome and principe|senegal|seychelles|sierra leona|sierra leone|somalia|" +
            "sudafrica|south africa|sudan|sudan del sur|south sudan|suazilandia|esuatini|eswatini|tanzania|togo|" +
            "tunez|tunisia|uganda|yibuti|djibouti|zambia|zimbabue|zimbabwe",
        Continent.OCEANIA to "australia|fiyi|fiji|kiribati|islas marshall|marshall islands|micronesia|nauru|" +
            "nueva zelanda|new zealand|palaos|palau|papua nueva guinea|papua new guinea|samoa|islas salomon|" +
            "solomon islands|tonga|tuvalu|vanuatu",
    )

    private val continentByCountry: Map<String, Continent> =
        countriesByContinent.flatMap { (continent, names) ->
            names.split("|").map { normalize(it) to continent }
        }.toMap()

    fun resolve(country: String): Continent? = continentByCountry[normalize(country)]

    private fun normalize(value: String): String =
        Normalizer.normalize(value, Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")
            .lowercase(Locale.ROOT)
            .replace(Regex("[.'’]"), "")
            .replace(Regex("[^a-z0-9]+"), " ")
            .trim()
}
