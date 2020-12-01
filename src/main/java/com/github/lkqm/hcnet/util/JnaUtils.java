package com.github.lkqm.hcnet.util;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import java.nio.ByteBuffer;
import lombok.experimental.UtilityClass;

/**
 * Jna相关工具类.
 */
@UtilityClass
public class JnaUtils {

    public static byte[] pointerToBytes(Pointer pointer, int length) {
        byte[] faceBytes = new byte[length];
        ByteBuffer buffers = pointer.getByteBuffer(0, length);
        buffers.rewind();
        buffers.get(faceBytes);
        return faceBytes;
    }

    public static void pointerToStructure(Pointer pointer, Structure target) {
        target.write();
        target.getPointer().write(0, pointer.getByteArray(0, target.size()), 0, target.size());
        target.read();
    }

}
