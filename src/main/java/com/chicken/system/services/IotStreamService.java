package com.chicken.system.services;

import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/** SSE hub for IoT readings (resilient to client disconnects) */
@Service
public class IotStreamService {

    private static final long SSE_TIMEOUT_MS = 0L; // never time out server-side
    private final Set<SseEmitter> clients = new CopyOnWriteArraySet<>();

    /**
     * Client subscribes; optionally push an initial snapshot payload (JSON).
     * @param firstPayloadJson JSON string to send immediately (may be null)
     */
    public SseEmitter subscribe(String firstPayloadJson) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);
        clients.add(emitter);

        emitter.onCompletion(() -> clients.remove(emitter));
        emitter.onTimeout(()   -> clients.remove(emitter));
        emitter.onError(e      -> clients.remove(emitter));

        try {
            // tiny comment & reconnect hint (helps proxies and client recovery)
            emitter.send(SseEmitter.event()
                    .comment("connected " + Instant.now())
                    .reconnectTime(5000));
            if (firstPayloadJson != null) {
                emitter.send(SseEmitter.event()
                        .name("reading")
                        .data(firstPayloadJson, MediaType.APPLICATION_JSON));
            }
        } catch (IOException | IllegalStateException ex) {
            emitter.complete();
            clients.remove(emitter);
        }
        return emitter;
    }

    /** Broadcast a new reading JSON to all connected clients. */
    public void push(String readingJson) {
        Iterator<SseEmitter> it = clients.iterator();
        while (it.hasNext()) {
            SseEmitter em = it.next();
            try {
                em.send(SseEmitter.event()
                        .name("reading")
                        .data(readingJson, MediaType.APPLICATION_JSON));
            } catch (IOException | IllegalStateException ex) {
                // client closed / emitter completed -> prune
                em.complete();
                it.remove();
            }
        }
    }

    /** Keep-alive pings so idle proxies don’t drop the stream. */
    @Scheduled(fixedDelay = 15000)
    public void heartbeat() {
        Iterator<SseEmitter> it = clients.iterator();
        while (it.hasNext()) {
            SseEmitter em = it.next();
            try {
                em.send(SseEmitter.event().name("ping").comment("hb"));
            } catch (IOException | IllegalStateException ex) {
                em.complete();
                it.remove();
            }
        }
    }
}
