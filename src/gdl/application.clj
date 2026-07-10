(ns gdl.application
  (:require [com.badlogic.gdx.application :as application]))

; TODO pass data?
; as one ???

(defn get-audio [application]
  (application/getAudio application))

(defn get-files [application]
  (application/getFiles application))

(defn get-graphics [application]
  (application/getGraphics application))

(defn get-input [application]
  (application/getInput application))

(defn post-runnable! [application f]
  (application/postRunnable application f))
