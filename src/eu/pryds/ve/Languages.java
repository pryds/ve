package eu.pryds.ve;

import java.util.Hashtable;

public class Languages {
    public static final String DEFAULT_LANG_CODE = "en"; // use this if not found
    
    public static String getName(String code) {
        if (!langCodeIsSupported(code))
            return getName(DEFAULT_LANG_CODE);
        
        return l.get(code)[0];
    }

    public static int getNplurals(String code) {
        if (!langCodeIsSupported(code))
            return getNplurals(DEFAULT_LANG_CODE);

        return Integer.parseInt(l.get(code)[1]);
    }
    
    public static String getPlural(String code) {
        if (!langCodeIsSupported(code))
            return getPlural(DEFAULT_LANG_CODE);
        
        return l.get(code)[2];
    }
    
    private static boolean langCodeIsSupported(String code) {
        if (code == null || code.length() == 0)
            return false;
        return l.get(code) != null;
    }
    
    private static final Hashtable<String, String[]> l = new Hashtable<String, String[]>() {{
        put("ach",   new String[]{"Acholi",      "2", "(n > 1)"});
        put("af",    new String[]{"Afrikaans",   "2", "(n != 1)"});
        put("ak",    new String[]{"Akan",        "2", "(n > 1)"});
        put("am",    new String[]{"Amharic",     "2", "(n > 1)"});
        put("an",    new String[]{"Aragonese",   "2", "(n != 1)"});
        put("ar",    new String[]{"Arabic",      "6", "(n==0 ? 0 : n==1 ? 1 : n==2 ? 2 : n%100>=3 && n%100<=10 ? 3 : n%100>=11 ? 4 : 5)"});
        put("arn",   new String[]{"Mapudungun",  "2", "(n > 1)"});
        put("ast",   new String[]{"Asturian",    "2", "(n != 1)"});
        put("ay",    new String[]{"Aymará",      "1", "0"});
        put("az",    new String[]{"Azerbaijani", "2", "(n != 1)"});
        put("be",    new String[]{"Belarusian",  "3", "(n%10==1 && n%100!=11 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2)"});
        put("bg",    new String[]{"Bulgarian",   "2", "(n != 1)"});
        put("bn",    new String[]{"Bengali",     "2", "(n != 1)"});
        put("bo",    new String[]{"Tibetan",     "1", "0"});
        put("br",    new String[]{"Breton",      "2", "(n > 1)"});
        put("brx",   new String[]{"Bodo",        "2", "(n != 1)"});
        put("bs",    new String[]{"Bosnian",     "3", "(n%10==1 && n%100!=11 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2)"});
        put("ca",    new String[]{"Catalan",     "2", "(n != 1)"});
        put("cgg",   new String[]{"Chiga",       "1", "0"});
        put("cs",    new String[]{"Czech",       "3", "(n==1) ? 0 : (n>=2 && n<=4) ? 1 : 2"});
        put("csb",   new String[]{"Kashubian",   "3", "n==1 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2"});
        put("cy",    new String[]{"Welsh",       "4", "(n==1) ? 0 : (n==2) ? 1 : (n != 8 && n != 11) ? 2 : 3"});
        put("da",    new String[]{"Danish",      "2", "(n != 1)"});
        put("de",    new String[]{"German",      "2", "(n != 1)"});
        put("goi",   new String[]{"Dogri",       "2", "(n != 1)"});
        put("dz",    new String[]{"Dzongkha",    "1", "0"});
        put("el",    new String[]{"Greek",       "2", "(n != 1)"});
        put("en",    new String[]{"English",     "2", "(n != 1)"});
        put("eo",    new String[]{"Esperanto",   "2", "(n != 1)"});
        put("es",    new String[]{"Spanish",     "2", "(n != 1)"});
        put("es_AR", new String[]{"Argentinean Spanish", "2", "(n != 1)"});
        put("et",    new String[]{"Estonian",    "2", "(n != 1)"});
        put("eu",    new String[]{"Basque",      "2", "(n != 1)"});
        put("fa",    new String[]{"Persian",     "1", "0"});
        put("ff",    new String[]{"Fulah",       "2", "(n != 1)"});
        put("fi",    new String[]{"Finnish",     "2", "(n != 1)"});
        put("fil",   new String[]{"Filipino",    "2", "(n > 1)"});
        put("fo",    new String[]{"Faroese",     "2", "(n != 1)"});
        put("fr",    new String[]{"French",      "2", "(n > 1)"});
        put("fur",   new String[]{"Friulian",    "2", "(n != 1)"});
        put("fy",    new String[]{"Frisian",     "2", "(n != 1)"});
        put("ga",    new String[]{"Irish",       "5", "n==1 ? 0 : n==2 ? 1 : n<7 ? 2 : n<11 ? 3 : 4"});
        put("gd",    new String[]{"Scottish Gaelic", "4", "(n==1 || n==11) ? 0 : (n==2 || n==12) ? 1 : (n > 2 && n < 20) ? 2 : 3"});
        put("gl",    new String[]{"Galician",    "2", "(n != 1)"});
        put("gu",    new String[]{"Gujarati",    "2", "(n != 1)"});
        put("gun",   new String[]{"Gun",         "2", "(n > 1)"});
        put("ha",    new String[]{"Hausa",       "2", "(n != 1)"});
        put("he",    new String[]{"Hebrew",      "2", "(n != 1)"});
        put("hi",    new String[]{"Hindi",       "2", "(n != 1)"});
        put("hne",   new String[]{"Chhattisgarhi", "2", "(n != 1)"});
        put("hy",    new String[]{"Armenian",    "2", "(n != 1)"});
        put("hr",    new String[]{"Croatian",    "3", "(n%10==1 && n%100!=11 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2)"});
        put("hu",    new String[]{"Hungarian",   "2", "(n != 1)"});
        put("ia",    new String[]{"Interlingua", "2", "(n != 1)"});
        put("id",    new String[]{"Indonesian",  "1", "0"});
        put("is",    new String[]{"Icelandic",   "2", "(n%10!=1 || n%100==11)"});
        put("it",    new String[]{"Italian",     "2", "(n != 1)"});
        put("ja",    new String[]{"Japanese",    "1", "0"});
        put("jbo",   new String[]{"Lojban",      "1", "0"});
        put("jv",    new String[]{"Javanese",    "2", "(n != 0)"});
        put("ka",    new String[]{"Georgian",    "1", "0"});
        put("kk",    new String[]{"Kazakh",      "1", "0"});
        put("km",    new String[]{"Khmer",       "1", "0"});
        put("kn",    new String[]{"Kannada",     "2", "(n != 1)"});
        put("ko",    new String[]{"Korean",      "1", "0"});
        put("ku",    new String[]{"Kurdish",     "2", "(n != 1)"});
        put("kw",    new String[]{"Cornish",     "4", "(n==1) ? 0 : (n==2) ? 1 : (n == 3) ? 2 : 3"});
        put("ky",    new String[]{"Kyrgyz",      "1", "0"});
        put("lb",    new String[]{"Letzeburgesch", "2", "(n != 1)"});
        put("ln",    new String[]{"Lingala",     "2", "(n > 1)"});
        put("lo",    new String[]{"Lao",         "1", "0"});
        put("lt",    new String[]{"Lithuanian",  "3", "(n%10==1 && n%100!=11 ? 0 : n%10>=2 && (n%100<10 or n%100>=20) ? 1 : 2)"});
        put("lv",    new String[]{"Latvian",     "3", "(n%10==1 && n%100!=11 ? 0 : n != 0 ? 1 : 2)"});
        put("mai",   new String[]{"Maithili",    "2", "(n != 1)"});
        put("mfe",   new String[]{"Mauritian Creole", "2", "(n > 1)"});
        put("mg",    new String[]{"Malagasy",    "2", "(n > 1)"});
        put("mi",    new String[]{"Maori",       "2", "(n > 1)"});
        put("mk",    new String[]{"Macedonian",  "2", "n==1 || n%10==1 ? 0 : 1"});
        put("ml",    new String[]{"Malayalam",   "2", "(n != 1)"});
        put("mn",    new String[]{"Mongolian",   "2", "(n != 1)"});
        put("mni",   new String[]{"Manipuri",    "2", "(n != 1)"});
        put("mnk",   new String[]{"Mandinka",    "3", "(n==0 ? 0 : n==1 ? 1 : 2)"});
        put("mr",    new String[]{"Marathi",     "2", "(n != 1)"});
        put("ms",    new String[]{"Malay",       "1", "0"});
        put("mt",    new String[]{"Maltese",     "4", "(n==1 ? 0 : n==0 || ( n%100>1 && n%100<11) ? 1 : (n%100>10 && n%100<20 ) ? 2 : 3)"});
        put("my",    new String[]{"Burmese",     "1", "0"});
        put("nah",   new String[]{"Nahuatl",     "2", "(n != 1)"});
        put("nap",   new String[]{"Neapolitan",  "2", "(n != 1)"});
        put("nb",    new String[]{"Norwegian Bokmal", "2", "(n != 1)"});
        put("ne",    new String[]{"Nepali",      "2", "(n != 1)"});
        put("nl",    new String[]{"Dutch",       "2", "(n != 1)"});
        put("se",    new String[]{"Northern Sami", "2", "(n != 1)"});
        put("nn",    new String[]{"Norwegian Nynorsk", "2", "(n != 1)"});
        put("no",    new String[]{"Norwegian (old code)", "2", "(n != 1)"});
        put("nso",   new String[]{"Northern Sotho", "2", "(n != 1)"});
        put("oc",    new String[]{"Occitan",     "2", "(n > 1)"});
        put("or",    new String[]{"Oriya",       "2", "(n != 1)"});
        put("ps",    new String[]{"Pashto",      "2", "(n != 1)"});
        put("pa",    new String[]{"Punjabi",     "2", "(n != 1)"});
        put("pap",   new String[]{"Papiamento",  "2", "(n != 1)"});
        put("pl",    new String[]{"Polish",      "3", "(n==1 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2)"});
        put("pms",   new String[]{"Piemontese",  "3", "(n==1 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2)"});
        put("pt",    new String[]{"Portuguese",  "2", "(n != 1)"});
        put("pt_BR", new String[]{"Brazilian Portuguese", "2", "(n > 1)"});
        put("rm",    new String[]{"Romansh",     "2", "(n != 1)"});
        put("ro",    new String[]{"Romanian",    "3", "(n==1 ? 0 : (n==0 || (n%100 > 0 && n%100 < 20)) ? 1 : 2)"});
        put("ru",    new String[]{"Russian",     "3", "(n%10==1 && n%100!=11 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2)"});
        put("rw",    new String[]{"Kinyarwanda", "2", "(n != 1)"});
        put("sah",   new String[]{"Yakut",       "1", "0"});
        put("sat",   new String[]{"Santali",     "2", "(n != 1)"});
        put("sco",   new String[]{"Scots",       "2", "(n != 1)"});
        put("sd",    new String[]{"Sindhi",      "2", "(n != 1)"});
        put("si",    new String[]{"Sinhala",     "2", "(n != 1)"});
        put("sk",    new String[]{"Slovak",      "3", "(n==1) ? 0 : (n>=2 && n<=4) ? 1 : 2"});
        put("sl",    new String[]{"Slovenian",   "4", "(n%100==1 ? 1 : n%100==2 ? 2 : n%100==3 || n%100==4 ? 3 : 0)"});
        put("so",    new String[]{"Somali",      "2", "(n != 1)"});
        put("son",   new String[]{"Songhay",     "2", "(n != 1)"});
        put("sq",    new String[]{"Albanian",    "2", "(n != 1)"});
        put("sr",    new String[]{"Serbian",     "3", "(n%10==1 && n%100!=11 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2)"});
        put("su",    new String[]{"Sundanese",   "1", "0"});
        put("sw",    new String[]{"Swahili",     "2", "(n != 1)"});
        put("sv",    new String[]{"Swedish",     "2", "(n != 1)"});
        put("ta",    new String[]{"Tamil",       "2", "(n != 1)"});
        put("te",    new String[]{"Telugu",      "2", "(n != 1)"});
        put("tg",    new String[]{"Tajik",       "2", "(n > 1)"});
        put("ti",    new String[]{"Tigrinya",    "2", "(n > 1)"});
        put("th",    new String[]{"Thai",        "1", "0"});
        put("tk",    new String[]{"Turkmen",     "2", "(n != 1)"});
        put("tr",    new String[]{"Turkish",     "2", "(n > 1)"});
        put("tt",    new String[]{"Tatar",       "1", "0"});
        put("ug",    new String[]{"Uyghur",      "1", "0"});
        put("uk",    new String[]{"Ukrainian",   "3", "(n%10==1 && n%100!=11 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2)"});
        put("ur",    new String[]{"Urdu",        "2", "(n != 1)"});
        put("uz",    new String[]{"Uzbek",       "2", "(n > 1)"});
        put("vi",    new String[]{"Vietnamese",  "1", "0"});
        put("wa",    new String[]{"Walloon",     "2", "(n > 1)"});
        put("wo",    new String[]{"Wolof",       "1", "0"});
        put("yo",    new String[]{"Yoruba",      "2", "(n != 1)"});
        put("zh",    new String[]{"Chinese",     "1", "0"});
}};
}
