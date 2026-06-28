(ns scene2d.stage.add-actor
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (scene2d Stage)))

(defn add-actor! [^Stage stage ^Actor actor]
  (.addActor stage actor))
