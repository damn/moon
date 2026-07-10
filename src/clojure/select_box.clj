(ns clojure.select-box
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.scenes.scene2d.ui.select-box :as select-box]))

(defn get-selected [& args]
  (apply select-box/getSelected args))

(defn new [& args]
  (apply select-box/new args))

(defn set-items! [& args]
  (apply select-box/setItems args))

(defn set-selected! [& args]
  (apply select-box/setSelected args))
