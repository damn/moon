(ns gdl.scenes.scene2d.utils.change-listener
  (:require [com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]))

(defn create [& args]
  (apply change-listener/create args))
