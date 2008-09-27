/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 */
package com.xpn.xwiki.web;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

/**
 * <p>
 * Action for serving javascript skin extensions.
 * </p>
 * 
 * @version $Id$
 * @since 1.4M2
 */
public class JsxAction extends XWikiAction
{
    enum CachePolicies
    {
        LONG, SHORT, DEFAULT, FORBID
    }

    protected static final long LONG_CACHE_DURATION = 30 * 24 * 3600 * 1000L;

    protected static final long SHORT_CACHE_DURATION = 1 * 24 * 3600 * 1000L;

    /** Logging helper. */
    private static final Log LOG = LogFactory.getLog(JsxAction.class);

    public static final String JSX_CLASS_NAME = "XWiki.JavaScriptExtension";

    /**
     * {@inheritDoc}
     * 
     * @see XWikiAction#render(XWikiContext)
     */
    @Override
    public String render(XWikiContext context) throws XWikiException
    {
        XWiki xwiki = context.getWiki();
        XWikiRequest request = context.getRequest();
        XWikiResponse response = context.getResponse();
        XWikiDocument doc = context.getDoc();

        if (doc.isNew()) {
            response.setStatus(404);
            return "docdoesnotexist";
        }

        CachePolicies finalCache = CachePolicies.LONG;
        StringBuilder resultBuilder = new StringBuilder();

        if (doc.getObjects(JSX_CLASS_NAME) != null) {
            for (BaseObject sxObj : doc.getObjects(JSX_CLASS_NAME)) {
                String sxContent = sxObj.getLargeStringValue("code");
                int parse = sxObj.getIntValue("parse");
                try {
                    CachePolicies cache =
                        CachePolicies.valueOf(StringUtils.upperCase(StringUtils.defaultIfEmpty(sxObj
                            .getStringValue("cache"), "LONG")));
                    if (cache.compareTo(finalCache) > 0) {
                        finalCache = cache;
                    }
                } catch (Exception ex) {
                    LOG.warn(String.format("JSX object [%s#%s] has an invalid cache policy: [%s]", doc.getFullName(),
                        sxObj.getStringValue("name"), sxObj.getStringValue("cache")));
                }

                if (parse == 1) {
                    sxContent = xwiki.getRenderingEngine().interpretText(sxContent, doc, context);
                }
                // Also add a newline, in case the different object contents don't end with a blank
                // line, and could cause syntax errors when concatenated.
                resultBuilder.append(sxContent + "\n");
            }
        }

        String result = resultBuilder.toString();

        response.setContentType("text/javascript");
        response.setDateHeader("Last-Modified", doc.getDate().getTime());
        response.setHeader("Cache-Control", "public");

        if (finalCache == CachePolicies.LONG) {
            // Cache for one month (30 days)
            response.setDateHeader("Expires", (new Date()).getTime() + LONG_CACHE_DURATION);
        } else if (finalCache == CachePolicies.SHORT) {
            // Cache for one day
            response.setDateHeader("Expires", (new Date()).getTime() + SHORT_CACHE_DURATION);
        } else if (finalCache == CachePolicies.FORBID) {
            response.setHeader("Cache-Control", "no-cache");
        }
        if (BooleanUtils.toBoolean(StringUtils.defaultIfEmpty(request.get("minify"), "true"))) {
            try {
                JavaScriptCompressor compressor =
                    new JavaScriptCompressor(new StringReader(result), new ErrorReporter()
                    {
                        public void error(String arg0, String arg1, int arg2, String arg3, int arg4)
                        {
                            LOG.warn("Error minimizing JSX object");
                        }

                        public EvaluatorException runtimeError(String arg0, String arg1, int arg2, String arg3, int arg4)
                        {
                            // TODO Auto-generated method stub
                            return null;
                        }

                        public void warning(String arg0, String arg1, int arg2, String arg3, int arg4)
                        {
                            // TODO Auto-generated method stub
                        }

                    });
                StringWriter out = new StringWriter();
                compressor.compress(out, -1, true, false, false, false);
                result = out.toString();
            } catch (EvaluatorException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
        }
        try {
            response.setContentLength(result.getBytes("UTF-8").length);
            response.getOutputStream().write(result.getBytes("UTF-8"));
        } catch (IOException ex) {
            LOG.warn("Failed to send JSX content: " + ex.getMessage());
        }
        return null;
    }
}
