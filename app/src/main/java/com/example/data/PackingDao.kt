package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PackingDao {
    @Query("SELECT * FROM packing_lists ORDER BY updatedAt DESC")
    fun getAllLists(): Flow<List<PackingListEntity>>

    @Query("SELECT * FROM packing_lists WHERE id = :id LIMIT 1")
    fun getListById(id: Long): Flow<PackingListEntity?>

    @Query("SELECT * FROM packing_items WHERE listId = :listId ORDER BY sno ASC")
    fun getItemsForList(listId: Long): Flow<List<PackingItemEntity>>

    @Query("SELECT MAX(sno) FROM packing_items WHERE listId = :listId")
    suspend fun getMaxSno(listId: Long): Int?

    @Query("SELECT COUNT(*) FROM packing_items WHERE listId = :listId")
    fun getTotalItemCount(listId: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM packing_items WHERE listId = :listId AND isPacked = 1")
    fun getPackedItemCount(listId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(list: PackingListEntity): Long

    @Update
    suspend fun updateList(list: PackingListEntity)

    @Delete
    suspend fun deleteList(list: PackingListEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: PackingItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<PackingItemEntity>)

    @Update
    suspend fun updateItem(item: PackingItemEntity)

    @Delete
    suspend fun deleteItem(item: PackingItemEntity)

    @Query("DELETE FROM packing_items WHERE id = :itemId")
    suspend fun deleteItemById(itemId: Long)

    @Query("DELETE FROM packing_items WHERE listId = :listId")
    suspend fun deleteAllItemsForList(listId: Long)

    @Query("UPDATE packing_items SET isPacked = :isPacked, packedDate = :packedDate WHERE id = :itemId")
    suspend fun updatePackedStatus(itemId: Long, isPacked: Boolean, packedDate: String)

    @Query("UPDATE packing_items SET isPacked = :isPacked, packedDate = :packedDate WHERE listId = :listId")
    suspend fun markAllPacked(listId: Long, isPacked: Boolean, packedDate: String)

    @Query("UPDATE packing_items SET isPacked = :isPacked, packedDate = :packedDate WHERE listId = :listId AND sno >= :fromSno AND sno <= :toSno")
    suspend fun markRangePacked(listId: Long, fromSno: Int, toSno: Int, isPacked: Boolean, packedDate: String)
}
