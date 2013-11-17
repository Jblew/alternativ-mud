/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.tester;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import net.alternativmud.App;
import net.alternativmud.framework.Service;
import net.alternativmud.lib.DateFormatters;
import net.alternativmud.logic.time.TimeValue;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Tester będzie służył do testowania muda w trakcie działania. Będzie
 * raportował wyniki testów.
 *
 * @author jblew
 */
public class Tester implements Service {
    private final TimeValue testPeriod = new TimeValue(6, TimeUnit.HOURS);
    /*
     * LinkedList to prawie to samo, co ArrayList, z tym, że zależy do czego.
     * ArrayList jest szybszy do swobodnego dostępu, a LinkedList wymiata, jeśli
     * chodzi o przeglądanie listy po kolei.
     */
    private final LinkedList<RuntimeTest> tests = new LinkedList<>(); //w javie 7 nie trzeba podawać typów dwa razy. Wystarczy diamencik (<>). ;)
    private ScheduledExecutorService scheduledExecutor;

    public Tester() {
            scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    }
    
    public void start() {
        final Tester aThis = this;
        scheduledExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                aThis.test();
            }
        }, testPeriod.value, testPeriod.value, testPeriod.unit);
        
        App.getApp().getServiceManager().register(Tester.class, this);
    }
    
    public void stop() {
        App.getApp().getServiceManager().unregister(Tester.class);
        scheduledExecutor.shutdown();
        boolean finished = false;
        try {
            if(scheduledExecutor.awaitTermination(150, TimeUnit.MILLISECONDS)) {
                finished = true;
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(!finished) scheduledExecutor.shutdownNow();
        scheduledExecutor = null;
    }

    /**
     * Metoda test() uruchamia testy z listy, a następnie tworzy raporty z ich 
     * uruchomienia. Raporty są zapisywane do pliku .xml, wysyłane do systemowej
     * szyny wydarzeń (EventBus) a także zwracane bezpośrednio przez metodę.
     * @return listę raportów z testów
     */
    public Map<String, Tester.Report> test() {
        Map<String, Tester.Report> report = new HashMap<>();

        int numOfFailures = 0;
        int numOfTests = 0;
        List<RuntimeTest> tests_;
        synchronized (tests) {
            tests_ = tests.subList(0, tests.size());
        }
        for (RuntimeTest t : tests_) {
            try {
                numOfTests++;
                t.execute();
                report.put(t.getName(), new Report(t.getName(), t.getClass(), t, null));
            } catch (TestException e) {
                report.put(t.getName(), new Report(t.getName(), t.getClass(), t, e));
                numOfFailures++;
            } catch (Exception e) {
                report.put(t.getName(), new Report(t.getName(), t.getClass(), t, new TestException(t, e)));
                numOfFailures++;
            }
        }
        if (numOfFailures > 0) {
            String strReport = ""
                    + "------\n"
                    + "===Test report [" + new Date().toString() + "]===\n"
                    + "\n"
                    + "Tests:\n";

            for (String testName : report.keySet()) {
                Tester.Report r = report.get(testName);
                strReport += "[" + r.getTest().getName() + "] ";
                if (r.wasPassed()) {
                    strReport += "PASSED\n";
                } else {
                    if (r.getException().getCause() != null) {
                        strReport += "EXCEPTION: " + r.getException().getCause() + "\n";
                        strReport += "   Stack trace:\n";
                        for (StackTraceElement e : r.getException().getCause().getStackTrace()) {
                            strReport += "   " + e + "\n";
                        }
                    } else {
                        strReport += "ERROR: " + r.getErrorMessage() + "\n";
                    }
                }
            }

            strReport += "------\n";
            Logger.getLogger(Tester.class.getName()).warning(strReport);
        } else {
            Logger.getLogger(Tester.class.getName()).log(Level.INFO, "Executed {0} tests. {1} failures, {2} successful tests.", new Object[]{numOfTests, numOfFailures, numOfTests - numOfFailures});
        }

        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element root = doc.createElement("testReport");
            doc.appendChild(root);

            Element date = doc.createElement("date");
            date.setTextContent(new Date().toString());
            root.appendChild(date);

            Element numOfTestsElem = doc.createElement("numOfTests");
            numOfTestsElem.setTextContent(numOfTests + "");
            root.appendChild(numOfTestsElem);

            Element numOfFailuresElem = doc.createElement("numOfFailures");
            numOfFailuresElem.setTextContent(numOfFailures + "");
            root.appendChild(numOfFailuresElem);

            Element testsElem = doc.createElement("tests");

            for (String testName : report.keySet()) {
                Report r = report.get(testName);
                Element testElem = doc.createElement("test");

                Element nameElem = doc.createElement("name");
                nameElem.setTextContent(testName);
                testElem.appendChild(nameElem);

                Element statusElem = doc.createElement("status");
                if (r.wasPassed()) {
                    statusElem.setTextContent("passed");
                } else {
                    statusElem.setTextContent("failed");

                    Element messageElem = doc.createElement("message");
                    messageElem.setTextContent((r.getException().getCause() == null ? r.getErrorMessage() : "exception"));
                    testElem.appendChild(messageElem);
                    if (r.getException().getCause() != null) {
                        Element exceptionElem = doc.createElement("exception");

                        Element exceptionNameElem = doc.createElement("name");
                        exceptionNameElem.setTextContent(r.getException().getCause().getMessage());
                        exceptionElem.appendChild(exceptionNameElem);

                        Element stackTraceElem = doc.createElement("stackTrace");

                        for (StackTraceElement e : r.getException().getCause().getStackTrace()) {
                            Element stackTraceElemElem = doc.createElement("elem");
                            stackTraceElemElem.setTextContent(e + "");
                            stackTraceElem.appendChild(stackTraceElemElem);
                        }

                        exceptionElem.appendChild(stackTraceElem);

                        testElem.appendChild(exceptionElem);
                    }
                }
                testElem.appendChild(statusElem);

                testsElem.appendChild(testElem);
            }

            root.appendChild(testsElem);



            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");

            //create string from xml tree
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource(doc);
            trans.transform(source, result);
            String xmlString = sw.toString();

            String dateStr = DateFormatters.URLSAFE_DATE_FORMATTER.format(new Date());
            File f = new File("data/test-reports/" + dateStr + ".xml");
            if (!f.exists()) {
                f.createNewFile();
            }
            Files.write(xmlString, f, Charsets.UTF_8);
        } catch (IOException | TransformerException | ParserConfigurationException ex) {
            Logger.getLogger(Tester.class.getName()).log(Level.WARNING, null, ex);
        }

        App.getApp().getSystemEventBus().post(new TestsRunEvent(report));
        return report;
    }

    public synchronized void addTest(RuntimeTest t) {
        tests.add(t);
    }

    public synchronized void removeTest(RuntimeTest t) {
        tests.remove(t);
    }

    @Override
    public String getDescription() {
        return "Periodically runs tests.";
    }

    /**
     * Raport testu.
     */
    public static class Report {
        public final String name;
        public final Class<? extends RuntimeTest> testClass;
        private final TestException testException;
        private final RuntimeTest test;

        public Report(String name, Class<? extends RuntimeTest> cls, RuntimeTest test, TestException testException) {
            this.testException = testException;
            this.test = test;
            this.testClass = cls;
            this.name = name;
        }

        public boolean wasPassed() {
            return (testException == null);
        }

        public String getErrorMessage() {
            return (testException.getMessage() == null ? "[no exception]" : testException.getMessage());
        }

        public TestException getException() {
            return testException;
        }

        public RuntimeTest getTest() {
            return test;
        }
    }
    
    public static class TestsRunEvent {
        public final Map<String, Tester.Report> report;
        
        private TestsRunEvent(Map<String, Tester.Report> report) {
            this.report = report;
        }
    }
}
