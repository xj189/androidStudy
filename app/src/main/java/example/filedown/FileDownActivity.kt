package example.filedown

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.jeremyliao.liveeventbus.LiveEventBus
import com.style.base.BaseRecyclerViewAdapter
import com.style.base.BaseTitleBarActivity
import com.style.config.FileDirConfig
import com.style.data.event.EventBusEvent
import com.style.data.fileDown.CustomFileDownloadManager
import com.style.data.fileDown.FileDownloadStateBean
import com.style.data.fileDown.FileDownloadStateBean.DownStatus
import com.style.data.fileDown.entity.CustomFileBean
import com.style.data.fileDown.multiBlock.MultiThreadDownloadManager
import com.style.framework.databinding.FileDownListActivityBinding
import com.style.service.fileDownload.FileDownloadService
import com.style.utils.OpenFileUtil
import com.style.view.diviver.DividerItemDecoration
import java.io.File


class FileDownActivity : BaseTitleBarActivity() {

    private lateinit var bd: FileDownListActivityBinding
    private val targetPath = FileDirConfig.DIR_APP_FILE + "/apache-tomcat-8.0.24_multi_thread.exe"

    private lateinit var dataList: ArrayList<CustomFileBean>
    private lateinit var adapter: FileDownListAdapter
    private lateinit var mViewModel: FileDownListViewModel

    override fun onCreate(arg0: Bundle?) {
        super.onCreate(arg0)
        bd = FileDownListActivityBinding.inflate(layoutInflater)
        setContentView(bd.root)
        setTitleBarTitle("文件下载")
        dataList = ArrayList()
        adapter = FileDownListAdapter(getContext(), dataList)
        val layoutManager = LinearLayoutManager(getContext())
        bd.recyclerView.layoutManager = layoutManager
        bd.recyclerView.addItemDecoration(DividerItemDecoration(getContext()))
        //解决默认动画造成的itemView闪烁
        (bd.recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        bd.recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : BaseRecyclerViewAdapter.OnItemClickListener<CustomFileBean> {
            override fun onItemClick(position: Int, data: CustomFileBean) {
                logE("onItemClick", position.toString())
            }
        })
        adapter.setOnItemLongClickListener(object : BaseRecyclerViewAdapter.OnItemLongClickListener<CustomFileBean> {
            override fun onItemLongClick(itemView: View, position: Int, data: CustomFileBean) {
                logE("onItemLongClick", position.toString())
            }
        })
        adapter.setOnClickOptionListener(object : FileDownListAdapter.OnClickOptionListener<CustomFileBean> {
            override fun onClickOption(position: Int, data: CustomFileBean) {
                logE("onClickOption", position.toString())
                val state = data.fileStatus
                if (state == null) {
                    newDownloadFile(data)
                    return
                }
                when (state.status) {
                    DownStatus.NOT_DOWNLOAD -> newDownloadFile(data)
                    DownStatus.DOWNLOAD_PREPARE -> {
                    }
                    DownStatus.DOWNLOAD_PAUSE -> continueDownloadFile(data)
                    DownStatus.DOWNLOADING -> pauseDownloadFile(data)
                    DownStatus.DOWNLOAD_COMPLETED -> {
                        val file = File(FileDirConfig.DIR_APP_FILE, data.fileName)
                        //文件存在
                        if (file.parentFile.exists() && file.exists()) {
                            try {
                                OpenFileUtil.openFile(getContext(), FileDirConfig.FILE_PROVIDER_AUTHORITY, file)
                            } catch (e: Exception) {
                                showToast(e.message!!)
                            }
                        } else {
                            //未下载
                            showToast("文件不存在或已被删除")
                            state.status = DownStatus.NOT_DOWNLOAD
                            adapter.notifyItemChanged(position)
                        }
                    }
                }
            }
        })
        bd.viewBatchDownload.setOnClickListener { batchDownload() }
        LiveEventBus.get(EventBusEvent.FILE_DOWNLOAD_STATE_CHANGED, FileDownloadStateBean::class.java)
            .observe(this) {
                it ->
                onFileDownloadStateChanged(it)
            }
        mViewModel = ViewModelProvider(this).get(FileDownListViewModel::class.java)
        mViewModel.files.observe(this, Observer<ArrayList<CustomFileBean>> { t ->
            refreshData(t)
        })
        getData()
    }

    private fun batchDownload() {
        val i = Intent(CustomFileDownloadManager.FLAG_BATCH_DOWNLOAD)
        i.setClass(getContext(), FileDownloadService::class.java)
        i.putExtra("fileBeanList", dataList)
        startService(i)
    }

    //暂停下载
    fun pauseDownloadFile(f: CustomFileBean) {
        val i = Intent(CustomFileDownloadManager.FLAG_PAUSE_DOWNLOAD)
        i.setClass(getContext(), FileDownloadService::class.java)
        i.putExtra("fileBean", f)
        startService(i)
    }

    //新下载任务
    fun newDownloadFile(f: CustomFileBean) {
        val i = Intent(CustomFileDownloadManager.FLAG_NEW_DOWNLOAD)
        i.setClass(getContext(), FileDownloadService::class.java)
        i.putExtra("fileBean", f)
        startService(i)
    }

    //暂停后继续下载
    fun continueDownloadFile(f: CustomFileBean) {
        val i = Intent(CustomFileDownloadManager.FLAG_CONTINUE_DOWNLOAD)
        i.setClass(getContext(), FileDownloadService::class.java)
        i.putExtra("fileBean", f)
        startService(i)
    }

    private fun getData() {
        mViewModel.getData()
    }

    private fun refreshData(list: ArrayList<CustomFileBean>?) {
        if (list != null) {
            dataList.clear()
            /*list.forEachIndexed { index, b ->
                val file = File(FileDirConfig.DIR_APP_FILE, b.fileName)
                if (file.parentFile.exists() && file.exists()) {
                    if (b.fileStatus == null)
                        b.fileStatus = FileDownloadStateBean(b.url)
                    b.fileStatus?.status = DownStatus.DOWNLOAD_COMPLETED
                }
            }*/
            dataList.addAll(list)
            adapter.notifyDataSetChanged()
        }
    }

    private fun onFileDownloadStateChanged(f: FileDownloadStateBean) {
        for (i in dataList.indices) {
            if (dataList[i].url.equals(f.url)) {
                dataList[i].fileStatus = f
                adapter.notifyItemChanged(i)
                break
            }
        }
    }

    private fun multiThreadDownload() {
        /* MultiThreadDownloadManager.getInstance().down(TAG, url, targetPath, object : FileCallback() {
             override fun start(fileSize: Int) {
                 logE(TAG, "下载开始，文件大小==$fileSize")
             }

             override fun inProgress(currentDownSize: Int, fileSize: Int, progress: Float) {
                 bd.progressBar4.setProgress((100 * progress).toInt())
             }

             override fun complete(filePath: String) {
                 logE(TAG, "下载完成，文件路径==$filePath")
             }
         })*/
    }

    override fun onDestroy() {
        super.onDestroy()
        MultiThreadDownloadManager.getInstance().cancelCallback(TAG)
    }
}
