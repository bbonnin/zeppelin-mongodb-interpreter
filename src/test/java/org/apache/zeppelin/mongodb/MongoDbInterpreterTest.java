package org.apache.zeppelin.mongodb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.junit.Test;

public class MongoDbInterpreterTest {

  @Test
  public void testCommandine() {
    final CommandLine cmdLine = CommandLine.parse("mongo");
    cmdLine.addArgument("--quiet", false);
    cmdLine.addArgument("--host", false);
    cmdLine.addArgument("localhost", false);
    cmdLine.addArgument("--port", false);
    cmdLine.addArgument("28100", false);
    cmdLine.addArgument("/tmp/zeppelin-mongo-test.js");
    final DefaultExecutor executor = new DefaultExecutor();
    final ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
    executor.setStreamHandler(new PumpStreamHandler(System.out, errorStream));
    try {
      int exitVal = executor.execute(cmdLine);
      System.out.println(exitVal);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

}
