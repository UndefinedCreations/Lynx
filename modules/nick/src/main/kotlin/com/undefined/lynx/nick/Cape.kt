package com.undefined.lynx.nick

enum class Cape(val texture: String) {

    MIGRATOR("http://textures.minecraft.net/texture/2340c0e03dd24a11b15a8b33c2a7e9e32abb2051b2481d0ba7defd635ca7a933"),
    ANNIVERSARY("http://textures.minecraft.net/texture/cd9d82ab17fd92022dbd4a86cde4c382a7540e117fae7b9a2853658505a80625"),
    VANILLA("http://textures.minecraft.net/texture/f9a76537647989f9a0b6d001e320dac591c359e9e61a31f4ce11c88f207f0ad4"),
    CHERRY_BLOSSOM("http://textures.minecraft.net/texture/afd553b39358a24edfe3b8a9a939fa5fa4faa4d9a9c3d6af8eafb377fa05c2bb"),
    TWITCH("http://textures.minecraft.net/texture/cb40a92e32b57fd732a00fc325e7afb00a7ca74936ad50d8e860152e482cfbde"),
    TIKTOK("http://textures.minecraft.net/texture/569b7f2a1d00d26f30efe3f9ab9ac817b1e6d35f4f3cfb0324ef2d328223d350"),
    MCC("http://textures.minecraft.net/texture/56c35628fe1c4d59dd52561a3d03bfa4e1a76d397c8b9c476c2f77cb6aebb1df"),

    MINECON_2016("http://textures.minecraft.net/texture/e7dfea16dc83c97df01a12fabbd1216359c0cd0ea42f9999b6e97c584963e980"),
    MINECON_2015("http://textures.minecraft.net/texture/b0cc08840700447322d953a02b965f1d65a13a603bf64b17c803c21446fe1635"),
    MINECON_2013("http://textures.minecraft.net/texture/153b1a0dfcbae953cdeb6f2c2bf6bf79943239b1372780da44bcbb29273131da"),
    MINECON_2012("http://textures.minecraft.net/texture/a2e8d97ec79100e90a75d369d1b3ba81273c4f82bc1b737e934eed4a854be1b6"),
    MINECON_2011("http://textures.minecraft.net/texture/953cac8b779fe41383e675ee2b86071a71658f2180f56fbce8aa315ea70e2ed6"),

    REALMS_MAPMAKER("http://textures.minecraft.net/texture/17912790ff164b93196f08ba71d0e62129304776d0f347334f8a6eae509f8a56"),
    MOJANG("http://textures.minecraft.net/texture/5786fe99be377dfb6858859f926c4dbc995751e91cee373468c5fbf4865e7151"),
    MOJANG_STUDIOS("http://textures.minecraft.net/texture/9e507afc56359978a3eb3e32367042b853cddd0995d17d0da995662913fb00f7"),
    MOJANG_CLASSIC("http://textures.minecraft.net/texture/8f120319222a9f4a104e2f5cb97b2cda93199a2ee9e1585cb8d09d6f687cb761"),
    TRANSLATOR("http://textures.minecraft.net/texture/1bf91499701404e21bd46b0191d63239a4ef76ebde88d27e4d430ac211df681e"),
    TRANSLATOR_CHINESE("http://textures.minecraft.net/texture/2262fb1d24912209490586ecae98aca8500df3eff91f2a07da37ee524e7e3cb6"),
    TRANSLATOR_JAPANESE("http://textures.minecraft.net/texture/ca29f5dd9e94fb1748203b92e36b66fda80750c87ebc18d6eafdb0e28cc1d05f"),
    MOJIRA("http://textures.minecraft.net/texture/ae677f7d98ac70a533713518416df4452fe5700365c09cf45d0d156ea9396551"),
    COBALT("http://textures.minecraft.net/texture/ca35c56efe71ed290385f4ab5346a1826b546a54d519e6a3ff01efa01acce81"),
    SCROLLS("http://textures.minecraft.net/texture/3efadf6510961830f9fcc077f19b4daf286d502b5f5aafbd807c7bbffcaca245"),

    TURTLE("http://textures.minecraft.net/texture/5048ea61566353397247d2b7d946034de926b997d5e66c86483dfb1e031aee95"),

    DB("http://textures.minecraft.net/texture/bcfbe84c6542a4a5c213c1cacf8979b5e913dcb4ad783a8b80e3c4a7d5c8bdac"),
    MILLIONTH_CUSTOMER("http://textures.minecraft.net/texture/70efffaf86fe5bc089608d3cb297d3e276b9eb7a8f9f2fe6659c23a2d8b18edf"),
    PRISMARINE("http://textures.minecraft.net/texture/d8f8d13a1adf9636a16c31d47f3ecc9bb8d8533108aa5ad2a01b13b1a0c55eac"),
    SNOWMAN("http://textures.minecraft.net/texture/23ec737f18bfe4b547c95935fc297dd767bb84ee55bfd855144d279ac9bfd9fe"),
    SPADE("http://textures.minecraft.net/texture/2e002d5e1758e79ba51d08d92a0f3a95119f2f435ae7704916507b6c565a7da8"),

    UNKNOWN(""),
    NONE("");

    companion object {
        fun of(texture: String): Cape {
            return entries.firstOrNull { it.texture == texture } ?: UNKNOWN
        }
    }

}