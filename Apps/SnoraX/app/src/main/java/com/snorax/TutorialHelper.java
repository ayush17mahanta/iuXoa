package com.snorax;

import android.app.Activity;
import android.graphics.Typeface;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

public class TutorialHelper {

    private final Activity activity;
    private Runnable onComplete;
    private boolean skippable = true;

    public TutorialHelper(Activity activity) {
        this.activity = activity;
    }

    public TutorialHelper setOnComplete(Runnable onComplete) {
        this.onComplete = onComplete;
        return this;
    }

    public TutorialHelper setSkippable(boolean skippable) {
        this.skippable = skippable;
        return this;
    }

    public void startTutorial() {
        new TapTargetSequence(activity)
                .targets(
                        createTarget(R.id.fabBell, "Notification Button",
                                "Mute/unmute or set vibrate mode for all lectures"),
                        createTarget(R.id.btnLectureCount, "Lecture Count",
                                "Set number of lectures per day"),
                        createTarget(R.id.btnStartTime, "Start Time",
                                "Set your first lecture time"),
                        createTarget(R.id.btnDuration, "Duration",
                                "Set duration for each lecture")
                )
                .listener(new TapTargetSequence.Listener() {
                    @Override
                    public void onSequenceFinish() {
                        if (onComplete != null) onComplete.run();
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                        // Optional step handling
                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                        // Handle cancellation
                    }
                })
                .continueOnCancel(skippable)
                .start();
    }

    private TapTarget createTarget(int viewId, String title, String description) {
        return TapTarget.forView(activity.findViewById(viewId), title, description)
                .outerCircleColor(R.color.black)
                .outerCircleAlpha(0.7f)
                .targetCircleColor(android.R.color.white)
                .titleTextColor(android.R.color.white)
                .descriptionTextColor(android.R.color.white)
                .textTypeface(Typeface.SANS_SERIF)
                .dimColor(android.R.color.black)
                .drawShadow(true)
                .cancelable(skippable)
                .tintTarget(true)
                .transparentTarget(true);
    }
}