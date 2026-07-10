(ns gdl.scenes.scene2d.event
  (:require [com.badlogic.gdx.scenes.scene2d.event :as event]))

(defn get-stage [& args]
  (apply event/getStage args))
