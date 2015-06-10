KlseMarketValuationService marketValService = new KlseMarketValuationService()
println "<pre>${marketValService.emailBody}</pre>"

if (params.sendEmail) {
	marketValService.sendEmail()
}
