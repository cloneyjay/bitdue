package com.bitdue.financeapp

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bitdue.financeapp.data.local.FinanceDatabase
import com.bitdue.financeapp.data.local.dao.TransactionDao
import com.bitdue.financeapp.data.local.entity.TransactionEntity
import com.bitdue.financeapp.data.models.PaymentMethod
import com.bitdue.financeapp.data.models.TransactionType
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class FinanceDatabaseTest {
    private lateinit var transactionDao: TransactionDao
    private lateinit var db: FinanceDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, FinanceDatabase::class.java
        ).build()
        transactionDao = db.transactionDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() = runBlocking {
        val transaction = TransactionEntity(
            id = "1",
            amount = 100.0,
            type = TransactionType.EXPENSE,
            categoryId = "cat_food",
            title = "Lunch",
            date = System.currentTimeMillis(),
            paymentMethod = PaymentMethod.CASH
        )
        
        transactionDao.insertTransaction(transaction)
        
        // Use the synchronous list method I added
        val byItem = transactionDao.getAllTransactionsList()
        
        assertEquals(byItem[0].id, transaction.id)
        assertEquals(byItem[0].amount, transaction.amount, 0.0)
    }
}
