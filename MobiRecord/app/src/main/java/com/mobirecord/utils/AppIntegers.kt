package com.mobirecord.utils

interface AppIntegers {
    class ResultCode {
        companion object {
            val permissions = 11
            val RC_SIGN_IN = 100

        }
    }

    class Constants {
        companion object {
            val verified = 1
            val notVerified = 0

            val success = 1

            val adultAge = 12 //age
            val maxchildAge = 11
            val minchildAge = 3
            val minchildAgeForBus = 5
            val maxinfantAge = 2
            val mininfantAge = 0

            val hotel_minchildAge = 1

            val traceId_expire = 5

            val yes=1
//            val no=2
            val yes_no=0

            val lowestPirce=1
            val highestPrice=0

            val minPrice=100
            val maxPrice=10000
        }
    }

    class FlightClass {
        companion object {
            val economy = 1
            val business = 2
            val firstClass = 3
            val premiumEconomy = 4
        }
    }

    class PassengerType {
        companion object {
            val adult = 1
            val child = 2
            val infant = 3
        }
    }

    class AisleSeatType {
        companion object {
            val Aisle = 2
            val AisleRecline = 10
            val AisleWing = 11
            val AisleExitRow = 12
            val AisleReclineWing = 13
            val AisleReclineExitRow = 14
            val AisleWingExitRow = 15
            val AisleReclineWingExitRow = 23
            val AisleBulkhead = 31
            val AisleQuiet = 32
            val AisleBulkheadQuiet = 33
            val CentreAisle = 34
            val CentreAisleBulkHead = 36
            val CentreAisleQuiet = 37
            val CentreAisleBulkHeadQuiet = 38
            val AisleBulkHeadWing = 46
            val AisleBulkHeadExitRow = 47
            val WindowAisle = 49
            val WindowAisleRecline = 54
            val WindowAisleWing = 55
            val WindowAisleExitRow = 56
            val WindowAisleReclineWing = 57
            val WindowAisleReclineExitRow = 58
            val WindowAisleWingExitRow = 59
            val WindowAisleBulkhead = 60
            val WindowAisleBulkheadWing = 61
        }


    }

    class SeatAvailbilityType {
        companion object {
            val open = 1
            val reserved = 3
            val notSet = 0
        }
    }

    class ExitRowSeats {
        companion object {
            val WindowExitRow = 6
            val WindowReclineExitRow = 8
            val WindowWingExitRow = 9
            val AisleExitRow = 12
            val AisleReclineExitRow = 14
            val AisleWingExitRow = 15
            val MiddleExitRow = 18
            val MiddleReclineExitRow = 20
            val MiddleWingExitRow = 21
            val WindowReclineWingExitRow = 22
            val AisleReclineWingExitRow = 23
            val MiddleReclineWingExitRow = 24
            val WindowBulkHeadExitRow = 43
            val MiddleBulkHeadExitRow = 45
            val AisleBulkHeadExitRow = 47
            val WindowAisleExitRow = 56
            val WindowAisleReclineExitRow = 58
            val WindowAisleWingExitRow = 59
//                val NoSeatRowExit = 6
        }
    }

}