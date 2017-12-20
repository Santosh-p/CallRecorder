package myapp.com.callrecorder;

import android.view.View;

/**
 * Created by SSPL on 12/18/17.
 */

public interface  ItemClickListener {
    void onClick(View view, int position, boolean isLongClick);
}
