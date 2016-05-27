package com.auditeTeam9;


/**
 * Control commands
 */
public class Command {

    /**
     * Base command
     */
    public static interface Base {

        /**
         * Build command payload
         * @return Payload
         */
        String build();
    }

    public static class Velocity implements Base {

        public enum Speed {
            Slow('7'),
            Mid('8'),
            Fast('9');

            char value;

            Speed(char value) { this.value = value; }
        }

        private Speed speed;

        public Velocity(Speed speed) { this.speed = speed; }

        @Override
        public String build() {
            return "" + speed.value;
        }
    }

    /**
     * Move in the specified direction
     */
    public static class Move implements Base {

        public enum Direction {
            Forward('f'),
            Backward('b');

            char value;

            Direction(char value) {
                this.value = value;
            }
        }

        private Direction direction;

        public Move(Direction direction) {
            this.direction = direction;
        }

        @Override
        public String build() {
            return "m" + direction.value;
        }
    }

    /**
     * Turn in the specified direction
     */
    public static class Turn implements Base {

        public enum Direction {
            Left('l'),
            Right('r');

            private char value;

            Direction(char value) {
                this.value = value;
            }
        }

        public static final int INDEFINITELY = 0;

        private Direction direction;
        private int angle;

        public Turn(Direction direction, int angle) {
            this.direction = direction;
            this.angle = angle;
        }

        @Override
        public String build() {
            return "t" + direction.value + ((char) angle);
        }
    }

    /**
     * Stop
     */
    public static class Stop implements Base {

        public Stop() {}

        @Override
        public String build() {
            return "s";
        }
    }

    /**
     * Toggle feature or light
     */
    public static class Toggle implements Base {

        public enum Feature {
            FrontProximity('f'),
            RearProximity('r'),
            Infrared('i'),
            FrontLight('l');

            private char value;

            Feature(char value) {
                this.value = value;
            }
        }

        public enum Status {
            ON('1'),
            OFF('0');

            private char value;

            Status(char value) {
                this.value = value;
            }
        }

        private Feature feature;
        private Status status;

        public Toggle(Feature feature, Status status) {
            this.feature = feature;
            this.status = status;
        }

        @Override
        public String build() {
            return "o" + feature.value + status.value;
        }
    }
}
