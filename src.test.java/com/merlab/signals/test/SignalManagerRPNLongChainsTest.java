package com.merlab.signals.test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.merlab.signals.core.Signal;
import com.merlab.signals.core.SignalManager;
import com.merlab.signals.core.SignalStack;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SignalManagerRPNLongChainsTest {

    private static final Signal A = new Signal(List.of(1.0, 2.0, 3.0, 4.0));
    private static final Signal B = new Signal(List.of(4.0, 3.0, 2.0, 1.0));
    private static final Signal C = new Signal(List.of(1.0, 1.0, 1.0, 1.0));
    private static final Map<String,Signal> VARS = Map.of("A", A, "B", B, "C", C);

    private final SignalManager mgr = new SignalManager(
        /* provider=*/null,
        new SignalStack(),
        /* db=*/null,
        /* stats=*/false, /* feats=*/false, /* nn=*/false
    );

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("longChainProvider")
    void testLongRPNChains(String name, List<String> tokens) {
        Signal result = assertDoesNotThrow(() ->
            mgr.operateWithRPN(tokens, VARS),
            "Chain threw exception: " + name
        );
        assertEquals(4, result.getValues().size(),
            () -> "Chain `" + name + "` should preserve length");
    }

    static Stream<org.junit.jupiter.params.provider.Arguments> longChainProvider() {
        return Stream.of(
            org.junit.jupiter.params.provider.Arguments.of(
                "add-sub-norm",
                List.of("A", "B", "+", "C", "-", "norm")
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                "add-sub-norm-scale",
                List.of("A", "B", "+", "C", "-", "norm", "2", "*")
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                "scale-add-norm",
                List.of("A", "2", "*", "B", "+", "norm")
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                "scale-add-norm-blend",
                List.of("A","2","*","B","+","norm","C","0.5","blend")
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                "scale-add-norm-blend-clamp",
                List.of("A","2","*","B","+","norm","C","0.5","blend","1","4","clamp")
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                "chain-mult-scale-clamp-norm",
                List.of("A", "B", "+", "C", "*", "2", "*", "1", "3", "clamp", "norm")
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                "chain-movavg-then-mult",
                List.of("A", "2", "movavg", "2", "*")
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                "movavg-add-mult",
                List.of("A", "2", "movavg", "B", "+", "C", "*")
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                "movavg-add-mult-norm",
                List.of("A", "2", "movavg", "B", "+", "C", "*", "norm")
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                "movavg-add-mult-norm-blend",
                List.of("A","2","movavg","B","+","C","*","norm","C","0.25","blend")
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                "movavg-add-mult-norm-blend-clamp",
                List.of("A","2","movavg","B","+","C","*","norm","C","0.25","blend","1","4","clamp")
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                "subtract-clamp-norm",
                List.of("A", "B", "-", "1", "3", "clamp", "norm")
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                "subtract-clamp-norm-scale",
                List.of("A", "B", "-", "1", "3", "clamp", "norm", "2", "*")
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                "add-clamp-norm",
                List.of("A", "B", "+", "1", "4", "clamp", "norm")
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                "add-clamp-norm-subtract",
                List.of("A", "B", "+", "1", "4", "clamp", "norm", "C", "-")
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                "add-clamp-norm-subtract-scale",
                List.of("A", "B", "+", "1", "4", "clamp", "norm", "C", "-", "2", "*")
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                "blend-scale-add",
                List.of("A", "B", "0.3", "blend", "2", "*", "C", "+")
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                "blend-scale-add-norm",
                List.of("A", "B", "0.3", "blend", "2", "*", "C", "+", "norm")
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                "blend-scale-add-norm-movavg",
                List.of("A", "B", "0.3", "blend", "2", "*", "C", "+", "norm", "3", "movavg")
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                "clamp-blend-norm",
                List.of("A", "1", "4", "clamp", "B", "C", "0.5", "blend", "norm")
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                "clamp-blend-norm-scale",
                List.of("A", "1", "4", "clamp", "B", "C", "0.5", "blend", "norm", "2", "*")
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                "movavg-clamp-blend",
                List.of("A", "3", "movavg", "1", "3", "clamp", "B", "0.5", "blend")
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                "norm-movavg-scale",
                List.of("A", "norm", "4", "movavg", "2", "*")
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                "norm-movavg-scale-add",
                List.of("A", "norm", "4", "movavg", "2", "*", "B", "+")
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                "norm-movavg-scale-add-clamp",
                List.of("A", "norm", "4", "movavg", "2", "*", "B", "+", "0", "5", "clamp")
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                "clamp-norm-movavg",
                List.of("A", "2", "3", "clamp", "norm", "3", "movavg")
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                "blend-clamp-norm-movavg",
                List.of("A", "B", "0.7", "blend", "1", "4", "clamp", "norm", "2", "movavg")
            ),
            org.junit.jupiter.params.provider.Arguments.of(
                "blend-clamp-norm-movavg-scale",
                List.of("A", "B", "0.7", "blend", "1", "4", "clamp", "norm", "2", "movavg", "3", "*")
            )
        );
    }
}
