KlseMarketValuationService marketValService = new KlseMarketValuationService()
def marketValuations = marketValService.getFbmIndices().values()
println marketValuations
for (marketVal in marketValuations) {
    println "<p>$marketVal</p>"
}