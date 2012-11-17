/*
 * MUSTARD: Android's Client for StatusNet
 * 
 * Copyright (C) 2009-2010 macno.org, Michele Azzolari
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */


package org.mumod.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.HostNameResolver;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;
import org.apache.http.conn.ssl.StrictHostnameVerifier;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class UntrustedSSLSocketFactory implements LayeredSocketFactory {
	
    public static final String TLS   = "TLS";
    public static final String SSL   = "SSL";
    public static final String SSLV2 = "SSLv2";
    
    public static final X509HostnameVerifier ALLOW_ALL_HOSTNAME_VERIFIER 
        = new AllowAllHostnameVerifier();
    
    public static final X509HostnameVerifier BROWSER_COMPATIBLE_HOSTNAME_VERIFIER 
        = new BrowserCompatHostnameVerifier();
    
    public static final X509HostnameVerifier STRICT_HOSTNAME_VERIFIER 
        = new StrictHostnameVerifier();
    
    // volatile is needed to guarantee thread-safety of the setter/getter methods under all usage scenarios
    private volatile X509HostnameVerifier hostnameVerifier = ALLOW_ALL_HOSTNAME_VERIFIER;
    
    @SuppressWarnings("unused")
	private final SSLContext sslcontext;
    private final javax.net.ssl.SSLSocketFactory socketfactory;
    private final HostNameResolver nameResolver;
    
    private static final UntrustedSSLSocketFactory DEFAULT_FACTORY = new UntrustedSSLSocketFactory();
    
    /**
     * Gets the default factory, which uses the default JVM settings for secure 
     * connections.
     * 
     * @return the default factory
     */
    public static UntrustedSSLSocketFactory getSocketFactory() {
        return DEFAULT_FACTORY;
    }

    // non-javadoc, see interface org.apache.http.conn.SocketFactory
    @SuppressWarnings("cast")
    public Socket createSocket()
        throws IOException {

        // the cast makes sure that the factory is working as expected
        return (SSLSocket) this.socketfactory.createSocket();
    }

    /**
     * Creates the default SSL socket factory.
     * This constructor is used exclusively to instantiate the factory for
     * {@link #getSocketFactory getSocketFactory}.
     * @throws NoSuchAlgorithmException 
     * @throws KeyManagementException 
     */
    private UntrustedSSLSocketFactory() {
        super();
        this.nameResolver = null;
    	TrustManager[] blindTrustMan = new TrustManager[] { new X509TrustManager() {
    		public X509Certificate[] getAcceptedIssuers() { return null; }
    		public void checkClientTrusted(X509Certificate[] c, String a) throws CertificateException { }
    		public void checkServerTrusted(X509Certificate[] c, String a) throws CertificateException { }
    	} };
    	SSLContext sl = null;
    	SSLSocketFactory sslf = null;
    	try {
    		sl = SSLContext.getInstance(TLS);
    		sl.init(null, blindTrustMan, new java.security.SecureRandom());
    		sslf = sl.getSocketFactory();
    	} catch (Exception e) {
    		e.printStackTrace();
    		sslf = HttpsURLConnection.getDefaultSSLSocketFactory();
    	}
    	
    	this.sslcontext = sl;
    	this.socketfactory = sslf;
        
        
    }

    // non-javadoc, see interface org.apache.http.conn.SocketFactory
    public Socket connectSocket(
        final Socket sock,
        final String host,
        final int port,
        final InetAddress localAddress,
        int localPort,
        final HttpParams params
    ) throws IOException {

        if (host == null) {
            throw new IllegalArgumentException("Target host may not be null.");
        }
        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null.");
        }

        SSLSocket sslsock = (SSLSocket)
            ((sock != null) ? sock : createSocket());

        if ((localAddress != null) || (localPort > 0)) {

            // we need to bind explicitly
            if (localPort < 0)
                localPort = 0; // indicates "any"

            InetSocketAddress isa =
                new InetSocketAddress(localAddress, localPort);
            sslsock.bind(isa);
        }

        int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
        int soTimeout = HttpConnectionParams.getSoTimeout(params);

        InetSocketAddress remoteAddress;
        if (this.nameResolver != null) {
            remoteAddress = new InetSocketAddress(this.nameResolver.resolve(host), port); 
        } else {
            remoteAddress = new InetSocketAddress(host, port);            
        }
        try {
            sslsock.connect(remoteAddress, connTimeout);
        } catch (SocketTimeoutException ex) {
            throw new ConnectTimeoutException("Connect to " + remoteAddress + " timed out");
        }
        sslsock.setSoTimeout(soTimeout);
        try {
            hostnameVerifier.verify(host, sslsock);
            // verifyHostName() didn't blowup - good!
        } catch (IOException iox) {
            // close the socket before re-throwing the exception
            try { sslsock.close(); } catch (Exception x) { /*ignore*/ }
            throw iox;
        }

        return sslsock;
    }


    /**
     * Checks whether a socket connection is secure.
     * This factory creates TLS/SSL socket connections
     * which, by default, are considered secure.
     * <br/>
     * Derived classes may override this method to perform
     * runtime checks, for example based on the cypher suite.
     *
     * @param sock      the connected socket
     *
     * @return  <code>true</code>
     *
     * @throws IllegalArgumentException if the argument is invalid
     */
    public boolean isSecure(Socket sock)
        throws IllegalArgumentException {

        if (sock == null) {
            throw new IllegalArgumentException("Socket may not be null.");
        }
        // This instanceof check is in line with createSocket() above.
        if (!(sock instanceof SSLSocket)) {
            throw new IllegalArgumentException
                ("Socket not created by this factory.");
        }
        // This check is performed last since it calls the argument object.
        if (sock.isClosed()) {
            throw new IllegalArgumentException("Socket is closed.");
        }

        return true;

    } // isSecure


    // non-javadoc, see interface LayeredSocketFactory
    public Socket createSocket(
        final Socket socket,
        final String host,
        final int port,
        final boolean autoClose
    ) throws IOException, UnknownHostException {
        SSLSocket sslSocket = (SSLSocket) this.socketfactory.createSocket(
              socket,
              host,
              port,
              autoClose
        );
        hostnameVerifier.verify(host, sslSocket);
        // verifyHostName() didn't blowup - good!
        return sslSocket;
    }

}
