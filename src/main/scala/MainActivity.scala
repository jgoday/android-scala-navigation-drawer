package com.jgoday.navigationdrawer

import android.app._
import android.content._
import android.content.res.Configuration
import android.os._
import android.support.v4.app.ActionBarDrawerToggle
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.view._
import android.widget._

import Implicits._

class MainActivity extends Activity with TypedActivity {
  implicit val ctx: Context = this

  private lazy val planetTitles =
    getResources.getStringArray(R.array.planets_array)

  private lazy val drawerLayout =
    findView(TR.drawer_layout)

  private lazy val drawerList =
    findView(TR.left_drawer)

  private var mTitle: CharSequence = _
  private var mDrawerTitle: CharSequence = _

  private lazy val drawerToggle = new ActionBarDrawerToggle(
    this,
    drawerLayout,
    R.drawable.ic_drawer,
    R.string.drawer_open,
    R.string.drawer_close) {
    override def onDrawerClosed(view: View) {
      getActionBar.setTitle(mTitle)
      invalidateOptionsMenu()
    }
    override def onDrawerOpened(view: View) {
      getActionBar.setTitle(mDrawerTitle)
      invalidateOptionsMenu()
    }
  }

  override def onCreate(bundle: Bundle) {
    super.onCreate(bundle)
    setContentView(R.layout.main)

    mTitle = getTitle
    mDrawerTitle = getTitle

    drawerList.setAdapter(
      ArrayAdapter(R.layout.drawer_list_item, planetTitles))

    drawerList.setOnItemClickListener(new DrawerItemClickListener)

    getActionBar().setDisplayHomeAsUpEnabled(true)
    getActionBar().setHomeButtonEnabled(true)

    drawerLayout.setDrawerListener(drawerToggle)

    if (bundle == null) {
      selectItem(0)
    }
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    val inflater = getMenuInflater
    inflater.inflate(R.menu.main, menu)
    super.onCreateOptionsMenu(menu)
  }

  override def onPrepareOptionsMenu(menu: Menu): Boolean = {
    val drawerOpen = drawerLayout.isDrawerOpen(drawerList)
    menu.findItem(R.id.action_websearch).setVisible(!drawerOpen)
    super.onPrepareOptionsMenu(menu)
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    if (drawerToggle.onOptionsItemSelected(item)) return true
    else {
      item.getItemId match {
        case R.id.action_websearch =>
          val intent = new Intent(Intent.ACTION_WEB_SEARCH)
          intent.putExtra(SearchManager.QUERY, getActionBar.getTitle)

          if (intent.resolveActivity(getPackageManager) != null) startActivity(intent)
          else Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show
          true
        case _ =>
          super.onOptionsItemSelected(item)
      }
    }
  }

  private def selectItem(pos: Int) {
    val fragment = new PlanetFragment
    fragment.setIntArgument(PlanetFragment.ARG_PLANET_NUMBER, pos)

    val fragmentManager = getFragmentManager
    fragmentManager.beginTransaction.replace(R.id.content_frame, fragment).commit
    drawerList.setItemChecked(pos, true)
    setTitle(planetTitles(pos))
    drawerLayout.closeDrawer(drawerList)
  }

  override def setTitle(title: CharSequence) {
    mTitle = title
    getActionBar.setTitle(mTitle)
  }

  override def onPostCreate(args: Bundle) {
    super.onPostCreate(args)
    drawerToggle.syncState()
  }

  override def onConfigurationChanged(c: Configuration) {
    super.onConfigurationChanged(c)
    drawerToggle.onConfigurationChanged(c)
  }

  private class DrawerItemClickListener extends OnItemClickListener {
    onItemClicked {
      case (_, _, pos, _) =>
        selectItem(pos)
    }
  }

  object PlanetFragment {
    val ARG_PLANET_NUMBER = "planet_number"
  }
  
  class PlanetFragment extends Fragment {
    import PlanetFragment._

    override def onCreateView(inflater: LayoutInflater, container: ViewGroup, args: Bundle): View = {
      val rootView = inflater.inflate(R.layout.fragment_planet, container, false)
      val i = getArguments.getInt(ARG_PLANET_NUMBER)
      val planet = planetTitles(i)

      val imageId = getResources.getIdentifier(
          planet.toLowerCase(java.util.Locale.getDefault()),
          "drawable",
          getActivity.getPackageName)

      rootView.imageView(R.id.image).setImageResource(imageId)

      getActivity.setTitle(planet)
      
      rootView
    }
  }
}
