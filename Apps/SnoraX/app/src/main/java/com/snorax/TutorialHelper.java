package com.snorax;

import android.app.Activity;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class TutorialHelper {

    private Activity activity;

    public TutorialHelper(Activity activity) {
        this.activity = activity;
    }

    public void startTutorial() {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // Delay between showcases

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(activity, "tutorial_sequence");
        sequence.setConfig(config);

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(activity)
                        .setTarget(activity.findViewById(R.id.fabBell))
                        .setTitleText("Notification Button")
                        .setContentText("This button allows you to mute, unmute, or set vibrate mode for all lectures.")
                        .setDismissText("GOT IT")
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(activity)
                        .setTarget(activity.findViewById(R.id.btnLectureCount))
                        .setTitleText("Lecture Count")
                        .setContentText("Set the number of lectures per day using this button.")
                        .setDismissText("GOT IT")
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(activity)
                        .setTarget(activity.findViewById(R.id.btnStartTime))
                        .setTitleText("Start Time")
                        .setContentText("Set the start time for your lectures using this button.")
                        .setDismissText("GOT IT")
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(activity)
                        .setTarget(activity.findViewById(R.id.btnDuration))
                        .setTitleText("Duration")
                        .setContentText("Set the duration of each lecture using this button.")
                        .setDismissText("GOT IT")
                        .build()
        );

        sequence.start();
    }
}