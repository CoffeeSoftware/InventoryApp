package comcoffeesoftware.httpsgithub.inventarsoft;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adaptor for the RecyclerView containing a list of our saved products
 */

public class AdaptorListaProduse extends RecyclerView.Adapter<AdaptorListaProduse.myHolder> {


    final private MyOnItemClickListener myOnItemClickListener;

    private Cursor cursor;


    public interface MyOnItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }


    public AdaptorListaProduse(Cursor mCursor, MyOnItemClickListener listener) {
        cursor = mCursor;

        myOnItemClickListener = listener;
    }

    @Override
    public AdaptorListaProduse.myHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdListItem = R.layout.produs;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImediately = false;

        View view = inflater.inflate(layoutIdListItem, parent, shouldAttachToParentImediately);

        myHolder viewHolder = new myHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AdaptorListaProduse.myHolder holder, int position) {
        if (!cursor.moveToPosition(position))
            return;

        String text = cursor.getString(cursor.getColumnIndex(DbContract.Produse.COLUMN_NAME));
        holder.numeProdus.setText(text);

    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }


    class myHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView numeProdus;
        ImageView imagineProdus;

        public myHolder(View itemView) {
            super(itemView);
            numeProdus = (TextView) itemView.findViewById(R.id.nume_produs);
            imagineProdus = (ImageView) itemView.findViewById(R.id.imagine_produs);

            itemView.setOnClickListener(this);
        }

        void bind(int index) {

            numeProdus.setText("produs nr " + index);

        }


        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            myOnItemClickListener.onListItemClick(clickedPosition);
        }
    }
}
