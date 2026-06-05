(ns clojure.scene2d.stage.add-actor
  (:import (com.badlogic.gdx.scenes.scene2d Stage)))

(defn add-actor! [^Stage stage actor]
  (.addActor stage actor))
