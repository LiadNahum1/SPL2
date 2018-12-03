package bgu.spl.mics.Messages;

import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;

public class CheckAvailabilityEvent implements Event<Boolean> {
        private BookInventoryInfo book;
        public CheckAvailabilityEvent(BookInventoryInfo book){
            this.book = book;
        }

        public BookInventoryInfo getBook(){
            return this.book;
        }
    }

