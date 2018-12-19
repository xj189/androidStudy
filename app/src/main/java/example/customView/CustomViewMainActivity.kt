package example.customView

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment

import com.style.base.BaseDefaultTitleBarActivity
import com.style.framework.R
import com.style.framework.databinding.CustomViewMainBinding

import example.customView.fragment.*
import example.viewPagerTabLayout.FindTabAdapter
import kotlinx.android.synthetic.main.custom_view_main.*
import java.util.ArrayList

class CustomViewMainActivity : BaseDefaultTitleBarActivity() {
    private lateinit var fAdapter: CustomViewFragmentAdapter
    private val fragments = ArrayList<Fragment>()
    private val titles = ArrayList<String>()

    override fun onCreate(arg0: Bundle?) {
        super.onCreate(arg0)
        setContentView(R.layout.custom_view_main)
    }

    override fun initData() {
        setToolbarTitle("tabLayout")
        titles.add("自定义饼状图")
        fragments.add(PieChartFragment())
        titles.add("自定义通知小圆点")
        fragments.add(CustomNotifyViewFragment())
        titles.add("圆环进度条")
        fragments.add(CircleProgressBarFragment())
        titles.add("波浪球")
        fragments.add(WaterPoloFragment())
        titles.add("声波")
        fragments.add(SoundWaveFragment())
        titles.add("水平进度")
        fragments.add(HorizontalProgressFragment())
        titles.add("扫描")
        fragments.add(ScanViewFragment())
        titles.add("键盘")
        fragments.add(KeyboardFragment())
        fAdapter = CustomViewFragmentAdapter(this.supportFragmentManager, fragments, titles)
        viewPager.adapter = fAdapter
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }

}
