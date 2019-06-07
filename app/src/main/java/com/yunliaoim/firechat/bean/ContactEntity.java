package com.yunliaoim.firechat.bean;

import cn.ittiger.indexlist.entity.BaseEntity;

import org.jivesoftware.smack.roster.RosterEntry;

/**
 * 联系人实体
 */
public class ContactEntity implements BaseEntity {
    private RosterEntry mRosterEntry;

    public ContactEntity(RosterEntry rosterEntry) {
        mRosterEntry = rosterEntry;
    }

    @Override
    public String getIndexField() {
        return mRosterEntry.getName();
    }

    public RosterEntry getRosterEntry() {
        return mRosterEntry;
    }
}
