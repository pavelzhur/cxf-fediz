/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.cxf.fediz.core.servlet;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Element;
import org.apache.cxf.fediz.core.FedizPrincipal;
import org.apache.cxf.fediz.core.SecurityTokenThreadLocal;



/**
 * Add security token to thread local
 */
public class FederationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        if (request instanceof HttpServletRequest) {
            HttpServletRequest hrequest = (HttpServletRequest)request;
            Principal p = hrequest.getUserPrincipal();
            FedizPrincipal fedPrinc = (FedizPrincipal)p;
            if (fedPrinc != null && fedPrinc.getLoginToken() != null) {
                Element el = (Element)fedPrinc.getLoginToken();
                try {
                    SecurityTokenThreadLocal.setToken(el);
                    chain.doFilter(request, response);
                } finally {
                    SecurityTokenThreadLocal.setToken(null);
                }
            } else {
                chain.doFilter(request, response);
            }

        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }

}
