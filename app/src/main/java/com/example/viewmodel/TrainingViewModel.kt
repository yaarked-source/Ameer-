package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.CombatTechnique
import com.example.data.model.FightingCombo
import com.example.data.model.PresetCombos
import com.example.data.model.TrainingSong
import com.example.data.model.WorkoutSession
import com.example.ui.components.WorkoutPhase
import com.example.util.SoundBeepHelper
import com.example.util.YouTubeUrlHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TrainingViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val songDao = db.songDao()
    private val workoutDao = db.workoutDao()
    val soundHelper = SoundBeepHelper(application)

    // Songs state
    val allSongs: StateFlow<List<TrainingSong>> = songDao.getAllSongs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalWorkoutsCount = workoutDao.getTotalWorkoutsCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalCalories = workoutDao.getTotalCalories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalPunches = workoutDao.getTotalPunches()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val recentSessions = workoutDao.getAllSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Currently Playing / Selected Song
    private val _currentSong = MutableStateFlow<TrainingSong?>(null)
    val currentSong: StateFlow<TrainingSong?> = _currentSong.asStateFlow()

    private val _isPlayingMusic = MutableStateFlow(false)
    val isPlayingMusic: StateFlow<Boolean> = _isPlayingMusic.asStateFlow()

    private val _showPlayerDialog = MutableStateFlow(false)
    val showPlayerDialog: StateFlow<Boolean> = _showPlayerDialog.asStateFlow()

    // Interactive Fighter Animation State
    private val _currentTechnique = MutableStateFlow<CombatTechnique?>(null)
    val currentTechnique: StateFlow<CombatTechnique?> = _currentTechnique.asStateFlow()

    private val _strikeTriggerKey = MutableStateFlow(0L)
    val strikeTriggerKey: StateFlow<Long> = _strikeTriggerKey.asStateFlow()

    private val _isSouthpaw = MutableStateFlow(false)
    val isSouthpaw: StateFlow<Boolean> = _isSouthpaw.asStateFlow()

    private val _animSpeed = MutableStateFlow(1.0f)
    val animSpeed: StateFlow<Float> = _animSpeed.asStateFlow()

    private val _isAutoShadowboxing = MutableStateFlow(false)
    val isAutoShadowboxing: StateFlow<Boolean> = _isAutoShadowboxing.asStateFlow()

    private var autoShadowboxJob: Job? = null

    // Workout Round Timer & Combat Session State
    private val _currentRound = MutableStateFlow(1)
    val currentRound: StateFlow<Int> = _currentRound.asStateFlow()

    private val _totalRounds = MutableStateFlow(5)
    val totalRounds: StateFlow<Int> = _totalRounds.asStateFlow()

    private val _workDurationSeconds = MutableStateFlow(120) // 2 min rounds
    val workDurationSeconds: StateFlow<Int> = _workDurationSeconds.asStateFlow()

    private val _restDurationSeconds = MutableStateFlow(30) // 30 sec rest
    val restDurationSeconds: StateFlow<Int> = _restDurationSeconds.asStateFlow()

    private val _remainingSeconds = MutableStateFlow(120)
    val remainingSeconds: StateFlow<Int> = _remainingSeconds.asStateFlow()

    private val _workoutPhase = MutableStateFlow(WorkoutPhase.WORK)
    val workoutPhase: StateFlow<WorkoutPhase> = _workoutPhase.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _currentCombo = MutableStateFlow(PresetCombos.list[0])
    val currentCombo: StateFlow<FightingCombo> = _currentCombo.asStateFlow()

    private val _currentComboStep = MutableStateFlow(0)
    val currentComboStep: StateFlow<Int> = _currentComboStep.asStateFlow()

    private val _sessionPunches = MutableStateFlow(0)
    val sessionPunches: StateFlow<Int> = _sessionPunches.asStateFlow()

    private val _sessionDiscipline = MutableStateFlow("Boxing")
    val sessionDiscipline: StateFlow<String> = _sessionDiscipline.asStateFlow()

    private var timerJob: Job? = null
    private var comboCallJob: Job? = null

    init {
        // Ensure default song selected when songs load
        viewModelScope.launch {
            allSongs.collect { songs ->
                if (_currentSong.value == null && songs.isNotEmpty()) {
                    _currentSong.value = songs.first()
                }
            }
        }
    }

    // -------------------------------------------------------------
    // Music & YouTube Links Actions
    // -------------------------------------------------------------
    fun selectSong(song: TrainingSong, autoOpenPlayer: Boolean = true) {
        _currentSong.value = song
        _isPlayingMusic.value = true
        if (autoOpenPlayer) {
            _showPlayerDialog.value = true
        }
    }

    fun toggleMusicPlay() {
        if (_currentSong.value == null && allSongs.value.isNotEmpty()) {
            _currentSong.value = allSongs.value.first()
        }
        _isPlayingMusic.value = !_isPlayingMusic.value
        if (_isPlayingMusic.value) {
            _showPlayerDialog.value = true
        }
    }

    fun nextSong() {
        val list = allSongs.value
        if (list.isEmpty()) return
        val currentIndex = list.indexOfFirst { it.id == _currentSong.value?.id }
        val nextIndex = (currentIndex + 1) % list.size
        _currentSong.value = list[nextIndex]
        _isPlayingMusic.value = true
    }

    fun openPlayerDialog() {
        _showPlayerDialog.value = true
    }

    fun closePlayerDialog() {
        _showPlayerDialog.value = false
    }

    fun addPastedSong(url: String, titleInput: String, category: String, bpm: Int = 140, notes: String = ""): Boolean {
        val videoId = YouTubeUrlHelper.extractVideoId(url)
        if (videoId.isEmpty()) return false

        val songTitle = if (titleInput.isNotBlank()) titleInput.trim() else "Fight Beat (${videoId.take(6)})"

        viewModelScope.launch {
            val newSong = TrainingSong(
                title = songTitle,
                url = url.trim(),
                videoId = videoId,
                category = category,
                bpm = bpm,
                isFavorite = true,
                notes = notes
            )
            val newId = songDao.insertSong(newSong)
            _currentSong.value = newSong.copy(id = newId)
            _isPlayingMusic.value = true
            _showPlayerDialog.value = true
        }
        return true
    }

    fun toggleFavoriteSong(song: TrainingSong) {
        viewModelScope.launch {
            songDao.updateSong(song.copy(isFavorite = !song.isFavorite))
        }
    }

    fun deleteSong(song: TrainingSong) {
        viewModelScope.launch {
            songDao.deleteSong(song)
            if (_currentSong.value?.id == song.id) {
                _currentSong.value = allSongs.value.firstOrNull { it.id != song.id }
            }
        }
    }

    // -------------------------------------------------------------
    // Fighter Animation & Strike Triggers
    // -------------------------------------------------------------
    fun triggerStrike(technique: CombatTechnique) {
        _currentTechnique.value = technique
        _strikeTriggerKey.value += 1L
        _sessionPunches.value += 1
        soundHelper.playStrikeHitSound()
    }

    fun toggleSouthpaw() {
        _isSouthpaw.value = !_isSouthpaw.value
    }

    fun setAnimSpeed(speed: Float) {
        _animSpeed.value = speed
    }

    fun toggleAutoShadowbox() {
        if (_isAutoShadowboxing.value) {
            stopAutoShadowbox()
        } else {
            startAutoShadowbox()
        }
    }

    private fun startAutoShadowbox() {
        _isAutoShadowboxing.value = true
        autoShadowboxJob?.cancel()
        autoShadowboxJob = viewModelScope.launch {
            while (_isAutoShadowboxing.value) {
                val combo = PresetCombos.list.random()
                _currentCombo.value = combo
                for ((index, tech) in combo.sequence.withIndex()) {
                    if (!_isAutoShadowboxing.value) break
                    _currentComboStep.value = index
                    triggerStrike(tech)
                    delay((550 / _animSpeed.value).toLong())
                }
                // Short pause between combination flurries
                delay((1000 / _animSpeed.value).toLong())
            }
        }
    }

    private fun stopAutoShadowbox() {
        _isAutoShadowboxing.value = false
        autoShadowboxJob?.cancel()
        autoShadowboxJob = null
    }

    // -------------------------------------------------------------
    // Round Timer & Combat Workout Flow
    // -------------------------------------------------------------
    fun setWorkoutSettings(rounds: Int, workSeconds: Int, restSeconds: Int, discipline: String) {
        _totalRounds.value = rounds
        _workDurationSeconds.value = workSeconds
        _restDurationSeconds.value = restSeconds
        _sessionDiscipline.value = discipline
        resetWorkout()
    }

    fun toggleTimer() {
        if (_isTimerRunning.value) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        _isTimerRunning.value = true
        if (_workoutPhase.value == WorkoutPhase.WORK && _remainingSeconds.value == _workDurationSeconds.value) {
            soundHelper.playRoundStartBell()
        }

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_isTimerRunning.value) {
                delay(1000)
                if (!_isTimerRunning.value) break

                val newSeconds = _remainingSeconds.value - 1
                if (newSeconds == 10 && _workoutPhase.value == WorkoutPhase.WORK) {
                    soundHelper.playTenSecondWarning()
                }

                if (newSeconds > 0) {
                    _remainingSeconds.value = newSeconds
                } else {
                    handlePhaseTransition()
                }
            }
        }

        // Also run random combo calling during active work
        startComboCaller()
    }

    private fun handlePhaseTransition() {
        when (_workoutPhase.value) {
            WorkoutPhase.WORK -> {
                soundHelper.playRoundEndBell()
                if (_currentRound.value >= _totalRounds.value) {
                    // All rounds completed!
                    _workoutPhase.value = WorkoutPhase.COMPLETED
                    _isTimerRunning.value = false
                    _remainingSeconds.value = 0
                    saveWorkoutSession()
                } else {
                    _workoutPhase.value = WorkoutPhase.REST
                    _remainingSeconds.value = _restDurationSeconds.value
                }
            }
            WorkoutPhase.REST -> {
                _currentRound.value += 1
                _workoutPhase.value = WorkoutPhase.WORK
                _remainingSeconds.value = _workDurationSeconds.value
                soundHelper.playRoundStartBell()
            }
            WorkoutPhase.PREPARE -> {
                _workoutPhase.value = WorkoutPhase.WORK
                _remainingSeconds.value = _workDurationSeconds.value
                soundHelper.playRoundStartBell()
            }
            WorkoutPhase.COMPLETED -> {
                _isTimerRunning.value = false
            }
        }
    }

    private fun startComboCaller() {
        comboCallJob?.cancel()
        comboCallJob = viewModelScope.launch {
            while (_isTimerRunning.value && _workoutPhase.value == WorkoutPhase.WORK) {
                // Call a new combo every 4-6 seconds
                val nextCombo = PresetCombos.list.random()
                _currentCombo.value = nextCombo
                for ((idx, tech) in nextCombo.sequence.withIndex()) {
                    if (!_isTimerRunning.value || _workoutPhase.value != WorkoutPhase.WORK) break
                    _currentComboStep.value = idx
                    triggerStrike(tech)
                    delay((550 / _animSpeed.value).toLong())
                }
                delay(3000L)
            }
        }
    }

    fun pauseTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
        timerJob = null
        comboCallJob?.cancel()
        comboCallJob = null
    }

    fun skipPhase() {
        handlePhaseTransition()
    }

    fun resetWorkout() {
        pauseTimer()
        _currentRound.value = 1
        _workoutPhase.value = WorkoutPhase.WORK
        _remainingSeconds.value = _workDurationSeconds.value
        _sessionPunches.value = 0
    }

    private fun saveWorkoutSession() {
        viewModelScope.launch {
            val totalSeconds = (_totalRounds.value * _workDurationSeconds.value)
            // Approx 11 calories burned per minute in combat shadowboxing + punches
            val calories = ((totalSeconds / 60) * 11) + (_sessionPunches.value / 10)
            workoutDao.insertSession(
                WorkoutSession(
                    discipline = _sessionDiscipline.value,
                    totalRounds = _totalRounds.value,
                    completedRounds = _totalRounds.value,
                    durationSeconds = totalSeconds,
                    punchesThrown = _sessionPunches.value,
                    caloriesBurned = calories
                )
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundHelper.release()
        timerJob?.cancel()
        comboCallJob?.cancel()
        autoShadowboxJob?.cancel()
    }
}
