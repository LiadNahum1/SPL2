package bgu.spl.mics.Messages;

//The event checks if specific book is available and returns its price
public class CheckAvailabilityEvent implements Event<Integer> {
        private String bookTitle;
        public CheckAvailabilityEvent(String bookTitle){
            this.bookTitle = bookTitle;
        }
        public String getBookTitle(){
            return this.bookTitle;
        }
    }

