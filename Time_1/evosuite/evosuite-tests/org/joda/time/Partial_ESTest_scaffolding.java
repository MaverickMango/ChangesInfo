/**
 * Scaffolding file used to store all the setups needed to run 
 * tests automatically generated by EvoSuite
 * Thu Aug 31 12:25:42 GMT 2023
 */

package org.joda.time;

import shaded.org.evosuite.runtime.annotation.EvoSuiteClassExclude;
import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.After;
import org.junit.AfterClass;
import shaded.org.evosuite.runtime.sandbox.Sandbox;
import shaded.org.evosuite.runtime.sandbox.Sandbox.SandboxMode;

import static shaded.org.evosuite.shaded.org.mockito.Mockito.*;
@EvoSuiteClassExclude
public class Partial_ESTest_scaffolding {

  @org.junit.Rule
  public shaded.org.evosuite.runtime.vnet.NonFunctionalRequirementRule nfr = new shaded.org.evosuite.runtime.vnet.NonFunctionalRequirementRule();

  private static final java.util.Properties defaultProperties = (java.util.Properties) java.lang.System.getProperties().clone(); 

  private shaded.org.evosuite.runtime.thread.ThreadStopper threadStopper =  new shaded.org.evosuite.runtime.thread.ThreadStopper (shaded.org.evosuite.runtime.thread.KillSwitchHandler.getInstance(), 3000);


  @BeforeClass
  public static void initEvoSuiteFramework() { 
    shaded.org.evosuite.runtime.RuntimeSettings.className = "org.joda.time.Partial"; 
    shaded.org.evosuite.runtime.GuiSupport.initialize(); 
    shaded.org.evosuite.runtime.RuntimeSettings.maxNumberOfThreads = 100; 
    shaded.org.evosuite.runtime.RuntimeSettings.maxNumberOfIterationsPerLoop = 10000; 
    shaded.org.evosuite.runtime.RuntimeSettings.mockSystemIn = true; 
    shaded.org.evosuite.runtime.RuntimeSettings.sandboxMode = shaded.org.evosuite.runtime.sandbox.Sandbox.SandboxMode.RECOMMENDED; 
    shaded.org.evosuite.runtime.sandbox.Sandbox.initializeSecurityManagerForSUT(); 
    shaded.org.evosuite.runtime.classhandling.JDKClassResetter.init();
    setSystemProperties();
    initializeClasses();
    shaded.org.evosuite.runtime.Runtime.getInstance().resetRuntime(); 
    try { initMocksToAvoidTimeoutsInTheTests(); } catch(ClassNotFoundException e) {} 
  } 

  @AfterClass
  public static void clearEvoSuiteFramework(){ 
    Sandbox.resetDefaultSecurityManager(); 
    java.lang.System.setProperties((java.util.Properties) defaultProperties.clone()); 
  } 

  @Before
  public void initTestCase(){ 
    threadStopper.storeCurrentThreads();
    threadStopper.startRecordingTime();
    shaded.org.evosuite.runtime.jvm.ShutdownHookHandler.getInstance().initHandler(); 
    shaded.org.evosuite.runtime.sandbox.Sandbox.goingToExecuteSUTCode(); 
    setSystemProperties(); 
    shaded.org.evosuite.runtime.GuiSupport.setHeadless(); 
    shaded.org.evosuite.runtime.Runtime.getInstance().resetRuntime(); 
    shaded.org.evosuite.runtime.agent.InstrumentingAgent.activate(); 
  } 

  @After
  public void doneWithTestCase(){ 
    threadStopper.killAndJoinClientThreads();
    shaded.org.evosuite.runtime.jvm.ShutdownHookHandler.getInstance().safeExecuteAddedHooks(); 
    shaded.org.evosuite.runtime.classhandling.JDKClassResetter.reset(); 
    resetClasses(); 
    shaded.org.evosuite.runtime.sandbox.Sandbox.doneWithExecutingSUTCode(); 
    shaded.org.evosuite.runtime.agent.InstrumentingAgent.deactivate(); 
    shaded.org.evosuite.runtime.GuiSupport.restoreHeadlessMode(); 
  } 

