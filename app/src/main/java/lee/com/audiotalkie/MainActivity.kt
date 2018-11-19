package lee.com.audiotalkie

import android.Manifest
import android.app.Fragment
import android.app.FragmentManager
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity

import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import lee.com.audiotalkie.presenter.TalkiePresenterImpl
import lee.com.audiotalkie.view.TalkieFragment
import java.util.ArrayList

class MainActivity : AppCompatActivity(), TalkieFragment.OnFragmentInteractionListener {

    //需要申请的运行时权限
    private val permissions = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE)
    //被用户拒绝的权限列表
    private val mPermissionList = ArrayList<String>()

    companion object {

        val FRAGMENT_TAG = "fragment_tag"

        private val MY_PERMISSIONS_REQUEST = 1001
    }

    override fun onFragmentInteraction(uri: Uri?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
//    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
//        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
//        container.adapter = mSectionsPagerAdapter

        init()

        checkPermissions()

        fab.setOnClickListener { view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun init() {
        //初始化view
        val fragmentManager = fragmentManager
        var fragment: TalkieFragment? = fragmentManager.findFragmentByTag(FRAGMENT_TAG) as? TalkieFragment
        if (fragment == null) {
            fragment = TalkieFragment.newInstance("", "")
            fragmentManager.beginTransaction().add(R.id.fl_container, fragment, FRAGMENT_TAG).commit()
        }

        //初始化presenter
        TalkiePresenterImpl(fragment)
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (i in permissions.indices) {
                if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i])
                }
            }
            if (!mPermissionList.isEmpty()) {
                val permissions = mPermissionList.toTypedArray()
                ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == MY_PERMISSIONS_REQUEST) {
            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Log.e("Lee", permissions[i] + " 权限被用户禁止！")
                }
            }
            // todo while be refused
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        val id = item.itemId
//
//        if (id == R.id.action_settings) {
//            return true
//        }
//
//        return super.onOptionsItemSelected(item)
//    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
//    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
//
//        override fun getItem(position: Int): Fragment {
//            // getItem is called to instantiate the fragment for the given page.
//            // Return a PlaceholderFragment (defined as a static inner class below).
//            return TalkieFragment.newInstance("", "")
//        }
//
//        override fun getCount(): Int {
//            // Show 3 total pages.
//            return 1
//        }
//    }

    /**
     * A placeholder fragment containing a simple view.
     */
//    class PlaceholderFragment : Fragment() {
//
//        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
//                                  savedInstanceState: Bundle?): View? {
//            val rootView = inflater.inflate(R.layout.fragment_main, container, false)
//            rootView.section_label.text = getString(R.string.section_format, arguments?.getInt(ARG_SECTION_NUMBER))
//            return rootView
//        }
//
//        companion object {
//            /**
//             * The fragment argument representing the section number for this
//             * fragment.
//             */
//            private val ARG_SECTION_NUMBER = "section_number"
//
//            /**
//             * Returns a new instance of this fragment for the given section
//             * number.
//             */
//            fun newInstance(sectionNumber: Int): PlaceholderFragment {
//                val fragment = PlaceholderFragment()
//                val args = Bundle()
//                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
//                fragment.arguments = args
//                return fragment
//            }
//        }
//    }

}
