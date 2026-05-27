package me.kbrewster.eventbus.collection;

import me.kbrewster.eventbus.EventBus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

public class SubscriberArrayList extends ArrayList<EventBus.Subscriber> {
    @Override
    public boolean add(EventBus.Subscriber element) {
        if (size() == 0) {
            super.add(element);
        } else {
            int index = Collections.binarySearch(this, element, Comparator.comparingInt((EventBus.Subscriber obj) -> obj.priority));
            if (index < 0) index = -(index + 1);
            super.add(index, element);
        }
        return true;
    }
}
