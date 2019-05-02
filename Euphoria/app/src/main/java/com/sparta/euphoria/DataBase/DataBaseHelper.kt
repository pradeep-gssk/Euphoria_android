package com.sparta.euphoria.DataBase

import android.app.Application
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.sparta.euphoria.DataBase.Questionnaires.*
import com.sparta.euphoria.DataBase.Timer.*
import com.sparta.euphoria.Extensions.*
import org.json.JSONArray
import org.json.JSONObject

@Database(entities = [
    User:: class,
    Questionnaires:: class,
    Questionnaire:: class,
    Option:: class,
    Video:: class,
    Session:: class,
    Stop:: class,
    Sound:: class,
    Diet:: class,
    Exercises:: class,
    History:: class],
    version = 1)

abstract class DataBaseHelper: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun questionnairesDao(): QuestionnairesDao
    abstract fun questionnaireDao(): QuestionnaireDao
    abstract fun optionDao(): OptionDao
    abstract fun videoDao(): VideoDao
    abstract fun sessionDao(): SessionDao
    abstract fun stopDao(): StopDao
    abstract fun soundDao(): SoundDao
    abstract fun dietDao(): DietDao
    abstract fun exercisesDao(): ExercisesDao
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: DataBaseHelper? = null

        fun getDatabase(context: Context): DataBaseHelper {
            val tempInstance = INSTANCE
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DataBaseHelper::class.java,
                    "Euphoria"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

    fun preloadData(application: Application, customerId: Int) {
        addQuestionnaires("Questionnaire1.json".getJson(application), 1, customerId)
        addQuestionnaires("Questionnaire2.json".getJson(application), 2, customerId)
        addQuestionnaires("Questionnaire3.json".getJson(application), 3, customerId)
        addQuestionnaires("Questionnaire4.json".getJson(application), 4, customerId)
        addQuestionnaires("Questionnaire5.json".getJson(application), 5, customerId)
        addExercises("Exercises.json".getJsonArray(application))
        addSounds("Sounds.json".getJsonArray(application))
        addVideos("Videos.json".getJsonArray(application))
        addDiet("Diet.json".getJsonArray(application))
    }

    private fun addQuestionnaires(jsonObject: JSONObject, index: Int, customerId: Int) {
        val array = jsonObject.getJSONArray("questionnaire")
        val questionnaires = Questionnaires(customerId, index, 0, jsonObject.stringValue("title"), array.length())
        val questionnairesId = questionnairesDao().insert(questionnaires)
        addQuestionnaire(array, questionnairesId)
    }

    private fun addQuestionnaire(jsonArray: JSONArray, questionnairesId: Long) {
        for (i in 0..(jsonArray.length() - 1)){
            val questionnaireObject = jsonArray.getJSONObject(i)
            val questionnaire = Questionnaire(
                null,
                questionnaireObject.intValue("colorIndex"),
                null,
                questionnaireObject.stringValue("element"),
                questionnaireObject.intValue("optionType"),
                questionnaireObject.stringValue("question"),
                questionnaireObject.intValue("selectionType"),
                questionnaireObject.intValue("subOptionType"),
                questionnairesId
            )
            val questionnaireId = questionnaireDao().insert(questionnaire)
            val array = questionnaireObject.getJSONArray("options")
            addOption(array, questionnaireId)
        }
    }

    private fun addOption(jsonArray: JSONArray, questionnaireId: Long) {
        val options = ArrayList<Option>()
        for (i in 0..(jsonArray.length() - 1)) {
            val optionsObject = jsonArray.getJSONObject(i)
            val option = Option(
                optionsObject["option"].stringValue(),
                questionnaireId
            )
            options.add(option)
        }
        optionDao().insertAll(options)
    }

    fun checkIfAllAnswered(index: Int, customerId: Int): Boolean {
        val questionnairesObject = questionnairesDao().getQuestionnaire(index, customerId)
        println(questionnairesObject)
        println(questionnairesObject.uid)
        val questionnaireList = questionnaireDao().getAnsweredQuestionnaireList(questionnairesObject.uid)
        println(questionnaireList)
        println(questionnaireList.size)
        return if (questionnaireList.size > 0) false else true
    }

    private fun addExercises(jsonArray: JSONArray) {
    }

    private fun addSounds(jsonArray: JSONArray) {
    }

    private fun addVideos(jsonArray: JSONArray) {
    }

    private fun addDiet(jsonArray: JSONArray) {
    }

    //User
    fun saveUser(customerId: Int) {
        println("Save User")
        val user = User(customerId)
        userDao().insert(user)
    }

    fun checkIfUserExist(customerId: Int): Boolean {
        val user = userDao().getUser(customerId)
        println("came here")
        return  if (user == null) false else true
    }
}