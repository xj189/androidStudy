package example.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.style.base.BaseNoPagerLazyRefreshFragment
import com.style.framework.databinding.FragmentHome1Binding
import example.address.AddressActivity
import example.album.SelectLocalPictureActivity
import example.customView.*
import example.dialog.DialogActivity
import example.dialog.WheelActivity
import example.gesture.XXRefreshActivity
import example.viewPagerBanner.BannerActivity
import missing.namespace.databinding.FragmentHome1Binding

class CustomViewFragment : BaseNoPagerLazyRefreshFragment() {

    private lateinit var bd: FragmentHome1Binding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bd = FragmentHome1Binding.inflate(inflater, container, false)
        return bd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bd.btnAlbum.setOnClickListener { skip(SelectLocalPictureActivity::class.java) }
        bd.btnAddress.setOnClickListener { skip(AddressActivity::class.java) }
        bd.btnDialog.setOnClickListener { skip(DialogActivity::class.java) }
        bd.btnWheel.setOnClickListener { skip(WheelActivity::class.java) }
        bd.viewSuspend.setOnClickListener { skip(SuspendWindowActivity::class.java) }
        bd.btnRadioGroup.setOnClickListener { skip(BannerActivity::class.java) }
        bd.viewDraw.setOnClickListener { skip(DrawViewActivity::class.java) }
        bd.viewWriteWord.setOnClickListener { skip(WriteWordActivity::class.java) }
        bd.btnCustomView.setOnClickListener { skip(CustomViewMainActivity::class.java) }
        bd.btnHeart.setOnClickListener { skip(HeartLineActivity::class.java) }
        bd.btnTemp.setOnClickListener { skip(TempActivity::class.java) }
        bd.btnBp.setOnClickListener { skip(BpActivity::class.java) }
        bd.btnSleep.setOnClickListener { skip(SleepWeekActivity::class.java) }
        bd.btnSport.setOnClickListener { skip(SportWeekActivity::class.java) }
        bd.btnEcg.setOnClickListener { skip(EcgActivity::class.java) }
        bd.btnRecordAudioView.setOnClickListener { skip(RecordAudioViewActivity::class.java) }
        bd.btnExpandableText.setOnClickListener { skip(XXRefreshActivity::class.java) }
    }
}