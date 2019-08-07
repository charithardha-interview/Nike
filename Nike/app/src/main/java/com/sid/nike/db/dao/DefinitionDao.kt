package com.sid.nike.db.dao

import androidx.room.*
import com.sid.nike.db.dto.Definition

@Dao
interface DefinitionDao{
    @Query("SELECT * FROM definition")
    fun getAllDefinitions(): MutableList<Definition>

    @Query("SELECT * FROM definition WHERE word = :term")
    fun getAllTermDefinitions(term:String): MutableList<Definition>

    @Insert
    fun addDefinition(definition: Definition)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllDefinitions(vararg definitions: Definition)

    @Query("DELETE FROM definition WHERE word = :term")
    fun deleteDefinitionsByTerm(term:String)
}