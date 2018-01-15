////////////////////////////////////////////////////////////////////////////////
// The following FIT Protocol software provided may be used with FIT protocol
// devices only and remains the copyrighted property of Dynastream Innovations Inc.
// The software is being provided on an "as-is" basis and as an accommodation,
// and therefore all warranties, representations, or guarantees of any kind
// (whether express, implied or statutory) including, without limitation,
// warranties of merchantability, non-infringement, or fitness for a particular
// purpose, are specifically disclaimed.
//
// Copyright 2014 Dynastream Innovations Inc.
////////////////////////////////////////////////////////////////////////////////
// ****WARNING****  This file is auto-generated!  Do NOT edit this file.
// Profile Version = 10.2Release
// Tag = $Name: AKW10_020 $
////////////////////////////////////////////////////////////////////////////////


package com.garmin.fit;

public class WorkoutCapabilities {
   public static final long INTERVAL = 0x00000001;
   public static final long CUSTOM = 0x00000002;
   public static final long FITNESS_EQUIPMENT = 0x00000004;
   public static final long FIRSTBEAT = 0x00000008;
   public static final long NEW_LEAF = 0x00000010;
   public static final long TCX = 0x00000020; // For backwards compatibility.  Watch should add missing id fields then clear flag.
   public static final long SPEED = 0x00000080; // Speed source required for workout step.
   public static final long HEART_RATE = 0x00000100; // Heart rate source required for workout step.
   public static final long DISTANCE = 0x00000200; // Distance source required for workout step.
   public static final long CADENCE = 0x00000400; // Cadence source required for workout step.
   public static final long POWER = 0x00000800; // Power source required for workout step.
   public static final long GRADE = 0x00001000; // Grade source required for workout step.
   public static final long RESISTANCE = 0x00002000; // Resistance source required for workout step.
   public static final long PROTECTED = 0x00004000;
   public static final long INVALID = Fit.UINT32Z_INVALID;








}
