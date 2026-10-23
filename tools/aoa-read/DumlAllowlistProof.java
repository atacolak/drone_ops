package sh.colak.dji.aoaread;

/** Host proof: Duml.build allowlists keepalive 0x06/0x77 only. */
public final class DumlAllowlistProof {
    private DumlAllowlistProof() {}

    public static void main(String[] args) {
        byte[] ka = Duml.keepalive(0x06, 1);
        if (ka == null || ka.length < 8) {
            fail("keepalive dst 0x06 should build");
        }
        if ((ka[0] & 0xff) != 0x55 || (ka[1] & 0xff) != 0xCC
                || (ka[2] & 0xff) != 0x49 || (ka[3] & 0xff) != 0x57) {
            fail("keepalive should stay RCLink-wrapped");
        }
        Duml.keepalive(0x0E, 2);

        byte[] inner = Duml.build(0x02, 0x06, 0x40, 0x06, 0x77, new byte[] {
                0x01, 0x01, 0x00, (byte) 0xFF, (byte) 0xFF, 0x20, 0x00, 0x00
        }, 1);
        if (inner == null || (inner[0] & 0xff) != 0x55) {
            fail("keepalive cmd_set/cmd_id should build");
        }
        if ((inner[9] & 0xff) != 0x06 || (inner[10] & 0xff) != 0x77) {
            fail("built keepalive should carry 0x06/0x77");
        }

        expectRejected(0x03, 0x8E);
        expectRejected(0x03, 0x2A);
        expectRejected(0x00, 0x00);
        expectRejected(0x06, 0x05);
        expectRejected(0x09, 0x43);
        expectRejected(0x01, 0x0A);
        System.out.println("ok");
    }

    private static void expectRejected(int cmdSet, int cmdId) {
        try {
            Duml.build(0x02, 0x06, 0x40, cmdSet, cmdId, new byte[0], 1);
            fail(String.format("expected reject for 0x%02X/0x%02X", cmdSet, cmdId));
        } catch (IllegalArgumentException e) {
            // fail-closed
        }
    }

    private static void fail(String msg) {
        System.err.println(msg);
        System.exit(1);
    }
}
