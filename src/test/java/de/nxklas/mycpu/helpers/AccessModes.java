package de.nxklas.mycpu.helpers;

import de.nxklas.mycpu.core.AccessMode;

public interface AccessModes {
    byte IMM_TO_REG = AccessMode.encode(AccessMode.REGISTER, AccessMode.IMMEDIATE);
    byte REG_TO_REG = AccessMode.encode(AccessMode.REGISTER, AccessMode.REGISTER);
}
