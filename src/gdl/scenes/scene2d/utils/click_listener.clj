(ns gdl.scenes.scene2d.utils.click-listener
  (:require [com.badlogic.gdx.scenes.scene2d.utils.click-listener :as click-listener]))

(defn create [& args]
  (apply click-listener/create args))
