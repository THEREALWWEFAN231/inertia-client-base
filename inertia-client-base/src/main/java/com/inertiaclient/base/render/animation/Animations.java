package com.inertiaclient.base.render.animation;


import java.util.ArrayList;
import java.util.HashMap;

public class Animations {

    public static final ArrayList<CustomTweenEquation> ALL_ANIMATIONS = new ArrayList<>();
    public static final HashMap<String, CustomTweenEquation> ANIMATIONS_BY_NAME = new HashMap<>();

    public static final CustomTweenEquation linear = new CustomTweenEquation("linear") {
        @Override
        public float compute(float x) {
            return x;
        }
    };

    //https://easings.net
    public static final CustomTweenEquation easeInSine = new CustomTweenEquation("easeInSine") {
        @Override
        public float compute(float x) {
            return (float) (1 - Math.cos((x * Math.PI) / 2));
        }
    };

    public static final CustomTweenEquation easeOutSine = new CustomTweenEquation("easeOutSine") {
        @Override
        public float compute(float x) {
            return (float) Math.sin((x * Math.PI) / 2);
        }
    };

    public static final CustomTweenEquation easeInOutSine = new CustomTweenEquation("easeInOutSine") {
        @Override
        public float compute(float x) {
            return (float) (-(Math.cos(Math.PI * x) - 1) / 2);
        }
    };

    public static final CustomTweenEquation easeInQuad = new CustomTweenEquation("easeInQuad") {
        @Override
        public float compute(float x) {
            return x * x;
        }
    };

    public static final CustomTweenEquation easeOutQuad = new CustomTweenEquation("easeOutQuad") {
        @Override
        public float compute(float x) {
            return 1 - (1 - x) * (1 - x);
        }
    };

    public static final CustomTweenEquation easeInOutQuad = new CustomTweenEquation("easeInOutQuad") {
        @Override
        public float compute(float x) {
            return x < 0.5 ? 2 * x * x : (float) (1 - Math.pow(-2 * x + 2, 2) / 2);
        }
    };

    public static final CustomTweenEquation easeInCubic = new CustomTweenEquation("easeInCubic") {
        @Override
        public float compute(float x) {
            return x * x * x;
        }
    };

    public static final CustomTweenEquation easeOutCubic = new CustomTweenEquation("easeOutCubic") {
        @Override
        public float compute(float x) {
            return (float) (1 - Math.pow(1 - x, 3));
        }
    };

    public static final CustomTweenEquation easeInOutCubic = new CustomTweenEquation("easeInOutCubic") {
        @Override
        public float compute(float x) {
            return x < 0.5 ? 4 * x * x * x : (float) (1 - Math.pow(-2 * x + 2, 3) / 2);
        }
    };

    public static final CustomTweenEquation easeInQuart = new CustomTweenEquation("easeInQuart") {
        @Override
        public float compute(float x) {
            return x * x * x * x;
        }
    };

    public static final CustomTweenEquation easeOutQuart = new CustomTweenEquation("easeOutQuart") {
        @Override
        public float compute(float x) {
            return (float) (1 - Math.pow(1 - x, 4));
        }
    };

    public static final CustomTweenEquation easeInOutQuart = new CustomTweenEquation("easeInOutQuart") {
        @Override
        public float compute(float x) {
            return x < 0.5 ? 8 * x * x * x * x : (float) (1 - Math.pow(-2 * x + 2, 4) / 2);
        }
    };

    public static final CustomTweenEquation easeInQuint = new CustomTweenEquation("easeInQuint") {
        @Override
        public float compute(float x) {
            return x * x * x * x * x;
        }
    };

    public static final CustomTweenEquation easeOutQuint = new CustomTweenEquation("easeOutQuint") {
        @Override
        public float compute(float x) {
            return (float) (1 - Math.pow(1 - x, 5));
        }
    };

    public static final CustomTweenEquation easeInOutQuint = new CustomTweenEquation("easeInOutQuint") {
        @Override
        public float compute(float x) {
            return x < 0.5 ? 16 * x * x * x * x * x : (float) (1 - Math.pow(-2 * x + 2, 5) / 2);
        }
    };

    public static final CustomTweenEquation easeInExpo = new CustomTweenEquation("easeInExpo") {
        @Override
        public float compute(float x) {
            return x == 0 ? 0 : (float) Math.pow(2, 10 * x - 10);
        }
    };

    public static final CustomTweenEquation easeOutExpo = new CustomTweenEquation("easeOutExpo") {
        @Override
        public float compute(float x) {
            return x == 1 ? 1 : (float) (1 - Math.pow(2, -10 * x));
        }
    };

