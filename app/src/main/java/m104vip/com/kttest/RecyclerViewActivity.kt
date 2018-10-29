package m104vip.com.kttest

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_recyclerview.*
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import org.jsoup.nodes.Document
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import android.R.attr.data
import android.content.Intent
import android.widget.Toast
import android.widget.AdapterView.OnItemClickListener




class RecyclerViewActivity: AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    lateinit var dialog: ProgressDialog;

    var mainAdapter: MainAdapter? = null
    var mList: ArrayList<News>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)
        //下拉刷新颜色
        mSwipeRefresh.setColorSchemeColors(Color.rgb(47, 223, 189))
        mSwipeRefresh.setOnRefreshListener(this)
        //设置布局管理器  layout可以设置方向
        mRvCommonList.layoutManager = LinearLayoutManager(this)
        mList = ArrayList<News>();
        mRvCommonList.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            var lastVisibleItem: Int? = 0
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem!! +1 == mRvCommonList.adapter?.itemCount) {
                    addData()
                }
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView?.layoutManager as LinearLayoutManager
                //最后一个可见的ITEM
                lastVisibleItem = layoutManager.findLastVisibleItemPosition()
            }
        })

        dialog = ProgressDialog(this)
        dialog.setMessage("載入中")
        dialog.show()
        refreshList(this);



    }

    //重新整理
    override fun onRefresh() {
        mList!!.removeAll(mList!!);
        mRvCommonList.adapter.notifyDataSetChanged()
        refreshList(this);
    }

    //載入數據
    private fun addData() {
        loadList();
    }


    class MainAdapter(context: Context?, list: ArrayList<News>?) : RecyclerView.Adapter<MainAdapter.CommonHolder>() {
        var mContext: Context? = null
        var mList2: ArrayList<News>? = null
        var mInflater: LayoutInflater? = null

        init {
            mContext = context
            mList2 = list
            mInflater = LayoutInflater.from(context)
        }

        override fun getItemCount(): Int {
            return this.mList2!!.size
        }

        override fun onBindViewHolder(holder: CommonHolder,position: Int) {
            holder?.proLoad.visibility = View.GONE
            holder?.tvTitle?.text = mList2?.get(position)!!.title
            holder?.tvMsg.text = mList2?.get(position)!!.desc

            if (mList2?.get(position)!!.image.isNotEmpty()) {
                Glide.with(mContext).load(mList2?.get(position)!!.image).into(holder?.ivPic)
            }

            if(position == this.mList2!!.size-1){
                holder?.proLoad.visibility = View.VISIBLE
            }

            holder?.itemView.setOnClickListener {
                mContext?.startActivity(Intent(mContext, WebViewActivity::class.java).putExtra("url", mList2?.get(position)!!.url))
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): CommonHolder {
            return CommonHolder(mInflater?.inflate(R.layout.recyclerview_item,parent,false))

        }

        class CommonHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
            var tvTitle: TextView = itemView?.findViewById<TextView>(R.id.tvTitle) as TextView
            var tvMsg: TextView = itemView?.findViewById<TextView>(R.id.tvMsg) as TextView
            var ivPic: ImageView = itemView?.findViewById<ImageView>(R.id.ivPic) as ImageView
            var proLoad: ProgressBar = itemView?.findViewById<ProgressBar>(R.id.proLoad) as ProgressBar
        }
    }


    //上滑載入更多
    private fun loadList() = doAsync {
        val mList1: ArrayList<News>? = getLinks("http://www.cnbeta.com/")
        if (mList1!=null) {
            mList!!.addAll(mList1)
        }

        uiThread {
            mRvCommonList.adapter.notifyDataSetChanged();
        }

    }

    //重新整理與第1次載入
    fun refreshList(context: Context?) = doAsync {
         mList = getLinks("http://www.cnbeta.com/");

        uiThread {
            dialog.dismiss()
            mainAdapter = MainAdapter(context, mList)
            mRvCommonList.adapter = mainAdapter
            mSwipeRefresh.isRefreshing = false;
        }
    }

    //爬網站資料
    fun getLinks(url :String): ArrayList<News>{
        val links = ArrayList<News>()
        val doc: Document = Jsoup.connect(url).get()
        val elements: Elements = doc.select("div.items-area").first().allElements.first().children()

        for (e in elements) {
            if (e.getElementsByTag("dt").text().isEmpty())
                continue
            links.add(News(e.getElementsByTag("dt").text(),
                    e.getElementsByTag("dd").text(),
                    e.select("a[href]").first().attr("href"),
                    e.select("img[src]").first().attr("src")))
        }
        return links
    }

    fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(getAdapterPosition(), getItemViewType())
        }
        return this
    }


}





