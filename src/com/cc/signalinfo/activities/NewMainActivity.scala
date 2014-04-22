/*
package com.cc.signalinfo.activities

import java.util.Locale
import android.support.v7.app.ActionBarActivity
import android.support.v7.app.ActionBar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.cc.signalinfo.activities.NewMainActivity.DummyFragment
import com.cc.signalinfo.R

/**
 * Not currently used, but will be eventually.
 */
class NewMainActivity extends ActionBarActivity with ActionBar.TabListener
{
    implicit def pageChangeLambda(funct: (Int) => Unit) = new ViewPager.SimpleOnPageChangeListener {
        override def onPageSelected(position: Int): Unit = funct
    }

    override def onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val actionBar: ActionBar = getSupportActionBar
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS)
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager)
        mViewPager = findViewById(R.id.pager).asInstanceOf[ViewPager]
        mViewPager.setAdapter(mSectionsPagerAdapter)

        mViewPager.setOnPageChangeListener {
            (position: Int) => actionBar.setSelectedNavigationItem(position)
        }

        for (i ← 0 until mSectionsPagerAdapter.getCount) {
            actionBar.addTab(actionBar.newTab.setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this))
        }
    }

    override def onCreateOptionsMenu(menu: Menu): Boolean = {
        getMenuInflater.inflate(R.menu.options, menu)
        return true
    }

    override def onOptionsItemSelected(item: MenuItem): Boolean = {
        item.getItemId match {
            case R.id.preferences =>
                return true
        }
        return super.onOptionsItemSelected(item)
    }

    def onTabSelected(tab: ActionBar.Tab, fragmentTransaction: FragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition)
    }

    def onTabUnselected(tab: ActionBar.Tab, fragmentTransaction: FragmentTransaction) {
    }

    def onTabReselected(tab: ActionBar.Tab, fragmentTransaction: FragmentTransaction) {
    }

    /**
     * The `android.support.v4.view.PagerAdapter` that will provide
     * fragments for each of the sections. We use a
     * `FragmentPagerAdapter` derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * `android.support.v4.app.FragmentStatePagerAdapter`.
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter = null
    /**
     * The `ViewPager` that will host the section contents.
     */
    private var mViewPager           : ViewPager            = null

    /**
     * A `FragmentPagerAdapter` that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    class SectionsPagerAdapter(val fm: FragmentManager) extends FragmentPagerAdapter(fm)
    {
        def getItem(position: Int): Fragment = {
            return DummyFragment.newInstance(position + 1)
        }

        def getCount: Int = {
            return 3
        }

        override def getPageTitle(position: Int): CharSequence = {
            val l: Locale = Locale.getDefault
            position match {
                case 0 =>
                    return getString(R.string.title_section1).toUpperCase(l)
                case 1 =>
                    return getString(R.string.title_section2).toUpperCase(l)
                case 2 =>
                    return getString(R.string.title_section3).toUpperCase(l)
            }
            return null
        }
    }

}

object NewMainActivity
{

    /**
     * A dummy fragment containing a simple view.
     */
    object DummyFragment
    {
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        def newInstance(sectionNumber: Int): NewMainActivity.DummyFragment = {
            val fragment: DummyFragment = new DummyFragment
            val args: Bundle = new Bundle
            args.putInt(ARG_SECTION_NUMBER, sectionNumber)
            fragment.setArguments(args)
            return fragment
        }

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        final val ARG_SECTION_NUMBER: String = "section_number"
    }

    class DummyFragment extends Fragment
    {
        override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
            val rootView: View = inflater.inflate(R.layout.radio_signal_fragment, container, false)
            /*     val dummyTextView: TextView = rootView.findViewById(R.id.section_label).asInstanceOf[TextView]
                 dummyTextView.setText(Integer.toString(getArguments.getInt(DummyFragment.ARG_SECTION_NUMBER)))*/
            return rootView
        }
    }

}*/
