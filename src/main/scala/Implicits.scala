package com.jgoday.navigationdrawer {

  import scala.collection.JavaConversions._
  import android.app._
  import android.os._
  import android.content._
  import android.widget._
  import android.view._

  object ArrayAdapter {
    def apply[T](
      layoutId: Int, values: Array[T])
      (implicit ctx: Context): ArrayAdapter[T] = {
        new ArrayAdapter(ctx, layoutId, values.toList)
    }
  }

  object Implicits {
    implicit class Components(view: View) {
      def imageView(id: Int): ImageView =
        view.findViewById(id).asInstanceOf[ImageView]
    }

    implicit class Framents(f: Fragment) {
      def setIntArgument(name: String, value: Int) {
        val args = new Bundle
        args.putInt(name, value)
        f.setArguments(args)
      }
    }
  }

  class OnItemClickListener extends AdapterView.OnItemClickListener {
    type ItemClickHandler = PartialFunction[
      (AdapterView[_ <: Adapter], View, Int, Long), Unit]

    private var itemClickHandler: ItemClickHandler = _
    private val itemClickHandlerDefault: ItemClickHandler = { case _ => None }

    def onItemClicked(pf: ItemClickHandler) {
      itemClickHandler = pf
    }

    override def onItemClick(parent: AdapterView[_], view: View, pos: Int, id: Long) {
      (itemClickHandler orElse itemClickHandlerDefault)(
        (parent.asInstanceOf[AdapterView[_ <: Adapter]], view, pos, id))
    }
  }
}