  public static void setSystemProperties() {
 
    java.lang.System.setProperties((java.util.Properties) defaultProperties.clone()); 
    java.lang.System.setProperty("user.timezone", "America/Los_Angeles"); 
    java.lang.System.setProperty("user.dir", "/home/liumengjiao/Desktop/CI/bugs/Time_1_buggy"); 
    java.lang.System.setProperty("java.io.tmpdir", "/tmp"); 
  }

  private static void initializeClasses() {
    shaded.org.evosuite.runtime.classhandling.ClassStateSupport.initializeClasses(Partial_ESTest_scaffolding.class.getClassLoader() ,
      "org.joda.time.DateTimeZone",
      "org.joda.time.tz.DateTimeZoneBuilder$Recurrence",
      "org.joda.time.DateTimeUtils$MillisProvider",
      "org.joda.time.chrono.GJYearOfEraDateTimeField",
      "org.joda.time.field.OffsetDateTimeField",
      "org.joda.time.chrono.GJMonthOfYearDateTimeField",
      "org.joda.time.field.RemainderDateTimeField",
      "org.joda.time.JodaTimePermission",
      "org.joda.time.chrono.BasicWeekyearDateTimeField",
      "org.joda.time.field.AbstractPartialFieldProperty",
      "org.joda.time.chrono.BasicWeekOfWeekyearDateTimeField",
      "org.joda.time.DateTimeFieldType",
      "org.joda.time.DateTimeField",
      "org.joda.time.field.FieldUtils",
      "org.joda.time.DateTimeFieldType$StandardDateTimeFieldType",
      "org.joda.time.Partial",
      "org.joda.time.ReadableInterval",
      "org.joda.time.chrono.BasicChronology$HalfdayField",
      "org.joda.time.ReadableInstant",
      "org.joda.time.DateTimeUtils$SystemMillisProvider",
      "org.joda.time.chrono.GJDayOfWeekDateTimeField",
      "org.joda.time.IllegalInstantException",
      "org.joda.time.IllegalFieldValueException",
      "org.joda.time.tz.DateTimeZoneBuilder$PrecalculatedZone",
      "org.joda.time.chrono.BasicChronology$YearInfo",
      "org.joda.time.field.UnsupportedDurationField",
      "org.joda.time.tz.DefaultNameProvider",
      "org.joda.time.tz.Provider",
      "org.joda.time.field.ImpreciseDateTimeField$LinkedDurationField",
      "org.joda.time.ReadablePeriod",
      "org.joda.time.base.AbstractDateTime",
      "org.joda.time.chrono.ZonedChronology$ZonedDateTimeField",
      "org.joda.time.chrono.GregorianChronology",
      "org.joda.time.chrono.AssembledChronology$Fields",
      "org.joda.time.DurationFieldType",
      "org.joda.time.chrono.ISOChronology",
      "org.joda.time.base.BaseLocal",
      "org.joda.time.tz.NameProvider",
      "org.joda.time.chrono.BasicChronology",
      "org.joda.time.chrono.BasicYearDateTimeField",
      "org.joda.time.field.DividedDateTimeField",
      "org.joda.time.chrono.ZonedChronology",
      "org.joda.time.field.BaseDateTimeField",
      "org.joda.time.field.ZeroIsMaxDateTimeField",
      "org.joda.time.chrono.BasicMonthOfYearDateTimeField",
      "org.joda.time.base.AbstractPartial",
      "org.joda.time.DateTimeUtils",
      "org.joda.time.base.BaseDateTime",
      "org.joda.time.tz.CachedDateTimeZone$Info",
      "org.joda.time.field.MillisDurationField",
      "org.joda.time.field.DecoratedDurationField",
      "org.joda.time.tz.DateTimeZoneBuilder$DSTZone",
      "org.joda.time.chrono.AssembledChronology",
      "org.joda.time.base.AbstractInstant",
      "org.joda.time.tz.ZoneInfoProvider",
      "org.joda.time.chrono.GJEraDateTimeField",
      "org.joda.time.DateTimeZone$1",
      "org.joda.time.tz.DateTimeZoneBuilder",
      "org.joda.time.chrono.BaseChronology",
      "org.joda.time.Partial$Property",
      "org.joda.time.field.UnsupportedDateTimeField",
      "org.joda.time.field.ImpreciseDateTimeField",
      "org.joda.time.field.PreciseDurationField",
      "org.joda.time.tz.DateTimeZoneBuilder$OfYear",
      "org.joda.time.chrono.BasicGJChronology",
      "org.joda.time.field.ScaledDurationField",
      "org.joda.time.chrono.ISOYearOfEraDateTimeField",
      "org.joda.time.DurationField",
      "org.joda.time.format.DateTimeFormatter",
      "org.joda.time.Chronology",
      "org.joda.time.DateTime",
      "org.joda.time.field.PreciseDurationDateTimeField",
      "org.joda.time.LocalDateTime",
      "org.joda.time.tz.FixedDateTimeZone",
      "org.joda.time.tz.CachedDateTimeZone",
      "org.joda.time.field.PreciseDateTimeField",
      "org.joda.time.ReadableDateTime",
      "org.joda.time.chrono.BasicDayOfMonthDateTimeField",
      "org.joda.time.chrono.ZonedChronology$ZonedDurationField",
      "org.joda.time.ReadablePartial",
      "org.joda.time.chrono.BasicDayOfYearDateTimeField",
      "org.joda.time.DurationFieldType$StandardDurationFieldType",
      "org.joda.time.field.DecoratedDateTimeField",
      "org.joda.time.field.BaseDurationField"
    );
  } 
  private static void initMocksToAvoidTimeoutsInTheTests() throws ClassNotFoundException { 
    mock(Class.forName("org.joda.time.Chronology", false, Partial_ESTest_scaffolding.class.getClassLoader()));
    mock(Class.forName("org.joda.time.DateTimeFieldType", false, Partial_ESTest_scaffolding.class.getClassLoader()));
  }

