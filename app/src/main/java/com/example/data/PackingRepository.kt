package com.example.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class PackingRepository(
    private val packingDao: PackingDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    val allLists: Flow<List<PackingListEntity>> = packingDao.getAllLists()

    fun getListById(listId: Long): Flow<PackingListEntity?> {
        return packingDao.getListById(listId)
    }

    fun getItemsForList(listId: Long): Flow<List<PackingItemEntity>> {
        return packingDao.getItemsForList(listId)
    }

    fun getTotalCount(listId: Long): Flow<Int> = packingDao.getTotalItemCount(listId)
    fun getPackedCount(listId: Long): Flow<Int> = packingDao.getPackedItemCount(listId)

    suspend fun ensureInitialData(): Long = withContext(ioDispatcher) {
        val lists = packingDao.getAllLists().first()
        if (lists.isEmpty()) {
            val sampleList = SampleDataGenerator.getSamplePackingList()
            val listId = packingDao.insertList(sampleList)
            val items100 = SampleDataGenerator.generate100SampleItems(listId)
            packingDao.insertItems(items100)
            listId
        } else {
            lists.first().id
        }
    }

    suspend fun createListWith100Items(
        customerName: String,
        orderNumber: String,
        packingDate: String,
        dispatchDate: String,
        destination: String,
        notes: String,
        itemPrefix: String? = null
    ): Long = withContext(ioDispatcher) {
        val list = PackingListEntity(
            customerName = customerName,
            orderNumber = orderNumber,
            packingDate = packingDate,
            dispatchDate = dispatchDate,
            destination = destination,
            notes = notes,
            status = "In Packing",
            updatedAt = System.currentTimeMillis()
        )
        val listId = packingDao.insertList(list)
        val items = if (!itemPrefix.isNullOrBlank()) {
            SampleDataGenerator.generateCustom100Template(listId, customerName, itemPrefix)
        } else {
            SampleDataGenerator.generate100SampleItems(listId)
        }
        packingDao.insertItems(items)
        listId
    }

    suspend fun createEmptyList(
        customerName: String,
        orderNumber: String,
        packingDate: String,
        dispatchDate: String,
        destination: String,
        notes: String
    ): Long = withContext(ioDispatcher) {
        val list = PackingListEntity(
            customerName = customerName,
            orderNumber = orderNumber,
            packingDate = packingDate,
            dispatchDate = dispatchDate,
            destination = destination,
            notes = notes,
            status = "Draft",
            updatedAt = System.currentTimeMillis()
        )
        packingDao.insertList(list)
    }

    suspend fun updateList(list: PackingListEntity) = withContext(ioDispatcher) {
        packingDao.updateList(list.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteList(list: PackingListEntity) = withContext(ioDispatcher) {
        packingDao.deleteList(list)
    }

    suspend fun insertItem(item: PackingItemEntity): Long = withContext(ioDispatcher) {
        packingDao.insertItem(item)
    }

    suspend fun updateItem(item: PackingItemEntity) = withContext(ioDispatcher) {
        packingDao.updateItem(item)
    }

    suspend fun deleteItem(item: PackingItemEntity) = withContext(ioDispatcher) {
        packingDao.deleteItem(item)
    }

    suspend fun deleteItemById(itemId: Long) = withContext(ioDispatcher) {
        packingDao.deleteItemById(itemId)
    }

    suspend fun toggleItemPacked(item: PackingItemEntity) = withContext(ioDispatcher) {
        val newPacked = !item.isPacked
        val timestamp = if (newPacked) SampleDataGenerator.getCurrentTimestampFormatted() else ""
        packingDao.updatePackedStatus(item.id, newPacked, timestamp)
    }

    suspend fun markAllPacked(listId: Long, packed: Boolean) = withContext(ioDispatcher) {
        val timestamp = if (packed) SampleDataGenerator.getCurrentTimestampFormatted() else ""
        packingDao.markAllPacked(listId, packed, timestamp)
    }

    suspend fun markRangePacked(
        listId: Long,
        fromSno: Int,
        toSno: Int,
        packed: Boolean
    ) = withContext(ioDispatcher) {
        val timestamp = if (packed) SampleDataGenerator.getCurrentTimestampFormatted() else ""
        packingDao.markRangePacked(listId, fromSno, toSno, packed, timestamp)
    }

    suspend fun fill100Template(listId: Long, customerName: String, prefix: String = "Item") = withContext(ioDispatcher) {
        packingDao.deleteAllItemsForList(listId)
        val items = SampleDataGenerator.generateCustom100Template(listId, customerName, prefix)
        packingDao.insertItems(items)
    }

    suspend fun fill100IndustrialCatalog(listId: Long) = withContext(ioDispatcher) {
        packingDao.deleteAllItemsForList(listId)
        val items = SampleDataGenerator.generate100SampleItems(listId)
        packingDao.insertItems(items)
    }

    suspend fun getNextSno(listId: Long): Int = withContext(ioDispatcher) {
        val max = packingDao.getMaxSno(listId) ?: 0
        (max + 1).coerceAtMost(100)
    }
}
