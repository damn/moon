(ns gdx.change-listener
  (:require [com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]))

(defn create [f]
  (change-listener/create f))