  private static void resetClasses() {
    shaded.org.evosuite.runtime.classhandling.ClassResetter.getInstance().setClassLoader(Partial_ESTest_scaffolding.class.getClassLoader()); 

    shaded.org.evosuite.runtime.classhandling.ClassStateSupport.resetClasses(
      "org.joda.time.base.AbstractPartial",
      "org.joda.time.Partial",
      "org.joda.time.field.AbstractPartialFieldProperty",
      "org.joda.time.Partial$Property",
      "org.joda.time.DateTimeUtils$SystemMillisProvider",
      "org.joda.time.tz.FixedDateTimeZone",
      "org.joda.time.tz.ZoneInfoProvider",
      "org.joda.time.tz.DefaultNameProvider",
      "org.joda.time.DateTimeZone",
      "org.joda.time.tz.DateTimeZoneBuilder",
      "org.joda.time.tz.DateTimeZoneBuilder$PrecalculatedZone",
      "org.joda.time.tz.DateTimeZoneBuilder$DSTZone",
      "org.joda.time.tz.DateTimeZoneBuilder$Recurrence",
      "org.joda.time.tz.DateTimeZoneBuilder$OfYear",
      "org.joda.time.tz.CachedDateTimeZone",
      "org.joda.time.DateTimeUtils",
      "org.joda.time.format.FormatUtils",
      "org.joda.time.Chronology",
      "org.joda.time.chrono.BaseChronology",
      "org.joda.time.chrono.AssembledChronology",
      "org.joda.time.DurationField",
      "org.joda.time.field.MillisDurationField",
      "org.joda.time.field.BaseDurationField",
      "org.joda.time.field.PreciseDurationField",
      "org.joda.time.DurationFieldType$StandardDurationFieldType",
      "org.joda.time.DurationFieldType",
      "org.joda.time.DateTimeField",
      "org.joda.time.field.BaseDateTimeField",
      "org.joda.time.field.PreciseDurationDateTimeField",
      "org.joda.time.field.PreciseDateTimeField",
      "org.joda.time.DateTimeFieldType$StandardDateTimeFieldType",
      "org.joda.time.DateTimeFieldType",
      "org.joda.time.field.DecoratedDateTimeField",
      "org.joda.time.field.ZeroIsMaxDateTimeField",
      "org.joda.time.chrono.BasicChronology$HalfdayField",
      "org.joda.time.chrono.BasicChronology",
      "org.joda.time.chrono.BasicGJChronology",
      "org.joda.time.chrono.AssembledChronology$Fields",
      "org.joda.time.field.ImpreciseDateTimeField",
      "org.joda.time.chrono.BasicYearDateTimeField",
      "org.joda.time.field.ImpreciseDateTimeField$LinkedDurationField",
      "org.joda.time.chrono.GJYearOfEraDateTimeField",
      "org.joda.time.field.OffsetDateTimeField",
      "org.joda.time.field.DividedDateTimeField",
      "org.joda.time.field.DecoratedDurationField",
      "org.joda.time.field.ScaledDurationField",
      "org.joda.time.field.RemainderDateTimeField",
      "org.joda.time.chrono.GJEraDateTimeField",
      "org.joda.time.chrono.GJDayOfWeekDateTimeField",
      "org.joda.time.chrono.BasicDayOfMonthDateTimeField",
      "org.joda.time.chrono.BasicDayOfYearDateTimeField",
      "org.joda.time.chrono.BasicMonthOfYearDateTimeField",
      "org.joda.time.chrono.GJMonthOfYearDateTimeField",
      "org.joda.time.chrono.BasicWeekyearDateTimeField",
      "org.joda.time.chrono.BasicWeekOfWeekyearDateTimeField",
      "org.joda.time.field.UnsupportedDurationField",
      "org.joda.time.chrono.GregorianChronology",
      "org.joda.time.chrono.ISOYearOfEraDateTimeField",
      "org.joda.time.chrono.ISOChronology",
      "org.joda.time.format.DateTimeFormatterBuilder",
      "org.joda.time.format.DateTimeFormatterBuilder$NumberFormatter",
      "org.joda.time.format.DateTimeFormatterBuilder$PaddedNumber",
      "org.joda.time.format.DateTimeFormatter",
      "org.joda.time.format.DateTimeFormatterBuilder$CharacterLiteral",
      "org.joda.time.format.DateTimeFormatterBuilder$Composite",
      "org.joda.time.format.DateTimeFormatterBuilder$StringLiteral",
      "org.joda.time.format.DateTimeFormatterBuilder$UnpaddedNumber",
      "org.joda.time.format.DateTimeFormatterBuilder$Fraction",
      "org.joda.time.format.DateTimeFormatterBuilder$TimeZoneOffset",
      "org.joda.time.format.ISODateTimeFormat",
      "org.joda.time.format.DateTimeFormatterBuilder$FixedNumber",
      "org.joda.time.format.DateTimeFormatterBuilder$MatchingParser",
      "org.joda.time.format.ISODateTimeFormat$Constants",
      "org.joda.time.format.DateTimeFormat$1",
      "org.joda.time.format.DateTimeFormat",
      "org.joda.time.JodaTimePermission",
      "org.joda.time.chrono.ZonedChronology",
      "org.joda.time.chrono.ZonedChronology$ZonedDurationField",
      "org.joda.time.chrono.ZonedChronology$ZonedDateTimeField",
      "org.joda.time.base.BaseLocal",
      "org.joda.time.LocalDate",
      "org.joda.time.convert.ConverterManager",
      "org.joda.time.convert.ConverterSet",
      "org.joda.time.convert.AbstractConverter",
      "org.joda.time.convert.ReadableInstantConverter",
      "org.joda.time.convert.StringConverter",
      "org.joda.time.convert.CalendarConverter",
      "org.joda.time.convert.DateConverter",
      "org.joda.time.convert.LongConverter",
      "org.joda.time.convert.NullConverter",
      "org.joda.time.convert.ReadablePartialConverter",
      "org.joda.time.convert.ReadableDurationConverter",
      "org.joda.time.convert.ReadableIntervalConverter",
      "org.joda.time.convert.ReadablePeriodConverter",
      "org.joda.time.convert.ConverterSet$Entry",
      "org.joda.time.LocalDateTime",
      "org.joda.time.chrono.BasicChronology$YearInfo",
      "org.joda.time.field.FieldUtils",
      "org.joda.time.IllegalFieldValueException",
      "org.joda.time.DateTimeUtils$FixedMillisProvider",
      "org.joda.time.DateTimeUtils$OffsetMillisProvider",
      "org.joda.time.tz.CachedDateTimeZone$Info",
      "org.joda.time.base.BasePartial",
      "org.joda.time.MonthDay"
    );
  }
}
