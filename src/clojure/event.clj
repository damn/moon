(ns clojure.event
  (:require [com.badlogic.gdx.scenes.scene2d.event :as event]))

(defn get-stage [event]
  (event/getStage event))
