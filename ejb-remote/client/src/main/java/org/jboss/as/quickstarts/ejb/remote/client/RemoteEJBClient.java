/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.ejb.remote.client;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.jboss.as.quickstarts.ejb.remote.stateless.RemoteCalculator;

/**
 * A sample program which acts a remote client for a EJB deployed on JBoss EAP server. This program shows how to lookup stateful and
 * stateless beans via JNDI and then invoke on them
 *
 * @author Jaikiran Pai
 */
public class RemoteEJBClient {


	private static final String REMOTE_HTTP_ENDPOINT = "remote+http://mysample-rmi-test.1d35.starter-us-east-1.openshiftapps.com:80";
	//private static final String REMOTE_HTTP_ENDPOINT = "remote+http://test5-rmi-test.1d35.starter-us-east-1.openshiftapps.com:80";
    private static final String REMOTE_SSL_HOST = "ssl-mysample-rmi-test.1d35.starter-us-east-1.openshiftapps.com";

    private static final String REMOTE_TLS_ENDPOINT = "remote://"+REMOTE_SSL_HOST+":443";

	public static void main(String[] args) throws Exception {
        // Invoke a stateless bean
        invokeStatelessBean();
    }

    /**
     * Looks up a stateless bean and invokes on it
     *
     * @throws NamingException
     */
    private static void invokeStatelessBean() throws NamingException {
        // Let's lookup the remote stateless calculator
        final RemoteCalculator statelessRemoteCalculator = lookupRemoteStatelessCalculator();
        System.out.println("Obtained a remote stateless calculator for invocation");
        // invoke on the remote calculator
        int a = 204;
        int b = 340;
        System.out.println("Adding " + a + " and " + b + " via the remote stateless calculator deployed on the server");
        int sum = statelessRemoteCalculator.add(a, b);
        System.out.println("Remote calculator returned sum = " + sum);
        if (sum != a + b) {
            throw new RuntimeException("Remote stateless calculator returned an incorrect sum " + sum + " ,expected sum was "
                + (a + b));
        }
        // try one more invocation, this time for subtraction
        int num1 = 3434;
        int num2 = 2332;
        System.out.println("Subtracting " + num2 + " from " + num1
            + " via the remote stateless calculator deployed on the server");
        int difference = statelessRemoteCalculator.subtract(num1, num2);
        System.out.println("Remote calculator returned difference = " + difference);
        if (difference != num1 - num2) {
            throw new RuntimeException("Remote stateless calculator returned an incorrect difference " + difference
                + " ,expected difference was " + (num1 - num2));
        }
    }

    /**
     * Looks up and returns the proxy to remote stateless calculator bean
     *
     * @return
     * @throws NamingException
     */
    private static RemoteCalculator lookupRemoteStatelessCalculatorOverSSL() throws NamingException {
        final Hashtable<String, String> jndiProperties = new Hashtable<>();
        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        //jndiProperties.put(Context.PROVIDER_URL,REMOTE_HTTPS_ENDPOINT);
        jndiProperties.put(Context.SECURITY_PRINCIPAL, "user1");
        jndiProperties.put(Context.SECURITY_CREDENTIALS, "password1");
     
        jndiProperties.put("remote.connections", "default");
        jndiProperties.put("remote.connection.default.host", REMOTE_SSL_HOST);
        jndiProperties.put("remote.connection.default.port", "443");
        jndiProperties.put(
				"remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOPLAINTEXT",
				"false");
        
        jndiProperties.put(
				"remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS",
				"false");
      
        jndiProperties.put(
				"remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED",
				"true");
        jndiProperties.put(
				"remote.connection.default.connect.options.org.xnio.Options.SSL_ENABLED",
				"true");
        jndiProperties.put(
				"remote.connection.default.connect.options.org.xnio.Options.SSL_STARTTLS",
				"true");
        final Context context = new InitialContext(jndiProperties);

        // let's do the lookup
        return (RemoteCalculator) context.lookup("ejb:/ejb-remote-server-side/CalculatorBean!"
            + RemoteCalculator.class.getName());
    }
    
    /**
     * Looks up and returns the proxy to remote stateless calculator bean
     *
     * @return
     * @throws NamingException
     */
    private static RemoteCalculator lookupRemoteStatelessCalculator() throws NamingException {
        final Hashtable<String, String> jndiProperties = new Hashtable<>();
        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        jndiProperties.put(Context.PROVIDER_URL,REMOTE_HTTP_ENDPOINT);
        jndiProperties.put(Context.SECURITY_PRINCIPAL, "user1");
        jndiProperties.put(Context.SECURITY_CREDENTIALS, "password1");
  
        final Context context = new InitialContext(jndiProperties);

        // let's do the lookup
        return (RemoteCalculator) context.lookup("ejb:/ejb-remote-server-side/CalculatorBean!"
            + RemoteCalculator.class.getName());
    }
    
}
