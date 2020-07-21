import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class ParseSringIntoArrayTest {
  private static final String content = "This is a test1;This is a test2;This is a test3;";
  private static final String sentence1 = "This is a test1";
  private static final String sentence2 = "This is a test2";
  private static final String sentence3 = "This is a test3";
  private static final String[] dialogue = {sentence1, sentence2, sentence3};

  @Test
  public void ParseAnArray_Successfully() {
    String line = content;
    String[] sentences = line.split(";");

    for (int i = 0; i < sentences.length; i++) {
      Assert.assertEquals(sentences[i], dialogue[i]);
    }
  }
}
