package org.monzon.Wally;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestParams {

    private static final String url = System.getenv("FETCH_URL");

    static List<String> STORES = List.of(
            //30 miles from pittsburgh
            "3738"//, "2588", "4501", "2300", "1739", "5040", "4643", "2281", "4644", "4847", "2059", "4859", "5381", "2611", "1820", "5339", "1885", "2603", "5379", "3223", "1770", "2420", "1883", "3228", "5241", "3838", "4952"
    );

    static List<String> UPCS_TO_IGNORE = List.of(
            "010086770247", "887256113834", "686903989521", "686903133559", "647484112654",
            "616376506093", "605388475572", "196540053160", "194866384395", "194866384234",
            "088496298361", "050743660603", "038675274976", "038675262782", "038675262775",
            "028914585505", "028914580708", "028914550114", "028914192208", "028914190198",
            "026666813709", "025543010668", "021331060181", "021331044365", "021081221306",
            "016337380975", "014633379457", "848061023046", "605388475497", "842776119667",
            "799366894643", "048231029155", "889842893199", "195925363214"
    );

    static List<String> PX = List.of(
            "3:3b5b6a5cb3fe7f31934b5cc1f26eb1ea1c65c2466a27d33536ed0214792c9a3d:iz1BGof+Um991LnQeCRPw48R1l3QgXCgcgzxi9sfrslRoA9ZC8fmRdvZzzYcWtrM9gV+xOA95gjW/ruNnrp8gg==:1000:IF0jbWwou0kVQsVgQBa2Tts9fBKJZTNjUG5v12szgJ0lo194MVfcByp3oGZiRwP8/Qk0Vk8xzvQyBdgljdmx/Qeydb5H6XLbcCmtIPHYC9PS8eHoz5OSOomxlUKpEcNVFT3BvhY786ALSGRrSJiFViUhAjSSZo5Ty5k55qGRqrnUfJNBiXDZWDbx7EN8FuJZv8q4Z8YdCKo8eu8RwE3Rpg==",
            "3:c06f6b9b487cbd4dcaf75a90ab687debc938d5c27e13507f65c329b1e1816b87:m298EPvpTjTtiGdWAsnx50Isfbm7o7Ug4EvsQKUwa4btMmlcyQvh/y26NAGD1G11IIODzDZnnetVMrykoA/SSg==:1000:erXNS03Ut+lKRmuK8bievNtu5f9iwVkg8vcUmoKz1Eg2cZ+aKAmda9Z/5v0oUyGc5pAwMOKTlam1W8dTCaCxdlIOfOzaYMK20+cmd2wwQkzGZQRerfBTwPggDfmxoL3el6e0jEECpu86lbTaqIuJXh73GLG9ALkC1DuLtlPN0fa680H7td0cVtotr8MyRBrYkqGalu+7AELs9RoQVQ7hEA==",
            "3:b94f9b3973c716fa54e1b35519d67610238100295e81c29d4cd2d0dd9807a948:CcdLhdiQc9QRahcGU6+VP44ETDsjr8Gx1Oq8FLAjMtJzVDgrVtuLKFv5Y6irAx8DvK+WY0Kftb3OSfVdRbzj4g==:1000:DQg9Sr8YJJ5iwrpYMp3WuMUiEYhmG4e4sXDkzvwsUVMMZ7lNti2rShYb+zNB15agu0fTjmYL6WF91R7nzc6RuonJT1Mc7ez9lHwF5QxOOaIyS7gHr9zMjMPl5ai2i7K19+nyQZMMiePxjiDztESIokbdczAJaqbOMzC42nT8D6iYq3eZL1BXJyH6+YzhVf/TkuOeomH+olMqkRsKMkyvuA==",
            "3:11003d2a5fe2ab8b2085a208b5dc60aec1117a976564a9cedb62d90d80957150:FTajGbgTrKchXyy6D6ouYGDihWbs2YeejVcsOX6PM1rddez9eoqWQ4T6uVDOe/Y6AsRUBvdAepfMry6GeQD8jw==:1000:AvdMsausEAUyM4sgnoea6icF3szyv7+PU++qdBG84KjhbGeS+UEpUYzGGfJ/WaQsmeYiSabZSwh9TSibNEqavLSLqyBHR4mhnMy2aO7+x4eUwD7CYm2GZ62zI+G6hCzj5wB9IhUtf0r45Sx+AKFCH4dZg9J5asz3ZlO24cmiWJfnoDQsgEPYAgt2J2pLzrPjCDCkLSAbGkIhrIh0oJoKHQ==",
            "3:3cd7fe639cfc3302adfd1065c782abb136d68db023f395580a253488858bbc55:IONTJ9+5pat8OxIsIvc8vHF2AFlBcmh1dz1zc/qJpiUABVMwu4SrPzP+Qti9WWOZ4kw0O/tzhZvDc5whaexZag==:1000:vCmDaUQKEQmlB3w3zgoHUxhsvAa7tld7WIryTTvdfXLLNGLP90hzrHHN5fWxpoLMTivJXHHnGk5oVIDABDhOYecvXfZPgmYDbZpGG1Hlyszh8zzW2jM8trCYYkwdasIOyb2AisUkx5gpOPRDyIdnFP7Cib5z8kdvBtdrfSKSD/MSNZJsa2/l0SjUqxgX8QgpB4WpDAiTlTTs0u/80LkeOg==",
            "3:83fca40359101c62594ce805ccee2f45d7afe2a9d9f3679387e4ad59166524d0:+KjVSOzfdVnCgqWhZ/oKxNKzC2qcu43fyI4qly/hCpbI1c2T8KHuvmxx4zLTM4RTh+MLdXYuk90c9B7diDCfZw==:1000:o4ZRh2bJmwyfD0d9BbzoEc6DrHnWnOLc5AY0Dgmm6Z8X6h8iB1sZdwUwqhcvmI4rjf0lPoO7S8uqUyEWL1yYhThaXJUsR+FaD0NXQ0kPsWfnuGHfn9GcGoLcma7EFpDMXX59w74g0pIpMi+ulvz9YDfGdylvHmuI1SsNsmDy4Mbahy3Sa4GaUlSqcUs5umTfXRlnH0LKWRMGE+9r6tvwFQ==",
            "3:002b0284c7df08fafa73a72c0adff7d341ce3f325b5c13b43ad5c73579c5fa0a:JM5j/6laZhUqVrYJmVq/ZCVv7sUlKf9+iIDXYeA54vqGaL/b5DTUSTbJyOKytpl85hJZTThA96cyKvaQ2iC6PQ==:1000:daKgDMwxpC3+nu+wc5HEhAyxt31FI3r7Z01S1Kzx1mvPq1ng9LU8GKBtkFxnepSC6Jio/fFLwGJfcCyMbBNMJfDgeNCYKfH7ie2OmbIIsbcrBmNnXnzYrSurHP6P2TLS9368Dl0zmQkBTCnMvWBI/lpIZUM0lJafgrmPM0iskMKlIL1UuPAdQn0PtGsPpHIskIfZGqasQ4/+nNtKPhDhJA==",
            "3:08298253777aa3f1f621b8ff37fb4cc4d47bd48f22418329c94e711a305604e3:KJMCj8tDc/Lsc3CgLT9mCC3l43F7b2Au3JLfIiGKGRDdkAV2rtLR8O4xxOc7+/kDwq7LiI6pb6dVIabovnY7ow==:1000:ooZGQKJ/EL+9DMJoXmLspqT7YWi83e5sSW1VkSvH17eWM84p3Lq9mfixNoucl3jrYFtoGezkEITj6d0JqdGdHR6IRr+0HSIUSEf01CNenoVL5YhGkr77Ay8Jeer6lf954jRbvKPVD/RJCU5MXytEFyHYgjxmLV6Ahz4auS+F8l92jygTwBfAowMorOkOxLcjeDEd301y2djtAOmXTZIkCw==",
            "3:9b0716186390c8c970aa95def88953a23e0f02ac316c593ee85755575c806823:Y3y8KScMK1NJybzTqAaedfoTxXnVBIsKqWY+gLCZgDyCCY/ZFTBmrtwaOylXm3JiX6WF4fRMinutLuqu31VPGw==:1000:ZzfQhSSVdlcn+O4sFLMbZ1zHOZ85dHVsq3/RIUI6gAIYqXAO6B1uP5iNwoppKapPpeSfbd4JJsxv5pL/30s70dJTCW0FxPfFNOJKkymXdRDJQ/JircLnp9qUXlxaDbMGJFb9msAsIDKO3OW1uuK2a2XZfnEdPsqOLaZDCbm/5VifwbiGwUvybDRad/jV+oX4/ASbbGhU9GuA1JwAl6AKTw==",
            "3:82079a5249d2dbe2b4353c1230d5412817f0479d892639534d7f213566faaf69:Ggt2MAMRJmOD4S2kCuGPIllR+S/hNUacLaWjrmrJcCdQL+Tv4HYIz9tyo9EqjaBi2iSNiJOycjXhI8dQuDOqcA==:1000:npZnPdPaOQDdAShuw5G+1XDdVieUO7tBMIsJej/h5E4189E/Enbp6NjLIrAkjeYEtzDVxqgBYhiTAda/1di5ugLsDHgfzaCvz95/JNoN5nsZYGnMEibC2r2lgdf4DSHeKxxG43GRDaoGetvxkv7rpsSQXZ08nng/MosYOm81HYgQVZqVylPPWcWAdvnYXz2pSplj2R21XhRAXobp3SqrDg==",
            "3:851512000af52e488174db50b49cc092680bdd9d41e737145da5a19f00ba8e7f:iS9Af6E6PhDLLAeziwTc96Y8i4Hn5B222HZrel6J69wqyO4FHE/8yQR18Zcf6TleagTGEhf73RfPoAhaXSNl9w==:1000:exsqPRzMNUl8/1kRbZ/if9ePjt+bRcXB4NjBhfyqymadiRDu8jgYN9okxcj88ZVDWHdNH/GKYO6TombDgGDZngfbDidNzMHS9V24gqiT+t7d4UUgesdZ+2UmgqBSlmCaqGdkMhBsi5LlhWSD26RuGXF9KKa1udBXIKVsen2x5A6MffeboTu2d/tsLBy7lfaf88kPSkIvvYnhqfPgjQthUw==",
            "3:204224f0daf9ec5eb72e42a606bdc2f76bb0aec8a64dc4e72160a3aa88289a04:OHw7wvuDf5RNBIOFyCLpwwxy09ge02jNw+VYi5TIlOjKdzmfFbJfYYyDWNbxdsO8axHrU5EZh64Tcs9k1MU7uw==:1000:GyMNayLcptvqspBh1vzYcyw/day4U4hx27KoydLhtQ7nqnKQf1emF9+pAz/QUc7d15Fg5IZTzlZU1yCKW5/Fin8keNzANTbzIxw1jUTJ4Djqve4ZibodVg/JP7WlMaB9FdoQdNYx/DwDr+V0UlSGPUJkYHJgelAN8JcWObRjo2uKIsfDMl6DDe9xuEwyJK2XRSU7umAd8eP0AxaO02Drmg==",
            "3:e098307ec03a611a0734a2bd32c8bae8dec0ed1ec033a4d95188c28f5aa56137:g+aq5IsEkIf2d0ZiCmk8KBVIi/XV3cbM8kIixC5dE7n5sbKRJAPtaoEtWHvayz8GMPJ9c3vhxu0lIDWE+AWrbw==:1000:75mgNlj6UWMQXh2tx32TkDpkyI92Zu9RJFD/77vTDSnnTUnJwLaVGJphxdLZJuXScp7lCC05AoWklY9AOpjIua1S7yTUJbZGp2d1P+E1OLx788j/m+eBXeuBsDtBhzV+uiSH8c0DSCkJyqihfWZqoA8Jza/KKD6EVPK38j1oMnfcNZP96pTH8ZFKNjBfrQJeczD51x5B5bWFtXiHSnI/rA==",
            "3:56ea5b12ada17549dced63bd7f15df59267500b80b6020cd69830c40a6845367:oi9GDhuuDMFh/t9RPt+gVqLtXvdE91j9LOqljbjWZT+xhWWGdqNToAu6zee3YTVXQrHktLzqCVj/WRVI30Sgwg==:1000:8ZtsOVVS2Bx296a0C4SnirJx7AwNY4Lo0okxkLVHyZOwjWH33YotZaYKA5TerWb1wlf0xpjhNfEeTDWxysn38HnCirB82Za3nbyQfIXpGuKMTw3Pt+4Zu+UeO2zxqsr+iULU1+bbwLdzDgrzztmQM+GXqIEAhhq33RfLzEsSqD9/5IjnhbFwuGHj0htAsKXtYXPYBveksjvL4Jxy9R374g==",
            "3:413f1e8c6675d86b1d957f8a8e639779aa3f3ee5200a506bae39caec18f9a441:Y3y8KScMK1NJybzTqAaedfoTxXnVBIsKqWY+gLCZgDyCCY/ZFTBmrtwaOylXm3JiX6WF4fRMinutLuqu31VPGw==:1000:ZzfQhSSVdlcn+O4sFLMbZ1zHOZ85dHVsq3/RIUI6gAIYqXAO6B1uP5iNwoppKapPM1hZ77l7TByida42/SvO1g1GFUBkzgCV2zIxiWi0NZYjAGN480pqbUT7dX5e/LyGI5HMXpWswiHVfXcJbHe2p2UJgKvi21OK9422vFTRjOtZ95m6qjQAc5RrMZeH9Uklv9gXDtUwDFFNoeNrA6Q7Mw==",
            "3:93ef1483e540c657aa2cf4657f505453907c780052dd7cdb8b36c0d6c66cf332:77xIGo6cOyNHQ1qYHKQLmiMhajqafIo/xjeq1XpyIi3+wrSLiKmz1fb9EAtEc5ZccWDU6YMviMQYUOoAPFyNCg==:1000:8aCOBRs991XULmc+ihjmgko9aUbnWz+W1M1CH3JUHsk72eV/JuTY+Q63+P0NAmdTb/crponbw/hzPZW4oIEQQPrq7yfvV2e69XaPXqYa20TAYfmJVm46MVFH3tWirvbgHephh4BjYHNuecjayMY9+DAGXywbxop55Z6AXsqI/IrRNx7xtWZOBMwsCRziYvGWYHi0vySzlK7kqsQU89cYug==",
            "3:e1fca829a77f14305a81744757f2fb5adc03807b59ac214dda8e424c1ede3bfe:bX68DkPD7am6IOiJphUfF2zLE9noQr9Mj/cfV33T3v7RKNHbUsK5ZwI1cF/a08YE5OALLPfVYTd2P0pZQ/1IOA==:1000:Gtd0rI4rkIA5MB9N9nRLYIKpeQZeeUNPKiWINiZ6vcTZXNzik9HGRYHUFF1KexoqX3j8qssjfYcUbcKRfeQVDTSl+xsBZyzO1/3os3J5hxq1ipeW5EPaeHb8OLu5aLTchfcp8hYCjxRCFSC6sWzRl4Jcfd7uKvUzbGGWe8KXo0h5AxZr26Kb9TnO/Hv4nWKux+AlF3D59o3gBO75Ba0tVQ==",
            "3:24839bd5064477202fd6390a7e31ac467188218bf2e4d4d5f17673a98d092ed1:EUb2mO5xa0P+QQsVfJ11mtpXakkbXG4HXB5HLqbP/PxsjVC8uYtzKAnicBE4z/gLYtwxDEOcI4snomMsEMqXvw==:1000:L5K3NMEBxLnrL3yFeAKFLByP9GM7SUDXb1KneorrUaTQYoT4aVuhmJA95W1WzZUuDcyvRQRUuFL6loua4yPXy25J0iPXRWSAZnj9lt00mmYoDFc1qAGyfiLNZ0jbQUF3KEy3/2/WieWoOsaDQuICtJnNANJ8VHmzTxsbCQe2X8Au7cgwvH4m8/B3ahKebIFmkOH1DatvSV/9XjKUwsZ2sw==",
            "3:1d23d44de844275dadd25fcb7f66e44b95346942e2a696502cb122dd7e6e9bf0:iS9Af6E6PhDLLAeziwTc96Y8i4Hn5B222HZrel6J69wqyO4FHE/8yQR18Zcf6TleagTGEhf73RfPoAhaXSNl9w==:1000:exsqPRzMNUl8/1kRbZ/if9ePjt+bRcXB4NjBhfyqymadiRDu8jgYN9okxcj88ZVDDOMZVWKdUIDmdQzVuMHvCqFJ1Ss5XxULL/VWQPQDX6Dbb0BRG0gJsd7R9FCyxZcchn3VJdMLXnzYECZ7o1nm7z0CLUadz4UltAuj7dRRvm6fW1/ZfTXCsGlp4ZreFM5vt0AxmbIgYwWgAUzXD5qLPA==",
            "3:576df0b2e685683c67d2cd5240f858a891d20c19593501b3362972a67ef6619a:xrnx0m2U9XTK37TpzSCVRfqiGyHtTvqkrumPZuG6AGCB8hLNEEro0GA+1N2lyf+/saeqG96NGHaCXxAPfeYABw==:1000:Njcz183jtMk4jDMXPw2IwcAlPAu7+UNz0pQJTI56cw0dZGpQb+hJ8FaJPQWY3WvQHRkvbiC6URlyfu8QpwCWbm7+lluGBpjyVqKOGog0Ee+1SjiiOsl4vWEW/Aww9sfmroy686DNGNzxyI0buuJiaJ2D7JpP8eXsFP5upyt7Wc0GTcF1gyepNaiLu85kMl5k+e6WVaA8OlUmphux/3Za4g==",
            "3:0e81f2fe0134217f869af246679db6301f465f0f8f1bf2c0ae73260c92c16d54:YGa1E5wyKqncngixiiEJd8dL/Egc+mB7iS0mkYgokNYxWZh2SnzF+WW3r2F6tHDHIeX9EDmzON4Ssabv3i+04g==:1000:IlocVS6YhvAb08Msrdf8VDh1nSTn9BpT03UlRwRsWkR7n/msk+9NlWTLEmiWD2EbiFXNZ+dOMWRn0V3pv7gXpmRmn4YlOaRsBosYtGuNsLevpihsQ+IAqN0HHHjkKqYctADjtWhybKkXCl+wIkhAnFI9P7xnBk/VcIaP6DA045ArZDoWEblCaZ9ErAhqC7R+2WV2le5GNXJrOL8NK0Rp8Q==",
            "3:da469a584d677d506c681e2438cc27c403cbb85706cb34683965f92cb09054e9:jQXh5Ze/QvFol0eR/HeQumDejM5TXQOJ2tss9hDIVO+TK5oE/s6ebDo/ufamvPq7kUlTGPxDI8eF0RVFkR7lPg==:1000:S+0xASFCmQD7/wIY9g+pRxXPz2tNzhali0lo4KrLI6+/rs267dgM6K/2GnmPWEe5kqLBk1wQU3z9+/6VGR2EUEF7v6vE5qoxTK1AIQ2eCTE9zZ01p9Hr09y+J9lvvRYIy5GhgwJzJY77mEJ20QZqL5cFfBmXsr+06V9iwHsf7jjOLz3L0RBUQAnvwi6vj0WdECugLYCvKgrnHu5/Q2HrDA==",
            "3:5b509493f3ea36e4668662b67e3dcef6cce695e1495dbd2b1b2a34fad5c33ff4:KJMCj8tDc/Lsc3CgLT9mCC3l43F7b2Au3JLfIiGKGRDdkAV2rtLR8O4xxOc7+/kDwq7LiI6pb6dVIabovnY7ow==:1000:ooZGQKJ/EL+9DMJoXmLspqT7YWi83e5sSW1VkSvH17eWM84p3Lq9mfixNoucl3jr4mWpITIUJ8WsnqlR7F22RIRxgy9++oF98zv9bne/VqbmN932oNVTlFpb1vuJ6RENY1lomWFY2ENBKbsnqh86kmHrDMcBxLf4vdt8RP/d3Dw+/9ma0PTtpkGhTS9t64DD4UeoSGtsczx0disqq4RNQg==",
            "3:387fa0f62efca197f646e9d653f797d1679557beef2b5a750806a0dab360da05:A5UKWocZED1K8HgJM2nOZQH2sCy3mDmVIxZM7iRve+sAx2Vkmm/mgFSMiwC5sQ8KGoQsymfBn5cUb/b4DwrueQ==:1000:meONCsTSqul81AsiKvdQWDlI0tcAT7LcAC6mmyKE+u0InRses4t+rpd3+dFJqHJYpLLqjaHjJMtAJDdC1xkECxPWr5F4li04jA36tRpn6AkxQVH88rzJ52C9P+3YaTqDZeolyarOyXEO383t7j4aOtCplfgUMjDOizbL7yKSxDIz3h84kOJd3kW/w90hAonTpktwJ0y0V/joHcIP0VYBmQ==",
            "3:faac5647a25c56790b1d85c97997600b0067a2c397cb790ecceecb28b4617247:iS9Af6E6PhDLLAeziwTc96Y8i4Hn5B222HZrel6J69wqyO4FHE/8yQR18Zcf6TleagTGEhf73RfPoAhaXSNl9w==:1000:exsqPRzMNUl8/1kRbZ/if9ePjt+bRcXB4NjBhfyqymadiRDu8jgYN9okxcj88ZVDVnYXAlSdKwFUTBH2FCEc9W85ypVc3yUn76a3rShNJFplV6PxP5RIXb33j9n4qL1xtgzOZ7a7Plpsfy+KbbcqQM+42LrsByJ7uSek41EY6dbzjm4CwKC2T0UfZLdtPls8gL5fMAbZOFfiUwg0/4fOig==",
            "3:c8d442b103a2f469af521ac78922f3eff2fe0179801d39773b666eeab5a29987:eiODjnsPLR4Go09bKQUSW5NBiA1WSuo1PCx1R16dU3YXygq/WnwpuNdu5yYKhOezTaqUbHJayGAcNn11WEEnWg==:1000:fAGQbtIl9vYHLEXAZmbPNpTHpURB3rbTcku90jpT0rwJ770gv+8aHHSLvtVMdIFqojNU/cN6bvaeakaMF1xUW3fUnOUza4PrLFveS+EyeyrnQFRXaU8WliyYrRsij9uhOznXbDh/kHF8VbIbU9UXudEkxQ20FCSE5qRDFJRft0hAAlYZwhXLo9Ky2fxe/Lf9VAvXcFuJGgfMzM4QuVtb5w==",
            "3:01285c7a7e49df49ff74e3d2f9a97b367eead78c1639d4cd0b4f1ff620922a91:Yi16rw2ZTalp5tJc+0QjhDNe1dUP8hIsgPlEwb4K0A1tziK9dZSYaL6hq5mcqIGMyOGhsrieUJ6O6bxdIqB8Ag==:1000:KXl++Rv1kbbuAXW7oX4NmWoHjhmtZPNbsvcMb1+1wlEFYh85k07IE35L1eiSqFrk4RheCE25VdatlP+u0LrVmsyn7Rb0YstdeaZT2/AGAGYNdZhl9OAW4d3l0sSZeOqNf99Maqswj5w1Gm81i7tq4DNMwZSR5jT1f/3xAKNRIDdz0xgud0yuMi9aMbK6TxYW+hQw14rRkgkIcsvxwvzSeg==",
            "3:6840679a3a91f2206c273002f2bfd7e39850c545c4a7cd54e60df93049e54936:jH8nAa+G8IRdtQcahSRjuSVDYWQoIjfTA42ZimO8u9lMOWS9ZEerfw3E/O5W1kIhnoGNfqNX3r4x5fMBodLyqQ==:1000:rx96tbnNRRGpXB2z+WpEliS3w0AAJzjrICb/fMIfE0YFlcD7loNC2W2+T23ex1vQvWhxxNunvO6hx8uSdudJ2spe+Kw5REi1Yd4bTbOVb16LQZ1O8URCA/FIX0rytY+Db4lKoIKveuQm9HRjA0TKcjIsIaoQ5E0E0F51gS6e+ZWjrGL15Vx0uUd+mp3IsSeXTj3kTFbRrMLyIrpBBIolPA==",
            "3:b0777e7b639ffa70eda8be29f6b577c43903ce8e1cb0c4d7e09af72ef1bf726f:+KjVSOzfdVnCgqWhZ/oKxNKzC2qcu43fyI4qly/hCpbI1c2T8KHuvmxx4zLTM4RTh+MLdXYuk90c9B7diDCfZw==:1000:o4ZRh2bJmwyfD0d9BbzoEc6DrHnWnOLc5AY0Dgmm6Z8X6h8iB1sZdwUwqhcvmI4rtHpAwepWVISPDRqP0rkbrzy3MpjGyf08RvTCLdn6kzhVQvEwOFWS6of95XB59zjhaSbeU+OCm/RTMxFCGDzAx9yeVtXeyPrpPuh8tW3cO3wqPs9u98YPd91uKzIPOrSzWyvOwcmMgGR5qjHFwJfDlw==",
            "3:1dafc3c101a294c53329a411638caea5819a06bdb37afa4da276b3ea0453acc6:w0sqfhIupDg03zexraYSvSDRnk4/KBC4Aa16NX9zYyaqV/l4V8hg9ekzT99i2or9GicddJq5p/RkVErsoLFB2A==:1000:/oCG3TLUw4Il5+7CSNXQVxNsJnPbAJ+YJkkumVCGgbkicyHfcwzsYgsmvS231k9RNM1ptIiKuH+OHpHIzA9UwMGCLzqShP3CJDH2P5EavopH9MplRPlS8hMvmYgC+hIOpEfWu2Z3XLwCcPYFCrbjb0zEyIp/bH4GUudXGU1bFtUUo/cnCO2EHwsjl87noEuJZiTSNHLlGnc1Ziz1zMsqlQ==",
            "3:29afb9f400fbc1c1b4b493182f8ae923bbc52330826f7e1db8ed18998527974f:oUv+vJ6HeOjCcteUi1CEV7UIBTcDPbPFc9xf3SWHlPXMwNAt5z26JpCaF/IK6/VldbLI76NmjqD33b14/Gy3bQ==:1000:VAG+/2QiJn817yyzf0v8K2xpHKVnL+9SQiHM0ZV5R7Nr1YBwyuhoF92eEnHi+Mo+RaNLPMqrlnGDU7fcf5DIKL54XbuTtuoBmfBbMWOa9SFj5Jsq9Qqrr4Ryk2u/Da0EBvhoMCwvfmoHbT6cJnco3NdmV2i6dH8KeOmkP0DRO9im4CLrkq8BwZiuQh+C/n6Rt4qyP+Qv6iAS5lIIFr7bcg==",
            "3:e656c336836567a9964f8906d6128bdf293c452626e34b2ce3af713872dbc837:95eiRMOtVPalYv2Hw5aZkWMd3hASgUhsKYqEGUapLXVopQRliwsfo4qAq0cOh7j/s3Doi5mfRPYHV2KJsvyCaQ==:1000:vBgVSzxVvhupUEcM4545EIOEhhV5p6xJAxKBrw/AvYRW/Sf/B45585fl1bGp44FZXjPAIq9GMQajhsjR2nX9+BwgPxwGviJ+IWp2zBSn1s+7mBpFMxqB+qdBZDNOvxddQLjvjTbJxBaQCPJ5kgxi4T7Yv1A+EkGK6TBV1rVK3bTye8cZhYrPNFTyvyBC5UOJbYDbo2banS/4zVO3Rv8HWw==",
            "3:83d2f1a05af59e37934d95ceecdf3a9386da629dd612bc37a07f5741e952d9a3:pJ6OUoykhP40M13B/D/I95ppwmUQtrwMzmZzfbQu8sPRnuxeVBcnt9UwjxzzQPK0XIARZ5SkEse4QPpg6M3vbg==:1000:+GZKWUMLhbtflXlplll83yzz+XSehwqKFaBaCpey1eQKgp6NmP25z7amCINT4IDou4PDcsOQNLhHmdOeGbeG2su7QPW8WIwZ0Kj6MwV723o8fPOjRo2at+e/K1mLgexGUSCXXoRast64D7zZ4nxdTXHdalqohRjCOMXZrGiC7nawAyLHyjPKaAIETt6b6JeSj0sGIY1YzqLvb2lldWIixQ=="
    );

    static List<Map<String, String>> getReqHeaders() {
        List<Map<String, String>> REQ_DATA = new ArrayList<>();
        
        Map<String, String> data1 = new HashMap<>();
        data1.put("Accept-Encoding", "gzip, deflate, br");
        data1.put("X-PX-AUTHORIZATION", "3");
        data1.put("x-o-platform-version", "23.40.1");
        data1.put("x-wm-client-name", "glass");
        data1.put("DEVICE_PROFILE_REF_ID", "329745AE-C60E-47B1-80F4-5CCDBD5C2281");
        data1.put("x-o-bu", "WALMART-US");
        data1.put("x-enable-server-timing", "1");
        data1.put("x-o-device", "iPhone12,8");
        data1.put("x-wm-sid", "49024B1E-6DBB-431C-AACF-48D9B737B7BE");
        data1.put("x-latency-trace", "1");
        data1.put("Accept-Language", "en-US");
        data1.put("x-apollo-operation-name", "getScannedProductSubstitutions");
        data1.put("WM_MP", "true");
        data1.put("x-o-segment", "oaoh");
        data1.put("Connection", "keep-alive");
        data1.put("x-o-fuzzy-install-date", "1697000000000");
        data1.put("x-wm-vid", "3CD972C8-1CAB-41B8-9E1B-DCA742C25228");
        data1.put("x-o-tp-phase", "tp5");
        data1.put("x-apollo-operation-id", "4ad12f8838f6c60c2ba1b5bb01bc20a363f2b82e7a7e2d89f9b0a99870d01c88");
        data1.put("traceparent", "00-ae207639b8b4ef21b694e22e0eea2798-9ba14b6c1d432822-00");
        data1.put("x-o-platform", "ios");
        data1.put("Host", "www.walmart.com");
        data1.put("x-o-device-id", "33EEA20C-9001-45AB-9E3A-7D1AFCE04B8D");
        data1.put("User-Agent", "WMT1H/23.40.1 iOS/15.4.1");
        data1.put("x-o-mart", "B2C");
        data1.put("X-PX-ORIGINAL-TOKEN", "3:b52fdbd940e56d3d4cbe014096613d6d8f222b4d2255a85e035f1080e118e054:ImNvl7155+Zw9WS/ZxTVQIOuRrRVBrnRl5sgMiqNkVpsUBHUZXEV9EkzZi+BZjrFCyioYe1EeNKeuBtN3fZx9g==:1000:tIdR8XevrxSc/mrpAFLCwmL8LsMiGuboO1OnxTnjND3pYJFvu2JiuNanmLmSlFcMAzyi+zAamQw3shHgJJkkv0+BEs515jqLCDlZ2wwdYCfdlyZb6AQTwdM6j5rQpwyHx3rmc5KZU8tyaFXbtMIfCdo0MPSsp/4kamRjXR54FdYKx/1to1dXlVebQBqeSTknaPy8QVDuGYtToMmUWV4tiQ==");
        data1.put("Cookie", "xpwqd=1; TS01a90220=016ec065911e1d3dfad813d5900e8fc5e06f25aa4909712824f489c6d2496fee9cdd6dce7aa7b89f6e0cc5722a939ff2faac668d4e; bstc=eIZxMA8ZJuzqWgxQByL7IU; mobileweb=0; vtc=eIZxMA8ZJuzqWgxQByL7IU; xptwg=3653595505:164CA1F03AAB6B0:3878329:10D24C7:7DCB1867:21884B11:; TS012768cf=016ec065911e1d3dfad813d5900e8fc5e06f25aa4909712824f489c6d2496fee9cdd6dce7aa7b89f6e0cc5722a939ff2faac668d4e; abqme=true; akavpau_p2=1696047585~id=d444556623eb1a574ed766b08f07dcb3; xptwj=qq:66bdcee00ae0740f00fa:h1kqnrHPfehELECILhmwjADFGLdj2+cajWib94727licWDkM6y7z7l2vM04Xf2dDDECCq/hIGzchiWZri34eCaJkGtweP4vFhHnbVa4gnIsiA6aTi6uvIMDHbL+GOFjmzoadtMnsRYUfZm8EUwN5Cl85oAAQ; xpa=-X2ph|00mLv|0ZznN|0sRT3|5OFUf|6fZdk|7MHUg|7OWQ2|7xlHU|84dDY|97_Ru|DYky4|Ewana|FCR5B|HDtn-|HowsY|IYlDj|JQlMl|ML6eh|NwI_X|Of_p_|RNA9_|RX1FJ|WnvNk|Zr8Ca|_jbvZ|am0Uz|bKOq0|g1yHI|gQmK1|gfFu8|hfLe3|jOFoR|kAElD|mb2G8|o4LWG|o5wPm|oPbIt|r3sXj|r_iZ8|uKgF1|zxobC; xpm=-2%2B1696046973%2B%2B; _m=9; akavpau_p1=1696047568~id=b368902bb478ecb2b6e9852419a358a5; ACID=56983cd7-4f9c-4b91-bec7-8fd8066517ec; auth=MTAyOTYyMDE4LwUt61tVHhpRES%2FCraAAFYxL2BXIhwqg6HgomP0wkRTpGrZiY2COIp2RN8qhno7Xl71meeEjT4PizCJJX%2B7XtB%2FyUc%2F0fzN8Drgz1qhTBJP%2B%2BkqceED6vASEUSuCOs9x767wuZloTfhm7Wk2KcjygkeeSCv4Chv5IarMOQ7pqjeUcXT%2BAwbVAv%2BGg2KzT%2FQyU22STQFDjra7CYXgA1CCTouc7euKl6D2twniko2voHYUMk70P8glgOEpLOprhDfMDCcb9mgycy9jtT1uIyOBHarvnaHKDVTEV6Fsknp9SZmOQKURMvHBapgxP5jUmjj2xS3%2FNmzq88OflTe%2F53SCatFojkbzTylCxOJgjYiPch%2Fyt3bBDgxzC0xOjNcAMNQZta%2FfN7oQJT6NXS4F4XeDF5E5WBBdZBCyKnCQAR7o6eg%3D; hasACID=true; locDataV3=eyJpc0RlZmF1bHRlZCI6ZmFsc2UsImlzRXhwbGljaXQiOmZhbHNlLCJpbnRlbnQiOiJTSElQUElORyIsInBpY2t1cCI6W3siYnVJZCI6IjAiLCJub2RlSWQiOiIzNDQ3IiwiZGlzcGxheU5hbWUiOiJXaGl0ZWhhbGwgU3VwZXJjZW50ZXIiLCJub2RlVHlwZSI6IlNUT1JFIiwiYWRkcmVzcyI6eyJwb3N0YWxDb2RlIjoiNDMyMTMiLCJhZGRyZXNzTGluZTEiOiIzNjU3IEUgTWFpbiBTdCIsImNpdHkiOiJXaGl0ZWhhbGwiLCJzdGF0ZSI6Ik9IIiwiY291bnRyeSI6IlVTIiwicG9zdGFsQ29kZTkiOiI0MzIxMy0yOTI0In0sImdlb1BvaW50Ijp7ImxhdGl0dWRlIjozOS45NTM2NjIsImxvbmdpdHVkZSI6LTgyLjkwMDQwOX0sImlzR2xhc3NFbmFibGVkIjp0cnVlLCJzY2hlZHVsZWRFbmFibGVkIjp0cnVlLCJ1blNjaGVkdWxlZEVuYWJsZWQiOnRydWUsImh1Yk5vZGVJZCI6IjM0NDciLCJzdG9yZUhycyI6IjA2OjAwLTIzOjAwIiwic3VwcG9ydGVkQWNjZXNzVHlwZXMiOlsiUElDS1VQX0JBS0VSWSIsIkFDQyIsIlBJQ0tVUF9TUEVDSUFMX0VWRU5UIiwiQUNDX0lOR1JPVU5EIiwiUElDS1VQX0NVUkJTSURFIiwiUElDS1VQX0lOU1RPUkUiXSwic2VsZWN0aW9uVHlwZSI6IkxTX1NFTEVDVEVEIn1dLCJzaGlwcGluZ0FkZHJlc3MiOnsibGF0aXR1ZGUiOjM5Ljk5MTEsImxvbmdpdHVkZSI6LTgzLjAwMjgsInBvc3RhbENvZGUiOiI0MzIwMSIsImNpdHkiOiJDb2x1bWJ1cyIsInN0YXRlIjoiT0giLCJjb3VudHJ5Q29kZSI6IlVTQSIsImxvY2F0aW9uQWNjdXJhY3kiOiJsb3ciLCJnaWZ0QWRkcmVzcyI6ZmFsc2UsInRpbWVab25lIjoiQW1lcmljYS9OZXdfWW9yayJ9LCJhc3NvcnRtZW50Ijp7Im5vZGVJZCI6IjM0NDciLCJkaXNwbGF5TmFtZSI6IldoaXRlaGFsbCBTdXBlcmNlbnRlciIsImludGVudCI6IlBJQ0tVUCJ9LCJpbnN0b3JlIjpmYWxzZSwiZGVsaXZlcnkiOnsiYnVJZCI6IjAiLCJub2RlSWQiOiIzNDQ3IiwiZGlzcGxheU5hbWUiOiJXaGl0ZWhhbGwgU3VwZXJjZW50ZXIiLCJub2RlVHlwZSI6IlNUT1JFIiwiYWRkcmVzcyI6eyJwb3N0YWxDb2RlIjoiNDMyMTMiLCJhZGRyZXNzTGluZTEiOiIzNjU3IEUgTWFpbiBTdCIsImNpdHkiOiJXaGl0ZWhhbGwiLCJzdGF0ZSI6Ik9IIiwiY291bnRyeSI6IlVTIiwicG9zdGFsQ29kZTkiOiI0MzIxMy0yOTI0In0sImdlb1BvaW50Ijp7ImxhdGl0dWRlIjozOS45NTM2NjIsImxvbmdpdHVkZSI6LTgyLjkwMDQwOX0sImlzR2xhc3NFbmFibGVkIjp0cnVlLCJzY2hlZHVsZWRFbmFibGVkIjp0cnVlLCJ1blNjaGVkdWxlZEVuYWJsZWQiOnRydWUsImFjY2Vzc1BvaW50cyI6W3siYWNjZXNzVHlwZSI6IkRFTElWRVJZX0FERFJFU1MifV0sImh1Yk5vZGVJZCI6IjM0NDciLCJpc0V4cHJlc3NEZWxpdmVyeU9ubHkiOmZhbHNlLCJzdXBwb3J0ZWRBY2Nlc3NUeXBlcyI6WyJERUxJVkVSWV9BRERSRVNTIiwiQUNDIl0sInNlbGVjdGlvblR5cGUiOiJMU19TRUxFQ1RFRCJ9LCJyZWZyZXNoQXQiOjE2OTYwNTA1NTAwNzUsInZhbGlkYXRlS2V5IjoicHJvZDp2Mjo1Njk4M2NkNy00ZjljLTRiOTEtYmVjNy04ZmQ4MDY2NTE3ZWMifQ%3D%3D; locGuestData=eyJpbnRlbnQiOiJTSElQUElORyIsImlzRXhwbGljaXQiOmZhbHNlLCJzdG9yZUludGVudCI6IlBJQ0tVUCIsIm1lcmdlRmxhZyI6ZmFsc2UsImlzRGVmYXVsdGVkIjpmYWxzZSwicGlja3VwIjp7Im5vZGVJZCI6IjM0NDciLCJ0aW1lc3RhbXAiOjE2OTYwNDY5NTAwNzEsInNlbGVjdGlvblR5cGUiOiJMU19TRUxFQ1RFRCJ9LCJzaGlwcGluZ0FkZHJlc3MiOnsidGltZXN0YW1wIjoxNjk2MDQ2OTUwMDcxLCJ0eXBlIjoicGFydGlhbC1sb2NhdGlvbiIsImdpZnRBZGRyZXNzIjpmYWxzZSwicG9zdGFsQ29kZSI6IjQzMjAxIiwiY2l0eSI6IkNvbHVtYnVzIiwic3RhdGUiOiJPSCIsImRlbGl2ZXJ5U3RvcmVMaXN0IjpbeyJub2RlSWQiOiIzNDQ3IiwidHlwZSI6IkRFTElWRVJZIiwidGltZXN0YW1wIjoxNjk2MDQ0NTY0MzU1LCJzZWxlY3Rpb25UeXBlIjoiTFNfU0VMRUNURUQiLCJzZWxlY3Rpb25Tb3VyY2UiOiJURVRIRVJFRCJ9XX0sInBvc3RhbENvZGUiOnsidGltZXN0YW1wIjoxNjk2MDQ2OTUwMDcxLCJiYXNlIjoiNDMyMDEifSwibXAiOltdLCJ2YWxpZGF0ZUtleSI6InByb2Q6djI6NTY5ODNjZDctNGY5Yy00YjkxLWJlYzctOGZkODA2NjUxN2VjIn0%3D; assortmentStoreId=3447; hasLocData=1; ak_bmsc=A867F4EC83D06E911664BD4DE1DF88EE~000000000000000000000000000000~YAAQjsPBF4oBLs+KAQAA/6NG5BXNFmHxITF6UGIOn4dM7O0nfWOXFr+uxXapSet5kUV0vhXNwB0ZKdt8gi66Vc2mEzwKtuBnuJoxQWRf5woFn3rKWv0O68n9KKL1VcU55gBAKMlzASIq1ZtdTfr1uOhfbX2UMzyksCC4DFbbOh59ZW+5ur7JWWNXnAPC6vi1dq3JfAIkTNPZCIItncIesWTYbkIY9UBCVilTGam5exeyA2DSQ8TTIq0wDd5Y0v9NOjM1r4DFgX5/ht1d7wsbMenWRF/Ddo9pMJH7FFz7GXC1MKQpedIiZEl3Q7G65IIMJTQZX/ZG2U0Itr5W0N7awwMrrSKYao6O/REq9Ax+dcMIWjFY/vUOC/5BcYxpiYykSwgogkhKefWICFGK");
        data1.put("Accept", "*/*");

        Map<String, String> data2 = new HashMap<>();
        data2.put("x-o-device", "iPhone12,8");
        data2.put("x-latency-trace", "1");
        data2.put("User-Agent", "WMT1H/23.40.1 iOS/15.4.1");
        data2.put("x-apollo-operation-name", "getScannedProductSubstitutions");
        data2.put("Accept", "*/*");
        data2.put("Accept-Encoding", "gzip, deflate, br");
        data2.put("X-PX-AUTHORIZATION", "3");
        data2.put("x-enable-server-timing", "1");
        data2.put("Cookie", "xpwqd=1; TS01a90220=01428e59341b5fc271eef2807a7282349dfae3991c2758d2df90065500a83d58360fa0084d45e8c756151685ef8b4a0da198c10d8e; bstc=WYl0OyUFcpSAY4CEXs3qWo; mobileweb=0; vtc=WYl0OyUFcpSAY4CEXs3qWo; xptwg=3113606285:24553D43061D060:5C164A4:7A18D347:D3CC2EF6:F548A0A4:; TS012768cf=01428e59341b5fc271eef2807a7282349dfae3991c2758d2df90065500a83d58360fa0084d45e8c756151685ef8b4a0da198c10d8e; abqme=true; akavpau_p2=1694580524~id=9bb72959c4457ee6980665c7b8befbea; xptwj=qq:872830e64fc984b4bfca:qKae4vt0eYDxdYe3VGnKrY1CWSEdGmPNq5wXLjhWms301VBSgJLjm6NbDgp9FEs6y1z2VlKP63k4V91Etoz9vSBoiBELOCaWQmsLt3Hccc1t9sK+tyHcPRJI9AQ8LtXDHVVCO1zFSsT2wkx+elm3ED2ew4ud; xpa=18CNW|6fZdk|7LdHa|8_5Y5|J47Jt|JQlMl|LOEfC|PUr1t|WLjVh|WnvNk|bKOq0|e5eiR|glmTb|jqj_w|kLQGa|knpdw|oPbIt|pRDuY|pxrpN|r_Svc|sXodL|snSUK|uMgT9|vPoIZ|y60-5|zk_gc; xpm=-2%2B1694579921%2B%2B; _m=9; akavpau_p1=1694580516~id=7420d29ec3e6534854a1eacab7e14cd2; ACID=eea196d0-2a23-4061-bc39-a77d879325ab; assortmentStoreId=2926; auth=MTAyOTYyMDE4T6Fg3CpAJuFLOjYofwH5DkXTzvMbVpDRqRLG20BPCFgZdmk0g4noxkbrwfbqqBJA%2FF4zlaHAWtFm%2FlrgOLe03CTZPsz2YNdfkr6bgfx%2BJFyn8edzsNXeLMvl068d%2FeRS767wuZloTfhm7Wk2Kcjygi5k0VvBM%2FJjwcKWWhCnBS%2Be%2Bu79sO1KDCFRbcwLQPI7CBPTlNUZy2urJn56TUTotkseIOAghR8d6o8qe4ycwEsUMk70P8glgOEpLOprhDfMDCcb9mgycy9jtT1uIyOBHWrxp22zRz9VVbgudj1FNmADWB9tKuWkdKcigbwIJPo61VCMgV6xhZ8KDfKol3MrMeVCrn9fJoJ0HU0clYUMK%2BhrXSaxShOMOinZySzVHDeHqjclnoSpv9nOrOu%2FVxg0z5E5WBBdZBCyKnCQAR7o6eg%3D; hasACID=true; hasLocData=1; locDataV3=eyJpc0RlZmF1bHRlZCI6ZmFsc2UsImlzRXhwbGljaXQiOmZhbHNlLCJpbnRlbnQiOiJTSElQUElORyIsInBpY2t1cCI6W3siYnVJZCI6IjAiLCJub2RlSWQiOiIyOTI2IiwiZGlzcGxheU5hbWUiOiJQbGFubyBTdXBlcmNlbnRlciIsIm5vZGVUeXBlIjoiU1RPUkUiLCJhZGRyZXNzIjp7InBvc3RhbENvZGUiOiI3NTAyMyIsImFkZHJlc3NMaW5lMSI6IjYwMDAgQ29pdCBSZCIsImNpdHkiOiJQbGFubyIsInN0YXRlIjoiVFgiLCJjb3VudHJ5IjoiVVMiLCJwb3N0YWxDb2RlOSI6Ijc1MDIzLTU5MDMifSwiZ2VvUG9pbnQiOnsibGF0aXR1ZGUiOjMzLjA1NTM1OCwibG9uZ2l0dWRlIjotOTYuNzY3NjM1fSwiaXNHbGFzc0VuYWJsZWQiOnRydWUsInNjaGVkdWxlZEVuYWJsZWQiOnRydWUsInVuU2NoZWR1bGVkRW5hYmxlZCI6dHJ1ZSwiaHViTm9kZUlkIjoiMjkyNiIsInN0b3JlSHJzIjoiMDY6MDAtMjM6MDAiLCJzdXBwb3J0ZWRBY2Nlc3NUeXBlcyI6WyJQSUNLVVBfQ1VSQlNJREUiLCJQSUNLVVBfU1BFQ0lBTF9FVkVOVCIsIlBJQ0tVUF9CQUtFUlkiLCJQSUNLVVBfSU5TVE9SRSJdLCJzZWxlY3Rpb25UeXBlIjoiTFNfU0VMRUNURUQifV0sInNoaXBwaW5nQWRkcmVzcyI6eyJsYXRpdHVkZSI6MzMuMDU2MSwibG9uZ2l0dWRlIjotOTYuNzMzNCwicG9zdGFsQ29kZSI6Ijc1MDIzIiwiY2l0eSI6IlBsYW5vIiwic3RhdGUiOiJUWCIsImNvdW50cnlDb2RlIjoiVVNBIiwibG9jYXRpb25BY2N1cmFjeSI6ImxvdyIsImdpZnRBZGRyZXNzIjpmYWxzZSwidGltZVpvbmUiOiJBbWVyaWNhL0NoaWNhZ28ifSwiYXNzb3J0bWVudCI6eyJub2RlSWQiOiIyOTI2IiwiZGlzcGxheU5hbWUiOiJQbGFubyBTdXBlcmNlbnRlciIsImludGVudCI6IlBJQ0tVUCJ9LCJpbnN0b3JlIjpmYWxzZSwiZGVsaXZlcnkiOnsiYnVJZCI6IjAiLCJub2RlSWQiOiIyOTI2IiwiZGlzcGxheU5hbWUiOiJQbGFubyBTdXBlcmNlbnRlciIsIm5vZGVUeXBlIjoiU1RPUkUiLCJhZGRyZXNzIjp7InBvc3RhbENvZGUiOiI3NTAyMyIsImFkZHJlc3NMaW5lMSI6IjYwMDAgQ29pdCBSZCIsImNpdHkiOiJQbGFubyIsInN0YXRlIjoiVFgiLCJjb3VudHJ5IjoiVVMiLCJwb3N0YWxDb2RlOSI6Ijc1MDIzLTU5MDMifSwiZ2VvUG9pbnQiOnsibGF0aXR1ZGUiOjMzLjA1NTM1OCwibG9uZ2l0dWRlIjotOTYuNzY3NjM1fSwiaXNHbGFzc0VuYWJsZWQiOnRydWUsInNjaGVkdWxlZEVuYWJsZWQiOnRydWUsInVuU2NoZWR1bGVkRW5hYmxlZCI6dHJ1ZSwiYWNjZXNzUG9pbnRzIjpbeyJhY2Nlc3NUeXBlIjoiREVMSVZFUllfQUREUkVTUyJ9XSwiaHViTm9kZUlkIjoiMjkyNiIsImlzRXhwcmVzc0RlbGl2ZXJ5T25seSI6ZmFsc2UsInN1cHBvcnRlZEFjY2Vzc1R5cGVzIjpbIkRFTElWRVJZX0FERFJFU1MiXSwic2VsZWN0aW9uVHlwZSI6IkxTX1NFTEVDVEVEIn0sInJlZnJlc2hBdCI6MTY5NDU4MzUwNzU2OSwidmFsaWRhdGVLZXkiOiJwcm9kOnYyOmVlYTE5NmQwLTJhMjMtNDA2MS1iYzM5LWE3N2Q4NzkzMjVhYiJ9; locGuestData=eyJpbnRlbnQiOiJTSElQUElORyIsImlzRXhwbGljaXQiOmZhbHNlLCJzdG9yZUludGVudCI6IlBJQ0tVUCIsIm1lcmdlRmxhZyI6ZmFsc2UsImlzRGVmYXVsdGVkIjpmYWxzZSwicGlja3VwIjp7Im5vZGVJZCI6IjI5MjYiLCJ0aW1lc3RhbXAiOjE2OTQ1Nzk5MDc1NjcsInNlbGVjdGlvblR5cGUiOiJMU19TRUxFQ1RFRCJ9LCJzaGlwcGluZ0FkZHJlc3MiOnsidGltZXN0YW1wIjoxNjk0NTc5OTA3NTY3LCJ0eXBlIjoicGFydGlhbC1sb2NhdGlvbiIsImdpZnRBZGRyZXNzIjpmYWxzZSwicG9zdGFsQ29kZSI6Ijc1MDIzIiwiY2l0eSI6IlBsYW5vIiwic3RhdGUiOiJUWCIsImRlbGl2ZXJ5U3RvcmVMaXN0IjpbeyJub2RlSWQiOiIyOTI2IiwidHlwZSI6IkRFTElWRVJZIiwidGltZXN0YW1wIjoxNjk0NTcwNDc0MjMwLCJzZWxlY3Rpb25UeXBlIjoiTFNfU0VMRUNURUQiLCJzZWxlY3Rpb25Tb3VyY2UiOiJURVRIRVJFRCJ9XX0sInBvc3RhbENvZGUiOnsidGltZXN0YW1wIjoxNjk0NTc5OTA3NTY3LCJiYXNlIjoiNzUwMjMifSwibXAiOltdLCJ2YWxpZGF0ZUtleSI6InByb2Q6djI6ZWVhMTk2ZDAtMmEyMy00MDYxLWJjMzktYTc3ZDg3OTMyNWFiIn0%3D; ak_bmsc=451456238DB05100F3E0BD5C9904B56A~000000000000000000000000000000~YAAQ0UJ1aIBA0lGKAQAAFVnVjBU9dOyLGI0443FgkBDv/u2e/2BmKVLlhH84OrDIMipoaLGiglZWCUgR1bSxKM7XFGZwuPW4nmp8VOQ5Wnsq7s5PWoAJ7pqr5F/Q/imRM35hasBBJFZlszQ8/RBciuCY6bXm06wCZH/M40MEc2PhL/tBs1LmT++hT3IiMi3InxezEWXu3tIhu74arzLKQc6QoY5UKp2uAPGHPrssHci14/nQAvPMxI+GQ2/GvJIStU8sNiKsjE+udE/ef1u/SeUBzwMN6e3JGXC+UiRjyMOvbAU8q3J/vvjOQj//jDAeYZOdllAVofdjbhjHppwKhPGYb64Dx204xQ98E+0SCAlJAWiu3uCDYLICS8/zoZ0O67U4bi9ot89ost7G");
        data2.put("X-PX-ORIGINAL-TOKEN", "3:7c94f308ab702f22bfabc19a2f4ca096ec4227a78fd34a0df989551df6ec4fbe:6PJWhmpvxcomElDBEjWWh0RaVnKIUO6lSBf/h3cw3FTVuFTD5sF4BP4v2M4QY0MqeuL+H6E2aqq9HtzfzGNdIQ==:1000:KLemDYVeGozoqo/fIeT7Pw7UuO3qxfwyPIUhdtQfGvnVwXtpSNg2jKI0nv/UYw+k22mJneYlcAm+dx80LlgEV7Uy9VcdLhXIuXBX0VqadBDvm5b3PDdjfFjFxuE72pppuJ2qfyHyppTF+Crx6vRrZzS8MW5o5z46k7cPOSuXVmc+HBjIiC3/xbY5hc8Kbk2xbuqA9Xv3w5sN3QWzfywrwQ==");
        data2.put("WM_MP", "true");
        data2.put("x-wm-client-name", "glass");
        data2.put("x-o-platform", "ios");
        data2.put("x-o-mart", "B2C");
        data2.put("x-o-platform-version", "23.40.1");
        data2.put("x-wm-sid", "90A73743-DFEF-473F-B3AB-A0D5C4821CF5");
        data2.put("Accept-Language", "en-US");
        data2.put("DEVICE_PROFILE_REF_ID", "B9B6B4DD-5F63-493D-9E3A-CA8477CC6027");
        data2.put("x-wm-vid", "784E5CC2-2A20-4052-AD1C-25388449BE49");
        data2.put("Host", "www.walmart.com");
        data2.put("x-apollo-operation-id", "4ad12f8838f6c60c2ba1b5bb01bc20a363f2b82e7a7e2d89f9b0a99870d01c88");
        data2.put("x-o-segment", "oaoh");
        data2.put("x-o-fuzzy-install-date", "1697000000000");
        data2.put("traceparent", "00-16630dd8fd8b055eb909d0a05c079a2a-247d14bcebf144fc-00");
        data2.put("x-o-tp-phase", "tp5");
        data2.put("Connection", "keep-alive");
        data2.put("x-o-bu", "WALMART-US");
        data2.put("x-o-device-id", "E2B7270C-18F3-40E1-99E2-51289BE97D20");

        REQ_DATA.add(data2);
        REQ_DATA.add(data1);
        return REQ_DATA;
    }

    static List<ProxyCreds> PROXIES = List.of(
            new ProxyCreds("172.245.158.75","","", 6028),
            new ProxyCreds("23.236.170.34","","", 9067),
            new ProxyCreds("45.56.173.122","","", 6105),
            new ProxyCreds("104.238.50.134","","", 6680)
    );

    static String getUrl(String upc){
        return url.replace("REPLACE", upc);
    }
}
