(ns audio.new-sound
  (:require [com.badlogic.gdx.audio :as audio]))

(defn f [audio file-handle]
  (audio/new-sound audio file-handle))
