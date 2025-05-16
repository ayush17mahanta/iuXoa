package com.iuxoa.datadrop;

import java.nio.ByteBuffer;

public class DNSParser {
    public static String parse(byte[] data, int length) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(data, 0, length);
            buffer.position(28); // skip IP + UDP header
            int domainLength = buffer.get() & 0xFF;

            StringBuilder domain = new StringBuilder();
            while (domainLength > 0) {
                byte[] part = new byte[domainLength];
                buffer.get(part);
                domain.append(new String(part)).append('.');
                domainLength = buffer.get() & 0xFF;
            }
            if (domain.length() > 0) domain.setLength(domain.length() - 1); // remove trailing dot
            return domain.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
