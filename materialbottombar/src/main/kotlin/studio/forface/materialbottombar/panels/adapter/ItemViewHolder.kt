package studio.forface.materialbottombar.panels.adapter

import android.os.Handler
import android.view.View
import androidx.core.os.postDelayed
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.drawer_item_base.view.*
import studio.forface.materialbottombar.panels.AbsMaterialPanel
import studio.forface.materialbottombar.panels.AbsMaterialPanel.BaseBody.Companion.SELECTION_DELAY_MS
import studio.forface.materialbottombar.panels.items.BasePanelItem
import studio.forface.materialbottombar.panels.items.PanelItem
import studio.forface.materialbottombar.panels.items.extra.ButtonItem
import studio.forface.materialbottombar.utils.constraintParams
import studio.forface.materialbottombar.utils.dpToPixels

/**
 * @author Davide Giuseppe Farella
 * A [RecyclerView.ViewHolder] for the Panel Adapter
 */
open class ItemViewHolder<B: AbsMaterialPanel.BaseBody<*>>(
        itemView: View,
        protected val panelBody: B,
        protected val closePanel: () -> Unit
): RecyclerView.ViewHolder( itemView ) {

    /** @return a [CharSequence] title of the item, read directly from TextView for simplicity */
    private val title: CharSequence get() = itemView.item_title.text

    /** Bind the given [PanelItem] */
    fun bind( panelItem: PanelItem ) {
        with( itemView ) {

            // Only handle BasePanelItem since its supertypes don't need to be bind ( e.g. Divider )
            if (panelItem is BasePanelItem<*>) {
                item_icon.alpha = panelItem.iconAlpha
                item_icon.constraintParams!!.marginStart = dpToPixels(panelItem.iconMarginStartDp).toInt()
                item_title.constraintParams!!.marginStart = dpToPixels(panelItem.iconMarginEndDp).toInt()

                panelItem.applyTitleTo( item_title )
                panelItem.applyIconTo( item_icon,true )

                panelItem.applyBadgeTo( item_badge )
                panelItem.applyButtonTo( item_button )

                panelBody.applySelectionTo(this, item_icon, item_title, panelItem.selected )

                //itemView.isEnabled = panelItem.selectable
                itemView.setOnClickListener( itemClickListener( panelItem ) )
                itemView.item_button
                        .setOnClickListener( buttonItemClickListener( panelItem.buttonItem ) )
            }
        }
    }

    /** @return a [View.OnClickListener] for the given [BasePanelItem] */
    protected open val itemClickListener: (BasePanelItem<*>) -> (View) -> Unit get() = { item -> {
        handleBaseClicks( item )
        // Close Panel and set the item selected in panelBody with a delay for let the ripple
        // animation finish first
        Handler().postDelayed( SELECTION_DELAY_MS ) {
            if ( panelBody.closeOnClick ) closePanel()
            item.selected = item.selectable
            panelBody.setSelected( item.id )
        }
    } }

    /** Trigger the click actions to the [item] itself and to [panelBody] */
    protected fun handleBaseClicks( item: BasePanelItem<*> ) {
        // Trigger the click action to the item
        @Suppress("UNCHECKED_CAST")
        ( item as BasePanelItem<BasePanelItem<*>> ).onClick( item )
        // Trigger the click action to panelBody
        panelBody.onItemClick( item.id, title )
    }

    /** @return a [View.OnClickListener] for the given [ButtonItem] */
    private val buttonItemClickListener: (ButtonItem) -> (View) -> Unit get() = { buttonItem -> {
        panelBody.onItemClick( buttonItem.id, itemView.item_button.text )
    } }
}