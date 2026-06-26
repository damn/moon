(ns scene2d.event.get-stage
  (:require [com.badlogic.gdx.scenes.scene2d.event :as event]))

(defn f [event]
  (event/get-stage event))
