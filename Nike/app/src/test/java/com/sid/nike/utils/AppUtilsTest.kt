package com.sid.nike.utils

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AppUtilsTest{
    @Test fun convertToGeneralDateFormat_nullInput_emptyOutput(){
        Assert.assertEquals("",AppUtils.convertToGeneralDateFormat(null))
        Assert.assertEquals("",AppUtils.convertToGeneralDateFormat(""))

    }

    @Test fun convertToGeneralDateFormat_invalidInput_emptyOutput(){
        Assert.assertEquals("",AppUtils.convertToGeneralDateFormat("102"))
        Assert.assertEquals("",AppUtils.convertToGeneralDateFormat("ABCD"))
        Assert.assertEquals("",AppUtils.convertToGeneralDateFormat("10-10-2001"))
        Assert.assertEquals("",AppUtils.convertToGeneralDateFormat("10-10-2001"))
    }

    @Test fun convertToGeneralDateFormat_validInput_validOutput(){
        Assert.assertEquals("15 Aug 2008",AppUtils.convertToGeneralDateFormat("2008-08-15T00:00:00.000Z"))
        Assert.assertEquals("24 Sep 2003",AppUtils.convertToGeneralDateFormat("2003-09-24T00:00:00.000Z"))
    }
}