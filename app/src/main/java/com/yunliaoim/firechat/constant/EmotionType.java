package com.yunliaoim.firechat.constant;

import com.yunliaoim.firechat.R;
import com.yunliaoim.firechat.fragment.BaseEmotionFragment;
import com.yunliaoim.firechat.fragment.EmotionFragment;

/**
 * 表情类型
 *
 */
public enum EmotionType {
    EMOTION_TYPE_CLASSIC(R.drawable.vector_keyboard_emotion, EmotionFragment.class),   //经典表情
    EMOTION_TYPE_MORE(R.drawable.vector_emotion_more, BaseEmotionFragment.class);       //+点击更多

    private int mEmotionTypeIcon;
    private Class mFragmentClass;

    EmotionType(int emotionTypeIcon, Class fragmentClass) {

        mEmotionTypeIcon = emotionTypeIcon;
        mFragmentClass = fragmentClass;
    }

    public int getEmotionTypeIcon() {

        return mEmotionTypeIcon;
    }

    public Class getFragmentClass() {

        return mFragmentClass;
    }
}
