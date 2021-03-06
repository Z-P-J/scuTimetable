package com.scu.timetable.ui.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.scu.timetable.R;
import com.scu.timetable.bean.ScuSubject;
import com.scu.timetable.ui.fragment.DetailFragment;
import com.scu.timetable.ui.widget.DetailLayout;
import com.zpj.fragmentation.dialog.base.BottomDragDialogFragment;
import com.zpj.toast.ZToast;

public class SubjectDetailDialog extends BottomDragDialogFragment {

    private ScuSubject subject;

    public SubjectDetailDialog setSubject(ScuSubject subject) {
        this.subject = subject;
        return this;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.layout_subject_detail;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);

        if (subject == null) {
            dismiss();
            return;
        }

        TextView courseName = findViewById(R.id.course_name);
        DetailLayout teacherName = findViewById(R.id.teacher_name);
        DetailLayout classRoom = findViewById(R.id.class_room);
        DetailLayout classTime = findViewById(R.id.class_time);

        courseName.setText(subject.getCourseName());
        teacherName.setContent(subject.getTeacher());
        classRoom.setContent(subject.getRoom());
        classTime.setContent(subject.getClassTime());

        if (!TextUtils.isEmpty(subject.getNote())) {
            DetailLayout noteLayout = findViewById(R.id.layout_note);
            noteLayout.setVisibility(View.VISIBLE);
            noteLayout.setContent(subject.getNote());
        }

        ImageView note = findViewById(R.id.subject_note);
        note.setOnClickListener(v -> {
            new SubjectNoteDialog().setSubject(subject).show(context);
            dismiss();
        });
        ImageView more = findViewById(R.id.subject_more);
        more.setOnClickListener(v -> {
            DetailFragment.start(subject);
            dismiss();
        });

        ImageView alarm = findViewById(R.id.subject_alarm);
        alarm.setOnClickListener(v -> {
            //todo alarm
            ZToast.normal("提醒功能未实现！");
        });
    }

}
