package fgfbrands.barcodescanners;

import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthSchemeFactory;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.impl.auth.NTLMScheme;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

/**
 * Created by psung on 4/9/2015.
 */
public class NTLMSchemeFactory implements AuthSchemeProvider {

    public AuthScheme create(final HttpContext context) {
        return new NTLMScheme(new JCIFSEngine());
    }
}
