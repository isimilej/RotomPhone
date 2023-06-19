package com.android.play.rotomphone.data

object Constants {

    val TYPES = mapOf<String, String>(
        "normal" to "노말",
        "fighting" to  "격투",
        "flying" to "비행",
        "poison" to "독",
        "ground" to "땅",
        "rock" to "바위",
        "bug" to "벌레",
        "ghost" to "고스트",
        "steel" to "강철",
        "fire" to "불꽃",
        "water" to "물",
        "grass" to "풀",
        "electric" to "전기",
        "psychic" to "에스퍼",
        "ice" to "얼음",
        "dragon" to "드래곤",
        "dark" to "악",
        "fairy" to "페어리",
        "unknown" to "???",
        "shadow" to "다크"
    )

    val STATS = mapOf<String, String>(
          "hp" to "HP",
          "attack" to "공격",
          "defense" to "방어",
          "special-attack" to "특수공격",
          "special-defense" to "특수방어",
          "speed" to "스피드",
          "accuracy" to "명중률",
          "evasion" to "회피율"
    )

    const val GENERATIONS = """
        {
            "generation-i": "1세대",
            "generation-ii": "2세대",
            "generation-iii": "3세대",
            "generation-iv": "4세대",
            "generation-v": "5세대",
            "generation-vi": "6세대",
            "generation-vii": "7세대",
            "generation-viii": "8세대",
            "generation-ix": "9세대"
        }
    """

    const val VERSIONS = """
        {
            "red": "레드",
            "blue": "블루",
            "yellow": "피카츄",
            "gold": "골드",
            "silver": "실버",
            "crystal": "크리스탈",
            "ruby": "루비",
            "sapphire": "사파이어",
            "emerald": "에메랄드",
            "firered": "파이어레드",
            "leafgreen": "리프그린",
            "diamond": "디아루가",
            "pearl": "펄기아",
            "platinum": "기라티나",
            "heartgold": "하트골드",
            "soulsilver": "소울실버",
            "black": "블랙",
            "white": "화이트",
            "colosseum": "콜로세움",
            "xd": "XD",
            "black-2": "블랙 2",
            "white-2": "화이트 2",
            "x": "X",
            "y": "Y",
            "omega-ruby": "오메가루비",
            "alpha-sapphire": "알파사파이어",
            "sun": "썬",
            "moon": "문",
            "ultra-sun": "울트라썬",
            "ultra-moon": "울트라문",
            "lets-go-pikachu": "레츠고! 피카츄",
            "lets-go-eevee": "레츠고! 이브이",
            "sword": "소드",
            "shield": "실드",
            "the-isle-of-armor": "갑옷의 외딴섬",
            "the-crown-tundra": "왕관의 설원",
            "brilliant-diamond": "브릴리언트 다이아몬드",
            "shining-pearl": "샤이닝 펄",
            "legends-arceus": "LEGENDS 아르세우스"
        }
    """
}