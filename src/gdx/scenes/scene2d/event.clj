(ns gdx.scenes.scene2d.event
  (:require [com.badlogic.gdx.scenes.scene2d.event :as event]))

(defn get-stage [e]
  (event/getStage e))
