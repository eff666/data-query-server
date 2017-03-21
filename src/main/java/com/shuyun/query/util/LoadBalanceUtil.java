package com.shuyun.query.util;

import com.google.common.collect.Lists;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;


public class LoadBalanceUtil {

    private static TreeMap<Long, Node> ketamaNodes;
    private static HashAlgorithm hashAlg = HashAlgorithm.KETAMA_HASH;

    public static void produceMoreNodes(List<Node> nodes, int nodeCopies) {
        ketamaNodes=new TreeMap<Long, Node>();

        for (Node node : nodes) {
            for (int i = 0; i < nodeCopies / 4; i++) {
                byte[] digest = hashAlg.computeMd5(node.getName() + i);
                for(int h = 0; h < 4; h++) {
                    long m = hashAlg.hash(digest, h);

                    ketamaNodes.put(m, node);
                }
            }
        }
    }

    public static void produceMoreNodes(List<String> names) {
        List<Node> nodes = Lists.newArrayList();
        for (String name : names){
            nodes.add(new Node(name));
        }
        produceMoreNodes(nodes, 10);
    }

    public static Node getPrimary(final String k) {
        byte[] digest = hashAlg.computeMd5(k);
        Node rv = getNodeForKey(hashAlg.hash(digest, 0));
        return rv;
    }

    private static Node getNodeForKey(long hash) {
        final Node rv;
        Long key = hash;
        if(!ketamaNodes.containsKey(key)) {
            SortedMap<Long, Node> tailMap=ketamaNodes.tailMap(key);
            if(tailMap.isEmpty()) {
                key=ketamaNodes.firstKey();
            } else {
                key=tailMap.firstKey();
            }
        }


        rv=ketamaNodes.get(key);
        return rv;
    }


    private enum HashAlgorithm {

        /**
         * MD5-based hash algorithm used by ketama.
         */
        KETAMA_HASH;

        public long hash(byte[] digest, int nTime) {
            long rv = ((long) (digest[3+nTime*4] & 0xFF) << 24)
                    | ((long) (digest[2+nTime*4] & 0xFF) << 16)
                    | ((long) (digest[1+nTime*4] & 0xFF) << 8)
                    | (digest[0+nTime*4] & 0xFF);

            return rv & 0xffffffffL; /* Truncate to 32-bits */
        }

        /**
         * Get the md5 of the given key.
         */
        public byte[] computeMd5(String k) {
            MessageDigest md5;
            try {
                md5 = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("MD5 not supported", e);
            }
            md5.reset();
            byte[] keyBytes = null;
            try {
                keyBytes = k.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Unknown string :" + k, e);
            }

            md5.update(keyBytes);
            return md5.digest();
        }
    }
}
