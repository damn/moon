(ns gdl.scenes.scene2d.ui.stack
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.scenes.scene2d.ui.stack :as stack]))

(defn new [& args]
  (apply stack/new args))
