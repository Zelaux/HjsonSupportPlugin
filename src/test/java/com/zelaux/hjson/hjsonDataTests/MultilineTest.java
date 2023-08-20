package com.zelaux.hjson.hjsonDataTests;

import com.zelaux.hjson.AbstractHJsonDataTest;
import org.junit.Test;

import java.io.IOException;

public class MultilineTest extends AbstractHJsonDataTest {
    public MultilineTest() {
        super("");
    }
    @Test
    public void test() throws IOException {
        checkAll("multiline.hjson");
    }
}
