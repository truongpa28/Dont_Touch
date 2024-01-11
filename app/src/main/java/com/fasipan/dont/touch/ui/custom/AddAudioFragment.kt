package com.fasipan.dont.touch.ui.custom

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.base.BaseFragment
import com.fasipan.dont.touch.databinding.FragmentAddAudioBinding
import com.fasipan.dont.touch.db.LocalDataSource
import com.fasipan.dont.touch.db.entity.AudioEntity
import com.fasipan.dont.touch.ui.dialog.DialogQuitEditing
import com.fasipan.dont.touch.ui.dialog.DialogQuitRecording
import com.fasipan.dont.touch.ui.dialog.DialogSaveRecord
import com.fasipan.dont.touch.utils.DataUtils
import com.fasipan.dont.touch.utils.ex.clickSafe
import com.fasipan.dont.touch.utils.ex.gone
import com.fasipan.dont.touch.utils.ex.hide
import com.fasipan.dont.touch.utils.ex.setOnTouchScale
import com.fasipan.dont.touch.utils.ex.show
import com.fasipan.dont.touch.utils.ex.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("SetTextI18n")
class AddAudioFragment : BaseFragment() {

    private lateinit var binding: FragmentAddAudioBinding

    companion object {
        const val STATUS_NON = 0
        const val STATUS_RECORDING = 1
        const val STATUS_PAUSE = 2
        const val STATUS_PLAY = 3
        const val STATUS_PLAY_PAUSE = 4
    }

    private val dialogQuitEditing by lazy {
        DialogQuitEditing(requireContext())
    }

    private val dialogSaveRecord by lazy {
        DialogSaveRecord(requireContext())
    }


