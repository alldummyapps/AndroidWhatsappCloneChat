package edu.yonatan.mywhatsupclonechat.tabs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import edu.yonatan.mywhatsupclonechat.contact_requests.RequestFragment;
import edu.yonatan.mywhatsupclonechat.friend_list_contact.ContactsFragment;
import edu.yonatan.mywhatsupclonechat.group_chat.GroupsFragment;
import edu.yonatan.mywhatsupclonechat.private_chat_contacts.ChatFragment;


public class TabsAccessorAdapter extends FragmentPagerAdapter {


    public TabsAccessorAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;

            case 1:

                GroupsFragment groupsFragment = new GroupsFragment();
                return groupsFragment;
            case 2:
                ContactsFragment contactsFragment = new ContactsFragment();
                return contactsFragment;
            case 3:
                RequestFragment requestsFragment = new RequestFragment();
                return requestsFragment;


            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Chats";

            case 1:
                return "Groups";

            case 2:
                return "Contacts";
            case 3:
                return "Requests";


            default:
                return null;
        }
    }
}
