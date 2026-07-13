(ns clojure.gdx.scenes.scene2d.utils.click-listener
  (:require [com.badlogic.gdx.scenes.scene2d.utils.click-listener :as click-listener]))

(defn create [clicked-fn]
  (click-listener/create clicked-fn))
