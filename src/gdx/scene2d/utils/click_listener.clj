(ns gdx.scene2d.utils.click-listener
  (:require [com.badlogic.gdx.scenes.scene2d.utils.click-listener :as click-listener]))

(defn create [f]
  (click-listener/new f))
