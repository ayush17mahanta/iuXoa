package com.iuxoa.datadrop;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class BrowsingAccessibilityService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getPackageName() == null) return;

        String pkg = event.getPackageName().toString();
        AccessibilityNodeInfo nodeInfo = event.getSource();

        if (pkg.equals("com.android.chrome") && nodeInfo != null) {
            traverseNode(nodeInfo);
        }
    }

    private void traverseNode(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) return;

        if (nodeInfo.getText() != null) {
            String text = nodeInfo.getText().toString();
            if (text.startsWith("http")) {
                Log.d("BrowsingTracker", "Detected URL: " + text);
                // TODO: Store anonymously in Firestore or local DB with consent
            }
        }

        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            traverseNode(nodeInfo.getChild(i));
        }
    }

    @Override
    public void onInterrupt() {
        Log.w("BrowsingTracker", "Accessibility Service Interrupted");
    }
}
