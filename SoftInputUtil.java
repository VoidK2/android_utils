
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class SoftInputUtil {

    private int softInputHeight = 0;
    private boolean softInputHeightChanged = false;
    private boolean isNavigationBarShow = false;
    private int navigationHeight = 0;
    private boolean isSoftInputShowing = false;
    private static final String RESOURCE_NAME = "navigation_bar_height";
    private static final String RESOURCE_TYPE = "dimen";
    private static final String RESOURCE_PACKAGE = "android";

    private View anyView;
    private ISoftInputChanged listener;

    public interface ISoftInputChanged {
        void onChanged(boolean isSoftInputShow, int softInputHeight, int viewOffset);
    }

    public void attachSoftInput(final View anyView, final ISoftInputChanged listener) {
        if (anyView == null || listener == null)
            return;

        final View rootView = anyView.getRootView();
        if (rootView == null)
            return;

        navigationHeight = getNavigationBarHeight(anyView.getContext());

        this.anyView = anyView;
        this.listener = listener;

        rootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                int rootHeight = rootView.getHeight();
                Rect rect = new Rect();
                rootView.getWindowVisibleDisplayFrame(rect);
                if (rootHeight - rect.bottom == navigationHeight) {
                    isNavigationBarShow = true;
                } else if (rootHeight - rect.bottom == 0) {
                    isNavigationBarShow = false;
                }

                //cal softInput height
                boolean isSoftInputShow = false;
                int softInputHeight2 = 0;
                int mutableHeight = isNavigationBarShow == true ? navigationHeight : 0;
                if (rootHeight - mutableHeight > rect.bottom) {
                    isSoftInputShow = true;
                    softInputHeight2 = rootHeight - mutableHeight - rect.bottom;
                    if (softInputHeight != softInputHeight2) {
                        softInputHeightChanged = true;
                        softInputHeight = softInputHeight2;
                    } else {
                        softInputHeightChanged = false;
                    }
                }

                int[] location = new int[2];
                anyView.getLocationOnScreen(location);

                if (isSoftInputShowing != isSoftInputShow || (isSoftInputShow && softInputHeightChanged)) {
                    if (listener != null) {
                        listener.onChanged(isSoftInputShow, softInputHeight, location[1] + anyView.getHeight() - rect.bottom);
                    }
                    isSoftInputShowing = isSoftInputShow;
                }
            }
        });
    }


    //***************STATIC METHOD******************

    public static int getNavigationBarHeight(Context context) {
        if (context == null)
            return 0;
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier(RESOURCE_NAME, RESOURCE_TYPE, RESOURCE_PACKAGE);
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    public static void showSoftInput(View view) {
        if (view == null)
            return;
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(view, 0);
        }
    }

    public static void hideSoftInput(View view) {
        if (view == null)
            return;
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
