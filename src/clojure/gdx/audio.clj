(ns clojure.gdx.audio
  (:require [com.badlogic.gdx.audio :as audio]))

(defn new-sound [audio file-handle]
  (audio/newSound audio file-handle))
