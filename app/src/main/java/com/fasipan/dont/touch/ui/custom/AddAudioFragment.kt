package com.fasipan.dont.touch.ui.custom

import android.annotation.SuppressLint
import android.media.MediaRecorder
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.base.BaseFragment
import com.fasipan.dont.touch.databinding.FragmentAddAudioBinding
import com.fasipan.dont.touch.db.LocalDataSource
import com.fasipan.dont.touch.db.entity.AudioEntity
import com.fasipan.dont.touch.ui.dialog.DialogQuitEditing
import com.fasipan.dont.touch.ui.dialog.DialogSaveRecord
import com.fasipan.dont.touch.utils.DataUtils
import com.fasipan.dont.touch.utils.MediaPlayerUtils
import com.fasipan.dont.touch.utils.ex.clickSafe
import com.fasipan.dont.touch.utils.ex.gone
import com.fasipan.dont.touch.utils.ex.setOnTouchScale
import com.fasipan.dont.touch.utils.ex.show
import com.fasipan.dont.touch.utils.ex.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    }

    private fun initView() {
        binding.imgBack.clickSafe {
            onBack()
        }

        statusNon()
    }

    private fun initListener() {

        binding.imgCloseRecord.setOnTouchScale({
            statusNon()
            funCount?.cancel()
            myAudioRecorder?.stop()
        }, 0.9f)

        binding.imgDoneRecord.setOnTouchScale({
            finishRecord()
        }, 0.9f)

        binding.imgPausePlay.setOnTouchScale({
            MediaPlayerUtils.playAudioSoundRecord(requireContext(), outputFile) {

            }
        }, 0.9f)

        binding.txtSave.setOnTouchScale({
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
            MediaPlayerUtils.stopMediaPlayer()
        }, 0.9f)


        binding.txtTapToRecord.setOnTouchScale({
            startRecord()
        }, 0.9f)

        binding.imgPauseRecord.setOnTouchScale({
            if (status == STATUS_RECORDING) {
                pauseRecord()
            } else if (status == STATUS_PAUSE) {
                resumeRecord()
            }
        }, 0.9f)

    }

    override fun onPause() {
        super.onPause()
        MediaPlayerUtils.stopMediaPlayer()
    }

    private var funCount: CountDownTimer? = null
    private var timeRecord = 0

    private var status = STATUS_NON

    private var outputFile = ""

    var myAudioRecorder: MediaRecorder? = null

    private fun statusNon() {
        timeRecord = 0

        binding.imgIcon.setImageResource(R.drawable.mic_auto_v1)

        binding.txtTapToRecord.setBackgroundResource(R.drawable.bg_btn_tap_to_active)
        binding.txtTapToRecord.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.txtTapToRecord.isEnabled = true
        binding.txtTimeRecord.text = "00:00"
        binding.txtTimeRecord.setTextColor(ContextCompat.getColor(requireContext(), R.color.xam_1))
        binding.llBtnRecording.gone()

        binding.llBtnPlayRecord.gone()
        binding.llSave.gone()
    }

    private fun startRecord() {
        status = STATUS_RECORDING

        binding.imgIcon.setImageResource(R.drawable.mic_auto_v2)

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
                countTime()
            } ?: {

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun pauseRecord() {
        status = STATUS_PAUSE

        binding.imgIcon.setImageResource(R.drawable.mic_auto_v1)
        binding.imgPauseRecord.setImageResource(R.drawable.ic_resume_record)
    }

    private fun resumeRecord() {
        status = STATUS_RECORDING

        binding.imgIcon.setImageResource(R.drawable.mic_auto_v2)
        binding.imgPauseRecord.setImageResource(R.drawable.ic_pause_record)
    }

    private fun finishRecord() {
        status = STATUS_PLAY

        funCount?.cancel()

        myAudioRecorder?.stop()

        binding.imgIcon.setImageResource(R.drawable.mic_auto_v2)

        binding.txtTapToRecord.gone()
        binding.txtTimeRecord.gone()
        binding.llBtnRecording.gone()

        binding.llBtnPlayRecord.show()
        binding.llSave.show()
    }

    private fun countTime() {
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

            override fun onFinish() {

            }
        }
        funCount?.start()
    }
}