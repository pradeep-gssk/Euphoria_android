package com.sparta.euphoria.DataBase

import android.app.Application
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.sparta.euphoria.DataBase.Questionnaires.*
import com.sparta.euphoria.DataBase.Timer.*
import com.sparta.euphoria.Extensions.*
import com.sparta.euphoria.Model.EUSession
import com.sparta.euphoria.Model.EUStop
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
        addDiet("Diet.json".getJsonArray(application))
        addExercises("Exercises.json".getJsonArray(application))
        addSounds("Sounds.json".getJsonArray(application))
        addVideos("Videos.json".getJsonArray(application))
    }

    //Questionnaires
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
        val questionnaireList = questionnaireDao().getAnsweredQuestionnaireList(questionnairesObject.uid)
        return if (questionnaireList.size > 0) false else true
    }

    fun getElementCount(element: String, questionnaireId: Long) : Int {
        val questionnaireList = questionnaireDao().getQuestionnaireForElement("Yes", element, questionnaireId)
        return if (questionnaireList.isEmpty()) 0 else questionnaireList.size
    }

    fun clearAllAnswers(customerId: Int) {
        //TODO: clear answers
    }

    //Diet
    private fun addDiet(jsonArray: JSONArray) {
        val diets = ArrayList<Diet>()
        for (i in 0..(jsonArray.length() - 1)) {
            val dietObject = jsonArray.getJSONObject(i)
            val diet = Diet(
                dietObject.stringValue("channels"),
                dietObject.stringValue("diet"),
                dietObject.stringValue("effect"),
                dietObject.stringValue("element"),
                dietObject.stringValue("flavour"),
                dietObject.stringValue("name"),
                dietObject.stringValue("nature"),
                dietObject.stringValue("organ")
            )
            diets.add(diet)
        }
        dietDao().insertAll(diets)
    }

    //Exercises
    private fun addExercises(jsonArray: JSONArray) {
        val exercises = ArrayList<Exercises>()
        for (i in 0..(jsonArray.length() - 1)) {
            val exerciseObject = jsonArray.getJSONObject(i)
            val exercise = Exercises(
                exerciseObject.stringValue("element"),
                exerciseObject.stringValue("exercise"),
                exerciseObject.stringValue("thumbnail"),
                exerciseObject.stringValue("video_description"),
                exerciseObject.stringValue("video_name"),
                exerciseObject.stringValue("video_path")
            )
            exercises.add(exercise)
        }
        exercisesDao().insertAll(exercises)
    }

    //Sounds
    private fun addSounds(jsonArray: JSONArray) {
        val sounds = ArrayList<Sound>()
        for (i in 0..(jsonArray.length() - 1)) {
            val soundObject = jsonArray.getJSONObject(i)
            val sound = Sound(
                soundObject.stringValue("name"),
                soundObject.stringValue("resource"),
                soundObject.stringValue("type")
            )
            sounds.add(sound)
        }
        soundDao().insertAll(sounds)
    }

    //Videos
    private fun addVideos(jsonArray: JSONArray) {
        val videosList = ArrayList<Video>()
        for (i in 0..(jsonArray.length() - 1)) {
            val videosObject = jsonArray.getJSONObject(i)
            val video = Video(
                videosObject.stringValue("thumbnail"),
                videosObject.stringValue("title"),
                videosObject.stringValue("video_description"),
                videosObject.stringValue("video_name"),
                videosObject.stringValue("video_path")
            )
            videosList.add(video)
        }
        videoDao().insertAll(videosList)
    }

    //User
    fun saveUser(customerId: Int) {
        val user = User(customerId)
        userDao().insert(user)
    }

    fun checkIfUserExist(customerId: Int): Boolean {
        val user = userDao().getUser(customerId)
        return  if (user == null) false else true
    }

    //Timer
    fun saveSession(session: EUSession, customerId: Int) {
        val entity = Session(customerId, session.name, session.timeInterval)
        val entityId = sessionDao().insert(entity)
        saveStop(session.stops, entityId)
    }

    fun saveStop(stops: ArrayList<EUStop>, entityId: Long) {

        val stopEntities = ArrayList<Stop>()

        for (stop in stops) {
            val sound = stop.sound
            val stopEntity = Stop(
                sound?.resource.stringValue(),
                sound?.name.stringValue(),
                stop.timeInterval,
                sound?.type.toString(),
                entityId)
            stopEntities.add(stopEntity)
        }

        stopDao().insertAll(stopEntities)
    }
}