(ns gdl.scenes.scene2d.ui.widget
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.scenes.scene2d.ui.widget :as widget]))

(defn new [& args]
  (apply widget/new args))
