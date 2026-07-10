(ns gdl.audio
  (:require [com.badlogic.gdx.audio :as audio]))

(defn new-sound [& args]
  (apply audio/newSound args))
