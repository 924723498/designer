package com.fr.design.mainframe.templateinfo;

import com.fr.invoke.Reflect;
import com.fr.stable.StringUtils;
import junit.framework.TestCase;

import java.lang.reflect.Method;

/**
 * Created by XINZAI on 2018/8/7.
 */
public class ParseVersionTest extends TestCase {
    public void testParseVersion() throws Exception{
        assertEquals("10.0.0", parseVersion("KAA"));
        assertEquals("9.0.0", parseVersion("JAA"));
        assertEquals("8.0.0", parseVersion("IAA"));
        assertEquals("8.1.2", parseVersion("IBC"));
    }

    private String parseVersion(String xmlDesignerVersion) throws Exception{
        String version = StringUtils.EMPTY;
        try {
            version = Reflect.on("com.fr.design.mainframe.JTemplate").call("parseVersion", xmlDesignerVersion).get();

        }catch (Exception e){
            throw e;
        }

        return version;
    }

}