    public static final CustomTweenEquation easeInOutExpo = new CustomTweenEquation("easeInOutExpo") {
        @Override
        public float compute(float x) {
            return x == 0 ? 0 : (float) (x == 1 ? 1 : x < 0.5 ? Math.pow(2, 20 * x - 10) / 2 : (2 - Math.pow(2, -20 * x + 10)) / 2);
        }
    };

    public static final CustomTweenEquation easeInCirc = new CustomTweenEquation("easeInCirc") {
        @Override
        public float compute(float x) {
            return (float) (1 - Math.sqrt(1 - Math.pow(x, 2)));
        }
    };

    public static final CustomTweenEquation easeOutCirc = new CustomTweenEquation("easeOutCirc") {
        @Override
        public float compute(float x) {
            return (float) Math.sqrt(1 - Math.pow(x - 1, 2));
        }
    };

    public static final CustomTweenEquation easeInOutCirc = new CustomTweenEquation("easeInOutCirc") {
        @Override
        public float compute(float x) {
            return (float) (x < 0.5 ? (1 - Math.sqrt(1 - Math.pow(2 * x, 2))) / 2 : (Math.sqrt(1 - Math.pow(-2 * x + 2, 2)) + 1) / 2);
        }
    };

    public static final CustomTweenEquation easeInBack = new CustomTweenEquation("easeInBack") {
        @Override
        public float compute(float x) {
            float c1 = 1.70158f;
            float c3 = c1 + 1;

            return c3 * x * x * x - c1 * x * x;
        }
    };

    public static final CustomTweenEquation easeOutBack = new CustomTweenEquation("easeOutBack") {
        @Override
        public float compute(float x) {
            float c1 = 1.70158f;
            float c3 = c1 + 1;

            return (float) (1 + c3 * Math.pow(x - 1, 3) + c1 * Math.pow(x - 1, 2));
        }
    };

    public static final CustomTweenEquation easeInOutBack = new CustomTweenEquation("easeInOutBack") {
        @Override
        public float compute(float x) {
            float c1 = 1.70158f;
            float c2 = c1 * 1.525f;

            return (float) (x < 0.5 ? (Math.pow(2 * x, 2) * ((c2 + 1) * 2 * x - c2)) / 2 : (Math.pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2);
        }
    };

    public static final CustomTweenEquation easeInElastic = new CustomTweenEquation("easeInElastic") {
        @Override
        public float compute(float x) {
            double c4 = (2 * Math.PI) / 3;

            return x == 0 ? 0 : (float) (x == 1 ? 1 : -Math.pow(2, 10 * x - 10) * Math.sin((x * 10 - 10.75) * c4));
        }
    };

    public static final CustomTweenEquation easeOutElastic = new CustomTweenEquation("easeOutElastic") {
        @Override
        public float compute(float x) {
            double c4 = (2 * Math.PI) / 3;

            return x == 0 ? 0 : (float) (x == 1 ? 1 : Math.pow(2, -10 * x) * Math.sin((x * 10 - 0.75) * c4) + 1);
        }
    };

    public static final CustomTweenEquation easeInOutElastic = new CustomTweenEquation("easeInOutElastic") {
        @Override
        public float compute(float x) {
            double c5 = (2 * Math.PI) / 4.5;

            double sin = Math.sin((20 * x - 11.125) * c5);
            return x == 0 ? 0 : (float) (x == 1 ? 1 : x < 0.5 ? -(Math.pow(2, 20 * x - 10) * sin) / 2 : (Math.pow(2, -20 * x + 10) * sin) / 2 + 1);
        }
    };

    public static final CustomTweenEquation easeInBounce = new CustomTweenEquation("easeInBounce") {
        @Override
        public float compute(float x) {
            return 1 - easeOutBounce.compute(1 - x);
        }
    };

    public static final CustomTweenEquation easeOutBounce = new CustomTweenEquation("easeOutBounce") {
        @Override
        public float compute(float x) {
            float n1 = 7.5625f;
            float d1 = 2.75f;

            if (x < 1 / d1) {
                return n1 * x * x;
            } else if (x < 2 / d1) {
                return n1 * (x -= 1.5 / d1) * x + 0.75f;
            } else if (x < 2.5 / d1) {
                return n1 * (x -= 2.25 / d1) * x + 0.9375f;
            } else {
                return n1 * (x -= 2.625 / d1) * x + 0.984375f;
            }
        }
    };

    public static final CustomTweenEquation easeInOutBounce = new CustomTweenEquation("easeInOutBounce") {
        @Override
        public float compute(float x) {
            return x < 0.5 ? (1 - easeOutBounce.compute(1 - 2 * x)) / 2 : (1 + easeOutBounce.compute(2 * x - 1)) / 2;
        }
    };

}
