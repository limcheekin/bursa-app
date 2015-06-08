    Date maxDate = datastore.execute {
        select created: Date from stockValuation
        sort desc by created
        limit 1
    }
    maxDate.clearTime() // remove time portion

	println maxDate.format(Constants.DEFAULT_DATE_TIME_FORMAT, Constants.DEFAULT_TIME_ZONE)