package MailDeliveryTask;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static final String AUSTIN_POWERS = "Austin Powers";
    public static final String WEAPONS = "weapons";
    public static final String BANNED_SUBSTANCE = "banned substance";

    public static void main(String[] args) {
    }

    public static interface Sendable {
        String getFrom();
        String getTo();
    }

    public static abstract class AbstractSendable implements Sendable {

        protected final String from;
        protected final String to;

        public AbstractSendable(String from, String to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public String getFrom() {
            return from;
        }

        @Override
        public String getTo() {
            return to;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AbstractSendable that = (AbstractSendable) o;

            if (!from.equals(that.from)) return false;
            if (!to.equals(that.to)) return false;

            return true;
        }

    }


    public static class MailMessage extends AbstractSendable {

        private final String message;

        public MailMessage(String from, String to, String message) {
            super(from, to);
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            MailMessage that = (MailMessage) o;

            if (message != null ? !message.equals(that.message) : that.message != null) return false;

            return true;
        }

    }

    public static class MailPackage extends AbstractSendable {
        private final Package content;

        public MailPackage(String from, String to, Package content) {
            super(from, to);
            this.content = content;
        }

        public Package getContent() {
            return content;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            MailPackage that = (MailPackage) o;

            if (!content.equals(that.content)) return false;

            return true;
        }

    }

    public static class Package {
        private final String content;
        private final int price;

        public Package(String content, int price) {
            this.content = content;
            this.price = price;
        }

        public String getContent() {
            return content;
        }

        public int getPrice() {
            return price;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Package aPackage = (Package) o;

            if (price != aPackage.price) return false;
            if (!content.equals(aPackage.content)) return false;

            return true;
        }
    }

    public static interface MailService {
        Sendable processMail(Sendable mail);
    }

    /*
    Класс, в котором скрыта логика настоящей почты
    */
    public static class RealMailService implements MailService {

        @Override
        public Sendable processMail(Sendable mail) {
            // Здесь описан код настоящей системы отправки почты.
            return mail;
        }
    }

    //МОЙ КОД ДАЛЬШЕ

    public static class UntrustworthyMailWorker implements MailService {
        private MailService[] mailServiceMassive;
        RealMailService realMailService = new RealMailService();
        public UntrustworthyMailWorker(MailService[] mailServiceMassive) {
            this.mailServiceMassive = mailServiceMassive;
        }

        @Override
        public Sendable processMail(Sendable mail) {
            for (int i = 0; i < mailServiceMassive.length; i++) {
                mail = mailServiceMassive[i].processMail(mail);
            }
            mail = realMailService.processMail(mail);
            return mail;
        }

        public RealMailService getRealMailService() {
            return realMailService;
        }
    }

    public static class Spy implements MailService {
        private final Logger LOGGER;

        public Spy(Logger logger) {
            this.LOGGER = logger;
        }

        @Override
        public Sendable processMail(Sendable mail) {
            if (mail instanceof MailMessage) {
                MailMessage mm = (MailMessage) mail;
                if (mm.getFrom().equals(AUSTIN_POWERS) || mm.getTo().equals(AUSTIN_POWERS)) {
                    LOGGER.log(Level.WARNING, "Detected target mail correspondence: from {0} to {1} \"{2}\"", new Object[]{mm.getFrom(), mm.getTo(), mm.getMessage()});
                } else {
                    LOGGER.log(Level.INFO, "Usual correspondence: from {0} to {1}", new Object[]{mm.getFrom(), mm.getTo()});
                }
            }
            return mail;
        }
    }

    public static class Thief implements MailService {
        private int stolenSummary;
        private int minToStole;

        public Thief(int minToStole) {
            this.minToStole = minToStole;
        }

        @Override
        public Sendable processMail(Sendable mail) {
            if (mail instanceof Package && ((Package) mail).getPrice() >= minToStole) {
                Package mm = (Package) mail;
                stolenSummary += mm.getPrice();
                String prevContent = mm.getContent();
                Package newPackage = new Package(String.format("stones instead of %s", prevContent), 0);
                return (Sendable) newPackage;
            }
            if (mail instanceof MailPackage && ((MailPackage) mail).getContent().getPrice() >= minToStole) {
                Package mm = ((MailPackage) mail).getContent();
                stolenSummary += mm.getPrice();
                String prevContent = mm.getContent();
                Package newPackage = new Package(String.format("stones instead of %s", prevContent), 0);
                MailPackage newMailPackage = new MailPackage(((MailPackage) mail).from, ((MailPackage) mail).to, newPackage);
                return newMailPackage;
            }
            return mail;
        }

        public int getStolenValue() {
            return stolenSummary;
        }
    }

    public static class Inspector implements MailService {
        @Override
        public Sendable processMail(Sendable mail) {
            Package pack;
            if (mail instanceof MailPackage) {
                pack = ((MailPackage) mail).getContent();
                if (pack.getContent().contains(WEAPONS)) {
                    throw new IllegalPackageException(WEAPONS);
                }
                if (pack.getContent().contains(BANNED_SUBSTANCE)) {
                    throw new IllegalPackageException(BANNED_SUBSTANCE);
                }
                if (pack.getContent().contains("stones")) {
                    throw new StolenPackageException("stones");
                }
            }
            if (mail instanceof Package) {
                pack = (Package) mail;
                if (pack.getContent().contains(WEAPONS)) {
                    throw new IllegalPackageException(WEAPONS);
                }
                if (pack.getContent().contains(BANNED_SUBSTANCE)) {
                    throw new IllegalPackageException(BANNED_SUBSTANCE);
                }
                if (pack.getContent().contains("stones")) {
                    throw new StolenPackageException("stones");
                }
            }
            return mail;
        }
    }

    public static class IllegalPackageException extends RuntimeException {
        public IllegalPackageException(String message) {
            super(message);
        }
    }
    public static class StolenPackageException extends RuntimeException {
        public StolenPackageException(String message) {
            super(message);
        }
    }
}
