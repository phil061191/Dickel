package com.dickel.zeiterfassung.data.repository

import com.dickel.zeiterfassung.data.local.dao.CustomerDao
import com.dickel.zeiterfassung.data.local.entity.Customer
import com.dickel.zeiterfassung.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow

class CustomerRepository(private val dao: CustomerDao) {
    
    fun getAllCustomers(): Flow<List<Customer>> = dao.getAllCustomers()
    
    suspend fun getCustomerById(id: Long): Customer? = dao.getCustomerById(id)
    
    suspend fun insertCustomer(customer: Customer): Long = dao.insert(customer)
    
    suspend fun updateCustomer(customer: Customer) = dao.update(customer)
    
    suspend fun deleteCustomer(customer: Customer) = dao.delete(customer)
    
    suspend fun syncCustomersFromServer(): Result<Unit> {
        return try {
            val response = RetrofitClient.apiService.getCustomers()
            if (response.isSuccessful) {
                val customers = response.body()?.mapNotNull { map ->
                    try {
                        Customer(
                            id = (map["id"] as? Number)?.toLong() ?: 0,
                            name = map["name"] as? String ?: "",
                            address = map["address"] as? String,
                            email = map["email"] as? String,
                            phone = map["phone"] as? String
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                
                dao.deleteAll()
                dao.insertAll(customers)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to sync customers"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
