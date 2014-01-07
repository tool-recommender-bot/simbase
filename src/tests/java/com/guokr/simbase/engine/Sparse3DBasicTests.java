package com.guokr.simbase.engine;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.guokr.simbase.SimConfig;
import com.guokr.simbase.TestableCallback;

public class Sparse3DBasicTests {
    public static SimEngineImpl engine;

    @BeforeClass
    public static void setup() {

        Map<String, Object> settings = new HashMap<String, Object>();
        Map<String, Object> defaults = new HashMap<String, Object>();
        Map<String, Object> basis = new HashMap<String, Object>();
        Map<String, Object> sparse = new HashMap<String, Object>();
        Map<String, Object> econf = new HashMap<String, Object>();
        sparse.put("accumuFactor", 0.01);
        sparse.put("sparseFactor", 2048);
        basis.put("vectorSetType", "sparse");
        econf.put("savepath", "data");
        econf.put("saveinterval", 7200000);
        econf.put("maxlimits", 20);
        econf.put("loadfactor", 0.75);
        econf.put("bycount", 100);
        defaults.put("sparse", sparse);
        defaults.put("basis", basis);
        defaults.put("engine", econf);
        settings.put("defaults", defaults);
        SimConfig config = new SimConfig(settings);

        engine = new SimEngineImpl(config.getSub("engine"));

        String[] components = new String[3];
        for (int i = 0; i < components.length; i++) {
            components[i] = "B" + String.valueOf(i);
        }
        try {
            engine.bmk(TestableCallback.noop(), "btest", components);
            Thread.sleep(100);
            engine.vmk(TestableCallback.noop(), "btest", "vtest");
            Thread.sleep(100);
            engine.rmk(TestableCallback.noop(), "vtest", "vtest", "cosinesq");
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        engine.bget(TestableCallback.noop(), "btest");
        try {
            engine.vadd(TestableCallback.noop(), "vtest", 2, new float[] { 0.9f, 0.1f, 0.01f });
            Thread.sleep(100);
            engine.vadd(TestableCallback.noop(), "vtest", 3, new float[] { 0.9f, 0f, 0.11f });
            Thread.sleep(100);
            engine.vadd(TestableCallback.noop(), "vtest", 5, new float[] { 0.1f, 0.9f, 0.01f });
            Thread.sleep(100);
            engine.vadd(TestableCallback.noop(), "vtest", 7, new float[] { 0.1f, 0f, 0.91f });
            Thread.sleep(100);
            engine.vadd(TestableCallback.noop(), "vtest", 11, new float[] { 0f, 0.9f, 0.11f });
            Thread.sleep(100);
            engine.vadd(TestableCallback.noop(), "vtest", 13, new float[] { 0f, 0.1f, 0.91f });
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testrec() {
        TestableCallback test = new TestableCallback() {
            @Override
            public void excepted() {
                isIntegerList(new int[] { 7, 11, 3, 5, 2 });
            }
        };
        engine.rrec(test, "vtest", 13, "vtest");
        test.waitForFinish();
        test.validate();
        TestableCallback test2 = new TestableCallback() {
            @Override
            public void excepted() {
                isIntegerList(new int[] { 13, 3, 11, 2, 5 });
            }
        };
        engine.rrec(test2, "vtest", 7, "vtest");
        test2.waitForFinish();
        test2.validate();
    }

    @Test
    public void testvget() {
        TestableCallback test2 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0.891f, 0.099f, 0.01f });
            }
        };
        engine.vget(test2, "vtest", 2);
        test2.waitForFinish();
        test2.validate();

