enum KlseScreenerColumn {
    NAME(7),
    CODE(9),
    PRICE(13),
    VOLUME(15),
    EPS(17),
    DPS(19),
    NTA(21),
    PE(23),
    DY(25),
    ROE(27),
    PTBV(29),
    MARKET_CAP(31)

    KlseScreenerColumn(int value) {
        this.value = value
    }
    final int value
}