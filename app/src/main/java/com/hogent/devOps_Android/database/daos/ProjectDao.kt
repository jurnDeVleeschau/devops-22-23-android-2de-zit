package com.hogent.devOps_Android.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hogent.devOps_Android.database.entities.ProjectEntitiy

@Dao
interface ProjectDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg projects: ProjectEntitiy)

    @Query("SELECT * FROM project_table WHERE id = :key")
    fun get(key: Long): LiveData<ProjectEntitiy>

    @Query("select * from project_table where userid == :key")
    fun getByCustomerId(key: String): LiveData<List<ProjectEntitiy>>?
}
