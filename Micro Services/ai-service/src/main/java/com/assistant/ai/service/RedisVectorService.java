package com.assistant.ai.service;

import com.assistant.ai.dto.SearchResult;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.commands.ProtocolCommand;

import redis.clients.jedis.util.SafeEncoder;

import java.nio.ByteBuffer;
import java.util.*;

@Service
public class RedisVectorService {

    // Adjust names to match your FT.CREATE schema
    private static final String INDEX = "idx:embeddings";
    private static final String VECTOR_FIELD = "vector";   // vector field name in schema
    private static final String CONTENT_FIELD = "content"; // text field name in schema

    // Define ProtocolCommand for FT.SEARCH
    private static final ProtocolCommand FT_SEARCH = new ProtocolCommand() {
        private final byte[] raw = SafeEncoder.encode("FT.SEARCH");
        @Override public byte[] getRaw() { return raw; }
    };

    // Create index once externally, e.g.:
    // FT.CREATE idx:embeddings ON HASH PREFIX 1 doc: SCHEMA
    //   content TEXT
    //   vector VECTOR HNSW 12 TYPE FLOAT32 DIM 768 DISTANCE_METRIC COSINE
    // Ensure DIM and metric match your embeddings. [Docs reference]

    // Save: stores a binary FLOAT32 vector and a string payload in a hash
    public String saveEmbedding(List<Float> embedding, String content) {
        String docId = "doc:" + UUID.randomUUID();
        byte[] vec = toFloat32Bytes(embedding);
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            // HSET with binary on the vector field
            jedis.hset(SafeEncoder.encode(docId), SafeEncoder.encode(VECTOR_FIELD), vec);
            jedis.hset(docId, CONTENT_FIELD, content);
        }
        return docId;
    }

    // Top-1 nearest neighbor using RediSearch KNN
    public Optional<SearchResult> top1Similar(List<Float> queryEmbedding) {
        byte[] q = toFloat32Bytes(queryEmbedding);
        // FT.SEARCH idx "*=>[KNN 1 @vector $vec AS score]"
        //   PARAMS 2 vec <bytes>
        //   SORTBY score ASC
        //   RETURN 2 score content
        //   DIALECT 2
        byte[][] args = new byte[][]{
                SafeEncoder.encode(INDEX),
                SafeEncoder.encode("*=>[KNN 1 @" + VECTOR_FIELD + " $vec AS score]"),
                SafeEncoder.encode("PARAMS"),
                SafeEncoder.encode("2"),
                SafeEncoder.encode("vec"),
                q,
                SafeEncoder.encode("SORTBY"),
                SafeEncoder.encode("score"),
                SafeEncoder.encode("ASC"),
                SafeEncoder.encode("RETURN"),
                SafeEncoder.encode("2"),
                SafeEncoder.encode("score"),
                SafeEncoder.encode(CONTENT_FIELD),
                SafeEncoder.encode("DIALECT"),
                SafeEncoder.encode("2")
        };

        try (Jedis jedis = new Jedis("localhost", 6379)) {
            @SuppressWarnings("unchecked")
            List<Object> resp = (List<Object>) jedis.sendCommand(FT_SEARCH, args);
            return parseTop1(resp);
        }
    }

    private Optional<SearchResult> parseTop1(List<Object> resp) {
        // RESP layout: [total, key1, [field, value, ...], key2, [..], ...]
        if (resp == null || resp.size() < 2) return Optional.empty();
        long total = (resp.get(0) instanceof Long) ? (Long) resp.get(0) : 0L;
        if (total == 0L) return Optional.empty();

        String id = asString(resp.get(1));
        @SuppressWarnings("unchecked")
        List<Object> fields = (List<Object>) resp.get(2);

        String content = null;
        Double score = null;
        for (int i = 0; i + 1 < fields.size(); i += 2) {
            String fname = asString(fields.get(i));
            Object fval = fields.get(i + 1);
            if (CONTENT_FIELD.equals(fname)) {
                content = asString(fval);
            } else if ("score".equals(fname)) {
                String sval = asString(fval);
                try { score = Double.parseDouble(sval); } catch (Exception ignore) {}
            }
        }
        return Optional.of(new SearchResult(id, content, score != null ? score : 0.0));
    }

    private String asString(Object o) {
        if (o == null) return null;
        if (o instanceof byte[]) return SafeEncoder.encode((byte[]) o);
        return String.valueOf(o);
    }

    private byte[] toFloat32Bytes(List<Float> v) {
        ByteBuffer buf = ByteBuffer.allocate(v.size() * 4);
        for (Float f : v) buf.putFloat(f);
        return buf.array();
    }

}
