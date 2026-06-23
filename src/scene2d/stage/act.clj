(ns scene2d.stage.act
  (:import (com.badlogic.gdx.scenes.scene2d Stage)))

(defn act! [^Stage stage]
  (.act stage))
