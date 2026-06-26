(ns scene2d.utils.change-listener
  (:require [com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]))

(defn create [f]
  (change-listener/proxy-change-listener f))
