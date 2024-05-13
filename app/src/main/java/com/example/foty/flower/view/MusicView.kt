package com.example.foty.flower.view

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.foty.flower.databinding.LayoutMusicViewBinding

class MusicView : ConstraintLayout, MusicViewManager.MusicManager {

    private val musicViewManager by lazy { MusicViewManager(context) }
    private var binding: LayoutMusicViewBinding? = null

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    private fun initView() {
        binding = LayoutMusicViewBinding.inflate(LayoutInflater.from(context), this, true)
        musicViewManager.setMusicListener(this)
    }

    override fun contentChange(title: String?, lyrics: String?, cover: Bitmap?, str3: String?) {
        binding?.let {
            it.name.text = "作者：$title"
            it.song.text = "歌詞：$lyrics"
            it.img.setImageBitmap(cover)
        }
    }

    fun destroy(){
        musicViewManager.destroy()
    }

    override fun stateChange(i: Int) {
        Log.d("lxx", "stateChange  i= $i")
    }

}