    private val dialogQuitRecording by lazy {
        DialogQuitRecording(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddAudioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initListener()

        DataUtils.isInternet.observe(viewLifecycleOwner) {
            if (!it) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    myAudioRecorder?.stop()
                }
                if (status == STATUS_RECORDING) {
                    binding.txtTapToRecord.text = getString(R.string.tap_to_record)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        myAudioRecorder?.pause()
                    }
                    status = STATUS_PAUSE
                    binding.imgIcon.setImageResource(R.drawable.mic_auto_v1)
                    binding.imgPauseRecord.setImageResource(R.drawable.ic_resume_record)
                    funCount?.cancel()
                }
            }
        }
    }

    private fun initView() {
        binding.txtRetry.isSelected = true
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            binding.imgPauseRecord.hide()
        }
        statusNon()
    }

    override fun onBack() {

        if (mediaPlayer?.isPlaying == true) {
            status = STATUS_PLAY_PAUSE
            binding.imgPausePlay.setImageResource(R.drawable.ic_resume)
            binding.imgIcon.setImageResource(R.drawable.mic_auto_v1)
            mediaPlayer?.let {
                it.pause()
            }
        }

        if (status == STATUS_PLAY || status == STATUS_PLAY_PAUSE) {
            dialogQuitEditing.show(actionQuit = {
                findNavController().popBackStack()
            }, actionSave = {
                dialogSaveRecord.show {
                    if (it.trim().isEmpty()) {
                        requireContext().showToast(getString(R.string.name_must_not_null))
                    } else {
                        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                            LocalDataSource.addAudio(
                                AudioEntity(
                                    0,
                                    outputFile,
                                    R.drawable.avt_audio,
                                    R.string.app_name,
                                    it,
                                    false
                                )
                            )
                            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                                requireContext().showToast(getString(R.string.add_successfully))
                                dialogSaveRecord.hide()
                                findNavController().popBackStack()
                            }
                        }
                    }
                }
            })
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (status == STATUS_RECORDING || status == STATUS_PAUSE) {
                    if (status == STATUS_RECORDING) {
                        //pauseRecord
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            myAudioRecorder?.pause()
                        }
                        status = STATUS_PAUSE
                        binding.imgIcon.setImageResource(R.drawable.mic_auto_v1)
                        binding.imgPauseRecord.setImageResource(R.drawable.ic_resume_record)
                        funCount?.cancel()
                    }
                    dialogQuitRecording.show {
                        findNavController().popBackStack()
                    }
                } else {
                    findNavController().popBackStack()
                }
            } else {
                findNavController().popBackStack()
            }
        }

    }

    private fun initListener() {

        binding.imgBack.clickSafe {
            onBack()
        }

        binding.imgCloseRecord.setOnTouchScale({
            statusNon()
            funCount?.cancel()
            myAudioRecorder?.stop()
        }, 0.9f)

        binding.imgDoneRecord.setOnTouchScale({
            finishRecord()
        }, 0.9f)

        binding.imgPausePlay.setOnTouchScale({
            if (mediaPlayer?.isPlaying == false) {
                status = STATUS_PLAY
                binding.imgPausePlay.setImageResource(R.drawable.ic_pause)
                binding.imgIcon.setImageResource(R.drawable.mic_auto_v2)
                mediaPlayer?.let {
                    it.setOnCompletionListener {
                        /*it.start()
                        status = STATUS_PLAY
                        binding.imgPausePlay.setImageResource(R.drawable.ic_pause)
                        binding.imgIcon.setImageResource(R.drawable.mic_auto_v2)*/
                    }
                    it.start()
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                        while (it.isPlaying) {
                            withContext(Dispatchers.IO) {
                                delay(200)
                            }
                            val currentPosition = it.currentPosition
                            binding.sbProgress.progress = currentPosition * 100 / maxProgress
                            if (!it.isPlaying) {
                                if (status != STATUS_PLAY_PAUSE) {
                                    binding.imgPausePlay.setImageResource(R.drawable.ic_resume)
                                    binding.imgIcon.setImageResource(R.drawable.mic_auto_v1)
                                    binding.sbProgress.progress = 0
                                }
                                status = STATUS_PLAY_PAUSE
                            }
                        }
                    }
                }
            } else {
                status = STATUS_PLAY_PAUSE
                binding.imgPausePlay.setImageResource(R.drawable.ic_resume)
                binding.imgIcon.setImageResource(R.drawable.mic_auto_v1)
                mediaPlayer?.let {
                    it.pause()
                }
            }
        }, 0.9f)

        binding.txtSave.setOnTouchScale({
            if (mediaPlayer?.isPlaying == true) {
                status = STATUS_PLAY_PAUSE
                binding.imgPausePlay.setImageResource(R.drawable.ic_resume)
                binding.imgIcon.setImageResource(R.drawable.mic_auto_v1)
                mediaPlayer?.let {
                    it.pause()
                }
            }
            dialogSaveRecord.show {
                if (it.trim().isEmpty()) {
                    requireContext().showToast(getString(R.string.name_must_not_null))
                } else {
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                        LocalDataSource.addAudio(
                            AudioEntity(
                                0,
                                outputFile,
                                R.drawable.avt_audio,
                                R.string.app_name,
                                it,
                                false
                            )
                        )
                        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                            requireContext().showToast("Add successfully")
                            dialogSaveRecord.hide()
                            findNavController().popBackStack()
                        }

                    }

                }
            }
        }, 0.9f)

        binding.txtRetry.setOnTouchScale({
            statusNon()
            try {
                mediaPlayer?.stop()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, 0.9f)


        binding.txtTapToRecord.setOnTouchScale({
            startRecord()
        }, 0.9f)

        binding.imgPauseRecord.setOnTouchScale({
            if (status == STATUS_RECORDING) {
                binding.txtTapToRecord.text = getString(R.string.tap_to_record)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    myAudioRecorder?.pause()
                }
                status = STATUS_PAUSE
                binding.imgIcon.setImageResource(R.drawable.mic_auto_v1)
                binding.imgPauseRecord.setImageResource(R.drawable.ic_resume_record)
                funCount?.cancel()
            } else if (status == STATUS_PAUSE) {
                binding.txtTapToRecord.text = getString(R.string.recording)
                status = STATUS_RECORDING
                binding.imgIcon.setImageResource(R.drawable.mic_auto_v2)
                binding.imgPauseRecord.setImageResource(R.drawable.ic_pause_record)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    myAudioRecorder?.resume()
                }
                countTimeRecord()
            }
        }, 0.9f)

    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer?.isPlaying == true) {
            status = STATUS_PLAY_PAUSE
            binding.imgPausePlay.setImageResource(R.drawable.ic_resume)
            binding.imgIcon.setImageResource(R.drawable.mic_auto_v1)
            mediaPlayer?.let {
                it.pause()
            }
        }
    }

    private var funCount: CountDownTimer? = null
    private var timeRecord = 0

    private var status = STATUS_NON

    private var outputFile = ""

    var myAudioRecorder: MediaRecorder? = null

    private fun statusNon() {
        status = STATUS_NON
        timeRecord = 0
        binding.imgIcon.setImageResource(R.drawable.mic_auto_v1)
        binding.txtTapToRecord.show()
        binding.txtTapToRecord.setBackgroundResource(R.drawable.bg_btn_tap_to_active)
        binding.txtTapToRecord.text = getString(R.string.tap_to_record)
        binding.txtTapToRecord.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.txtTapToRecord.isEnabled = true
        binding.txtTimeRecord.text = "00:00"
        binding.txtTimeRecord.show()
        binding.txtTimeRecord.setTextColor(ContextCompat.getColor(requireContext(), R.color.xam_1))
        binding.llBtnRecording.gone()
        binding.llBtnPlayRecord.gone()
        binding.llSave.gone()
    }

    private fun startRecord() {
        status = STATUS_RECORDING
        binding.imgIcon.setImageResource(R.drawable.mic_auto_v2)
        binding.txtTapToRecord.text = getString(R.string.recording)
        binding.txtTapToRecord.setBackgroundResource(0)
        binding.txtTapToRecord.setTextColor(ContextCompat.getColor(requireContext(), R.color.xam_1))
        binding.txtTapToRecord.isEnabled = false
        binding.txtTimeRecord.text = "00:00"
        binding.txtTimeRecord.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.black_1
            )
        )
        binding.llBtnRecording.show()
        binding.imgPauseRecord.setImageResource(R.drawable.ic_pause_record)
        binding.llBtnPlayRecord.gone()
        binding.llSave.gone()
        outputFile = DataUtils.getNewPathFile(requireContext())
        try {
            myAudioRecorder = MediaRecorder()
            myAudioRecorder?.let {
                timeRecord = 0
                it.setAudioSource(MediaRecorder.AudioSource.MIC)
                it.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                it.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                it.setOutputFile(outputFile)
                it.prepare()
                it.start()
                countTimeRecord()
            } ?: {
                statusNon()
                requireContext().showToast(getString(R.string.error))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            statusNon()
            requireContext().showToast(getString(R.string.error))
        }
    }

    private fun finishRecord() {
        status = STATUS_PLAY_PAUSE
        funCount?.cancel()
        myAudioRecorder?.stop()
        binding.imgPausePlay.setImageResource(R.drawable.ic_resume)
        binding.imgIcon.setImageResource(R.drawable.mic_auto_v1)
        binding.txtTapToRecord.gone()
        binding.txtTimeRecord.gone()
        binding.llBtnRecording.gone()
        binding.llBtnPlayRecord.show()
        binding.llSave.show()

        setupPlayAudio()
    }

    private fun countTimeRecord() {
        funCount = object : CountDownTimer(20000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                lifecycleScope.launch(Dispatchers.Main) {
                    timeRecord++
                    if (timeRecord <= 15) {
                        binding.txtTimeRecord.text = DataUtils.getTimeShowFormCount(timeRecord)
                    }
                    if (timeRecord == 15) {
                        finishRecord()
                    }
                }
            }

            override fun onFinish() {}
        }
        funCount?.start()
    }


    //============================================Play==============================================

    var mediaPlayer: MediaPlayer? = null
    var maxProgress = 100
    var statusOnTrack = 0
    private fun setupPlayAudio() {
        mediaPlayer = MediaPlayer()

        binding.txtTimePlay.text = "00:00"

        mediaPlayer?.let { med ->
            med.setDataSource(requireContext(), Uri.parse(outputFile))
            med.prepare()
            maxProgress = med.duration
            binding.txtTimePlay.text = DataUtils.getTimeShowFormMillisecond(maxProgress)
        }

        binding.sbProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress * maxProgress / 100)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                statusOnTrack = status
                binding.imgPausePlay.setImageResource(R.drawable.ic_resume)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (statusOnTrack == STATUS_PLAY) {
                    status = STATUS_PLAY
                    binding.imgPausePlay.setImageResource(R.drawable.ic_pause)
                    binding.imgIcon.setImageResource(R.drawable.mic_auto_v2)
                    mediaPlayer?.let {
                        it.start()
                        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                            while (it.isPlaying) {
                                withContext(Dispatchers.IO) {
                                    delay(200)
                                }
                                val currentPosition = it.currentPosition
                                binding.sbProgress.progress = currentPosition * 100 / maxProgress
                                if (!it.isPlaying) {
                                    if (status != STATUS_PLAY_PAUSE) {
                                        binding.imgPausePlay.setImageResource(R.drawable.ic_resume)
                                        binding.imgIcon.setImageResource(R.drawable.mic_auto_v1)
                                        binding.sbProgress.progress = 0
                                    }
                                    status = STATUS_PLAY_PAUSE
                                }
                            }
                        }
                    }
                }
            }
        })


        status = STATUS_PLAY
        binding.imgPausePlay.setImageResource(R.drawable.ic_pause)
        binding.imgIcon.setImageResource(R.drawable.mic_auto_v2)
        mediaPlayer?.let {
            it.setOnCompletionListener {
                /*it.start()
                status = STATUS_PLAY
                binding.imgPausePlay.setImageResource(R.drawable.ic_pause)
                binding.imgIcon.setImageResource(R.drawable.mic_auto_v2)*/
            }
            it.start()
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                while (it.isPlaying) {
                    withContext(Dispatchers.IO) {
                        delay(200)
                    }
                    val currentPosition = it.currentPosition
                    binding.sbProgress.progress = currentPosition * 100 / maxProgress
                    if (!it.isPlaying) {
                        if (status != STATUS_PLAY_PAUSE) {
                            binding.imgPausePlay.setImageResource(R.drawable.ic_resume)
                            binding.imgIcon.setImageResource(R.drawable.mic_auto_v1)
                            binding.sbProgress.progress = 0
                        }
                        status = STATUS_PLAY_PAUSE
                    }
                }
            }
        }
    }

}