/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.mongodb;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.zeppelin.interpreter.InterpreterContext;
import org.apache.zeppelin.interpreter.InterpreterOutput;
import org.apache.zeppelin.interpreter.InterpreterOutputListener;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.apache.zeppelin.interpreter.InterpreterResult.Code;
import org.apache.zeppelin.interpreter.InterpreterResultMessageOutput;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * As there is no 'mongo' on the build platform, these tests simulates some basic behavior.
 *
 */
public class MongoDbInterpreterTest implements InterpreterOutputListener {
  
  private static final String SHELL_EXTENSION =
    new Scanner(MongoDbInterpreter.class.getResourceAsStream("/shell_extension.js"), "UTF-8")
      .useDelimiter("\\A").next();
  
  private static final boolean IS_WINDOWS = System.getProperty("os.name")
    .startsWith("Windows");
  
  private static final String MONGO_SHELL = System.getProperty("java.io.tmpdir") + 
    File.separator + "mongo-test." + (IS_WINDOWS ? "bat" : "sh");
    
  private final Properties props = new Properties();
  private final MongoDbInterpreter interpreter = new MongoDbInterpreter(props);
  private final InterpreterOutput out = new InterpreterOutput(this);
  private final InterpreterContext context = new InterpreterContext("test", "test", 
    null, null, null, null, null, null, null, null, null, out);
  
  private ByteBuffer buffer;
  
  @BeforeClass
  public static void setup() {
    // Create a fake 'mongo'
    final File mongoFile = new File(MONGO_SHELL);
    try {
      FileUtils.write(mongoFile, (IS_WINDOWS ? "@echo off\ntype \"%2%\"" : "cat \"$2\""));
      FileUtils.forceDeleteOnExit(mongoFile);
    }
    catch (IOException e) {
    }
  }
  
  @Before
  public void init() {
    buffer = ByteBuffer.allocate(10000);
    props.put("mongo.shell.path", (IS_WINDOWS ? "" : "sh ") + MONGO_SHELL);
    props.put("mongo.shell.command.table.limit", "10000");
  }

  @Test
  public void testSuccess() {
    final String userScript = "print('hello')";
    
    final InterpreterResult res = interpreter.interpret(userScript, context);
    
    assertTrue("Check SUCCESS: " + res.message(), res.code() == Code.SUCCESS);
    
    try {
      out.flush();
    } catch (IOException e) {}
    
    
    final String resultScript = new String(getBufferBytes());
    
    final String expectedScript = SHELL_EXTENSION.replace(
      "__ZEPPELIN_TABLE_LIMIT__", interpreter.getProperty("mongo.shell.command.table.limit")) + 
      userScript;
    
    // The script that is executed must contain the functions provided by this interpreter
    assertTrue("Check SCRIPT", resultScript.equals(expectedScript));
  }
  
  @Test
  public void testBadConf() {
    props.setProperty("mongo.shell.path", "/bad/path/to/mongo");
    final InterpreterResult res = interpreter.interpret("print('hello')", context);
    
    assertTrue(res.code() == Code.ERROR);
  }

  @Override
  public void onUpdateAll(InterpreterOutput interpreterOutput) {

  }

  @Override
  public void onAppend(int i, InterpreterResultMessageOutput interpreterResultMessageOutput, byte[] bytes) {
    buffer.put(bytes);
  }

  @Override
  public void onUpdate(int i, InterpreterResultMessageOutput interpreterResultMessageOutput) {

  }

  private byte[] getBufferBytes() {
    buffer.flip();
    final byte[] bufferBytes = new byte[buffer.remaining()];
    buffer.get(bufferBytes);
    return bufferBytes;
  }

}
