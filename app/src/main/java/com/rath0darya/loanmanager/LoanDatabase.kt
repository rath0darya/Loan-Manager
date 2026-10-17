package com.rath0darya.loanmanager

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "loans")
data class LoanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val personName: String,
    val direction: String,
    val principal: Double,
    val monthlyRate: Double,
    val startDate: Long,
    val dueDate: Long?,
    val note: String = "",
    @ColumnInfo(defaultValue = "''") val personName2: String = "",
    @ColumnInfo(defaultValue = "''") val personName3: String = ""
)

@Entity(tableName = "payments", indices = [Index("loanId")])
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val loanId: Long,
    val amount: Double,
    val paidAt: Long,
    val note: String = "",
    @ColumnInfo(defaultValue = "'Cash'") val paymentMethod: String = "Cash",
    @ColumnInfo(defaultValue = "''") val referenceNumber: String = ""
)

@Dao
interface LoanDao {
    @Query("SELECT * FROM loans ORDER BY startDate DESC, id DESC")
    fun observeLoans(): Flow<List<LoanEntity>>

    @Query("SELECT * FROM loans ORDER BY startDate DESC, id DESC")
    suspend fun allLoans(): List<LoanEntity>

    @Query("SELECT * FROM payments ORDER BY paidAt DESC, id DESC")
    fun observePayments(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments ORDER BY paidAt ASC, id ASC")
    suspend fun allPayments(): List<PaymentEntity>

    @Insert
    suspend fun addLoan(loan: LoanEntity): Long

    @Insert
    suspend fun addPayment(payment: PaymentEntity)

    @Update
    suspend fun updatePayment(payment: PaymentEntity)

    @Query("DELETE FROM payments WHERE id = :paymentId")
    suspend fun deletePayment(paymentId: Long)

    @Update
    suspend fun updateLoan(loan: LoanEntity)

    @Query("DELETE FROM payments")
    suspend fun clearPayments()

    @Query("DELETE FROM loans")
    suspend fun clearLoans()

    @Query("DELETE FROM payments WHERE loanId = :loanId")
    suspend fun deletePaymentsForLoan(loanId: Long)

    @Query("DELETE FROM loans WHERE id = :loanId")
    suspend fun deleteLoan(loanId: Long)
}

@Database(entities = [LoanEntity::class, PaymentEntity::class], version = 3, exportSchema = false)
abstract class LoanDatabase : RoomDatabase() {
    abstract fun loanDao(): LoanDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE loans ADD COLUMN personName2 TEXT NOT NULL DEFAULT ''")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE loans ADD COLUMN personName3 TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE payments ADD COLUMN paymentMethod TEXT NOT NULL DEFAULT 'Cash'")
                database.execSQL("ALTER TABLE payments ADD COLUMN referenceNumber TEXT NOT NULL DEFAULT ''")
            }
        }

        @Volatile private var INSTANCE: LoanDatabase? = null

        fun get(context: Context): LoanDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    LoanDatabase::class.java,
                    "loan_manager.db"
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).build().also { INSTANCE = it }
            }
    }
}
