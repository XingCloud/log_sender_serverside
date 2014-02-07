package com.xingcloud.sdk.serverside;

import com.xingcloud.sdk.serverside.json.JsonReaderWriter;
import com.xingcloud.sdk.serverside.model.HttpRequestEntityGroup;
import com.xingcloud.sdk.serverside.model.RowDescriptor;
import com.xingcloud.sdk.serverside.sender.SyncHttpLogSender;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * User: Z J Wu Date: 14-1-26 Time: 上午10:12 Package: com.xingcloud.sdk.serverside
 */
public class MainRun {

  private static final Logger LOGGER = Logger.getLogger(MainRun.class);

  public static void main(String[] args) throws InterruptedException {
    CommandLineParser parser = new BasicParser();
    Options options = new Options();

    OptionBuilder.withArgName("file to path");
    OptionBuilder.hasArg();
    OptionBuilder.isRequired();
    OptionBuilder.withLongOpt("in");
    OptionBuilder.withDescription("Input files separated by white space.");
    options.addOption(OptionBuilder.create('i'));

    OptionBuilder.withArgName("number of thread");
    OptionBuilder.hasArg();
    OptionBuilder.withLongOpt("num");
    OptionBuilder.withDescription("Number of sender-thread count.");
    options.addOption(OptionBuilder.create('n'));

    options.addOption("h", "help", false, "Print help.");
    CommandLine commandLine = null;
    try {
      commandLine = parser.parse(options, args);
    } catch (ParseException e) {
      LOGGER.error("Cannot parse args.");
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("Log sender server side", options);
      System.exit(1);
    }

    int senderThread = 10;
    String senderThreadString;
    if (commandLine.hasOption('n')) {
      senderThreadString = commandLine.getOptionValue('n');
      try {
        senderThread = Integer.valueOf(senderThreadString);
      } catch (Exception e) {
        LOGGER.error("Cannot parse thread count(" + senderThreadString + ").");
        System.exit(1);
      }
    }
    LOGGER.info("SenderThreadCount=" + senderThread);

    String[] files = null;
    if (commandLine.hasOption('i')) {
      files = commandLine.getOptionValues('i');
    }
    if (ArrayUtils.isEmpty(files)) {
      LOGGER.error("There is no any description file.");
      System.exit(1);
    }
    for (int i = 0; i < files.length; i++) {
      LOGGER.info("Descriptor file " + (i + 1) + "=" + files[i]);
    }

    List<RowDescriptor> rds = new ArrayList<>(files.length);
    LinkedBlockingQueue<HttpRequestEntityGroup> queue = new LinkedBlockingQueue<>();
    for (int i = 0; i < files.length; i++) {
      try {
        rds.add(JsonReaderWriter.parseAndInit(files[i], queue));
      } catch (LogSenderException e) {
        LOGGER.error("Cannot parse description file(" + files[i] + ")", e);
      }
    }
    if (CollectionUtils.isEmpty(rds)) {
      LOGGER.error("No valid descriptor created.");
      System.exit(1);
    }
    for (int i = 0; i < senderThread; i++) {
      new Thread(new SyncHttpLogSender(queue), "LogSenderThread-" + (i + 1)).start();
    }
    CountDownLatch signal = new CountDownLatch(rds.size());
    for (RowDescriptor rd : rds) {
      rd.setSignal(signal);
      new Thread(rd, "Thread-LogFetcher(" + rd.getProjectId() + "." + rd.getName() + ")").start();
    }
    signal.await();
    LOGGER.info("Log sender shut down.");
  }

}
