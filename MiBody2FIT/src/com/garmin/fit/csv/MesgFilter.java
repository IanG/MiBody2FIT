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


package com.garmin.fit.csv;

import com.garmin.fit.*;
import java.util.ArrayList;

/**
 * Listens to incoming mesg definitions and data messages and filters
 * them by name - Once filtered the messages are passed along to whoever
 * is listening.
 * 
 */
public class MesgFilter implements MesgListener, MesgDefinitionListener {
   private ArrayList<MesgListener> mesgListeners = new ArrayList<MesgListener>();
   private ArrayList<MesgDefinitionListener> mesgDefListeners = new ArrayList<MesgDefinitionListener>();
   private ArrayList<String> mesgDefinitionsToOutput;
   private ArrayList<String> mesgToOutput;
   private boolean outputMesgDefinitions;
   private boolean outputMesg;

   public MesgFilter() {
      this.outputMesgDefinitions = true;
      this.outputMesg = true;
   }

   public void addListener(MesgListener mesgListener) {
      if ((mesgListener != null) && !mesgListeners.contains(mesgListener))
         mesgListeners.add(mesgListener);      
   }
   
   public void addListener(MesgDefinitionListener mesgDefinitionListener) {
      if ((mesgDefinitionListener != null) && !mesgDefListeners.contains(mesgDefinitionListener))
         mesgDefListeners.add(mesgDefinitionListener);      
   }

   public void setMesgDefinitionsToOutput(ArrayList<String> inputMesgDefinitionsToOutput) {
      this.mesgDefinitionsToOutput = inputMesgDefinitionsToOutput;
      for(String string : this.mesgDefinitionsToOutput) {
         if(string.matches("^none$")) {
            this.outputMesgDefinitions = false;
         }
      }
   }

   public void setDataMessagesToOutput(ArrayList<String> inputMesgToOutput) {
      this.mesgToOutput = inputMesgToOutput;
      for(String string : this.mesgToOutput) {
         if(string.matches("^none$")) {
            this.outputMesg = false;
         }
      }
   }

   public void onMesgDefinition(MesgDefinition mesgDef) {
      boolean outputDefinition = true;
      Mesg mesg = Factory.createMesg(mesgDef.getNum());

      if(!this.outputMesgDefinitions) {
            outputDefinition = false;
      }
      else if(!this.mesgDefinitionsToOutput.isEmpty()) {
         outputDefinition = false;
         for(String string : this.mesgDefinitionsToOutput) {
            if(string.matches("^" + mesg.getName() + "$")){
               outputDefinition = true;
            }
         }
      }

      if(outputDefinition) {

         for (MesgDefinitionListener mesgListener : mesgDefListeners)
            mesgListener.onMesgDefinition(mesgDef);
      }
   }

   public void onMesg(Mesg mesg) {
      boolean outputMessage = true;
      if(!this.outputMesg) {
            outputMessage = false;
      }
      else if(!this.mesgToOutput.isEmpty()) {
         outputMessage = false;
         for(String string : this.mesgToOutput) {
            if(string.matches("^" + mesg.getName() + "$")){
               outputMessage = true;
            }
         }
      }

      if(outputMessage) {
         for (MesgListener mesgListener : mesgListeners)
            mesgListener.onMesg(mesg);
      }
   }
}
