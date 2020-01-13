package com.example.todo.dataAccessObject

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.todo.modal.Events

@Dao
interface TodoDao {

    @Query("SELECT * FROM todo_events")
    fun getAllEvents(): List<Events>

    @Query("SELECT * FROM todo_events WHERE event_name LIKE :eventName")
    fun findByEventName(eventName: String): LiveData<List<Events>>

    @Insert
    fun insertAll(vararg todo: Events)


    @Query(" DELETE FROM todo_events WHERE event_time = :todo")
    fun delete(todo: String)

    @Update
    fun updateTodo(vararg event: Events)
}