        TestableCallback test3 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0.891f, 0f, 0.109f });
            }
        };
        engine.vget(test3, "vtest", 3);
        test3.waitForFinish();
        test3.validate();

        TestableCallback test5 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0.099f, 0.891f, 0.01f });
            }
        };
        engine.vget(test5, "vtest", 5);
        test5.waitForFinish();
        test5.validate();

        TestableCallback test7 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0.099f, 0f, 0.901f });
            }
        };
        engine.vget(test7, "vtest", 7);
        test7.waitForFinish();
        test7.validate();

        TestableCallback test11 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0f, 0.891f, 0.109f });
            }
        };
        engine.vget(test11, "vtest", 11);
        test11.waitForFinish();
        test11.validate();

        TestableCallback test13 = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0f, 0.099f, 0.901f });
            }
        };
        engine.vget(test13, "vtest", 13);
        test13.waitForFinish();
        test13.validate();
    }

    @Test
    public void testrlist() {
        TestableCallback test = new TestableCallback() {
            @Override
            public void excepted() {
                isStringList(new String[] { "vtest" });
            }
        };
        engine.rlist(test, "vtest");
        test.waitForFinish();
        test.validate();
    }

    @Test
    public void testvrem() {
        TestableCallback testok = new TestableCallback() {
            @Override
            public void excepted() {
                isOk();
            }
        };
        engine.vrem(testok, "vtest", 5);
        testok.waitForFinish();
        testok.validate();
        TestableCallback test = new TestableCallback() {
            @Override
            public void excepted() {
                isIntegerList(new int[] { 7, 11, 3, 2 });
            }
        };
        engine.rrec(test, "vtest", 13, "vtest");
        test.waitForFinish();
        test.validate();
        try {
            engine.vadd(TestableCallback.noop(), "vtest", 5, new float[] { 0.1f, 0.9f, 0f });
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TestableCallback test2 = new TestableCallback() {
            @Override
            public void excepted() {
                isIntegerList(new int[] { 7, 11, 3, 5, 2 });
            }
        };
        engine.rrec(test2, "vtest", 13, "vtest");
        test2.waitForFinish();
        test2.validate();
    }

    // @Test
    public void testvset() {
        // replace 2 with 7 ,and 7 with 2
        TestableCallback testok = new TestableCallback() {
            @Override
            public void excepted() {
                isOk();
            }
        };
        engine.vset(testok, "vtest", 2, new float[] { 0.1f, 0f, 0.9f });
        testok.waitForFinish();
        testok.validate();
        engine.vset(testok, "vtest", 7, new float[] { 0.9f, 0.1f, 0f });
        testok.waitForFinish();
        testok.validate();
        TestableCallback test1 = new TestableCallback() {
            @Override
            public void excepted() {
                isIntegerList(new int[] { 2, 11, 5, 3, 7 });
            }
        };
        engine.rrec(test1, "vtest", 13, "vtest");
        // Restored to their original
        test1.waitForFinish();
        test1.validate();
        engine.vset(testok, "vtest", 2, new float[] { 0.9f, 0.1f, 0f });
        testok.waitForFinish();
        testok.validate();
        engine.vset(testok, "vtest", 7, new float[] { 0.1f, 0f, 0.9f });
        testok.waitForFinish();
        testok.validate();

        // so recommander restored
        TestableCallback test2 = new TestableCallback() {
            @Override
            public void excepted() {
                isIntegerList(new int[] { 7, 11, 5, 3, 2 });
            }
        };
        engine.rrec(test2, "vtest", 13, "vtest");
        test2.waitForFinish();
        test2.validate();
    }

    @Test
    public void testblist() {
        TestableCallback test = new TestableCallback() {
            @Override
            public void excepted() {
                isStringList(new String[] { "btest" });
            }
        };
        engine.blist(test);
        test.waitForFinish();
        test.validate();
    }

    @Test
    public void testvlist() {
        TestableCallback test = new TestableCallback() {
            @Override
            public void excepted() {
                isStringList(new String[] { "vtest" });
            }
        };
        engine.vlist(test, "btest");
        test.waitForFinish();
        test.validate();
    }

    @Test
    public void testvacc() {
        TestableCallback testok = new TestableCallback() {
            @Override
            public void excepted() {
                isOk();
            }
        };
        engine.vacc(testok, "vtest", 5, new float[] { 0.1f, 0.9f, 0f });
        testok.waitForFinish();
        testok.validate();
        TestableCallback testget = new TestableCallback() {
            @Override
            public void excepted() {
                isFloatList(new float[] { 0.1f, 0.9f, 0f });
            }
        };
        engine.vget(testget, "vtest", 5);
        testget.waitForFinish();
        testget.validate();
    }

    @Test
    public void testbrev() {
        TestableCallback testbrev = new TestableCallback() {
            @Override
            public void excepted() {
                isOk();
            }
        };
        engine.brev(testbrev, "btest", new String[] { "B2", "B1", "B0" });
        testbrev.waitForFinish();
        testbrev.validate();
    }
}
